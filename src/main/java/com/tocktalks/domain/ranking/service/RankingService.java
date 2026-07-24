package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.ranking.dto.response.*;
import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import com.tocktalks.domain.ranking.repository.RoomRankingArchiveRepository;
import com.tocktalks.domain.ranking.type.RankingType;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRedisService rankingRedisService;
    private final RankingPublisher rankingPublisher;
    private final RoomRankingArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final TransactionRepository transactionRepository;

    public void updateRanking(Long roomId, Long memberId, Long finalAsset, Long seedMoney) {
        BigDecimal returnRate = ReturnRateCalculator.calculate(finalAsset, seedMoney);

        rankingRedisService.updateRanking(roomId, memberId, returnRate, finalAsset);
    }

    public Map<Long, Boolean> getHasTradedByMemberId(Long roomId){
        List<RoomParticipant> participants = roomParticipantRepository.findByRoomIdAndStatus(roomId, "ACTIVE");
        if(participants.isEmpty()) return Map.of();

        Set<Long> tradedParticipantIds = new HashSet<>(transactionRepository.findDistinctRoomParticipantIdsIn(
                participants.stream().map(RoomParticipant::getId).toList()));

        return participants.stream().collect(Collectors.toMap(RoomParticipant::getMemberId,
                p -> tradedParticipantIds.contains(p.getId())));
    }

    public void broadcastRanking(Long roomId){
        List<RankingDto> live = rankingRedisService.getAll(roomId, RankingType.TOTAL_ASSET);
        if(live.isEmpty()) return;

        Map<Long, String> nicknameByMemberId = memberRepository.findAllById(live.stream().map(RankingDto::memberId).toList())
                .stream().collect(Collectors.toMap(Member::getId, Member::getNickname));

        Map<Long, Boolean> hasTradedByMemberId = getHasTradedByMemberId(roomId);

        List<RankingMemberDto> ranking = live.stream()
                .map(dto -> new RankingMemberDto(
                        dto.rank(),
                        dto.memberId(),
                        nicknameByMemberId.get(dto.memberId()),
                        dto.score().longValue(),
                        hasTradedByMemberId.getOrDefault(dto.memberId(), false))).toList();
        rankingPublisher.publish(roomId, new RankingBroadcastEvent(ranking));
    }


    public RankingListResponse getRanking(Long roomId, Long memberId, RankingType type, int topN){
        List<RankingDto> top = rankingRedisService.getTopN(roomId, type, topN);
        RankingDto my = rankingRedisService.getMyRank(roomId, memberId, type);
        return new RankingListResponse(top, my);
    }

    // Redis에 데이터가 없으면(트레이드가 없었던 방 등) 빈 리스트를 반환한다.
    // RoomService가 이 경우 현금 잔고 기반으로 폴백 처리한다.
    public List<RankingDto> getAllRanking(Long roomId, RankingType type){
        return rankingRedisService.getAll(roomId, type);
    }

    // Redis에 실시간 랭킹 데이터가 있으면 그걸로 아카이브하고 저장된 목록을 반환한다.
    // 이미 아카이브된 방이거나 Redis에 데이터가 없으면(트레이드가 없었던 방 등) 빈 리스트를 반환하며,
    // 호출부(RoomService)가 후자의 경우 현금 잔고 기반으로 폴백 처리한다.
    @Transactional
    public List<RoomRankingArchive> finalizeRanking(Long roomId){
        if(archiveRepository.existsByRoomId(roomId)) return List.of();

        List<RankingDto> finalRanking = rankingRedisService.getAll(roomId, RankingType.RETURN_RATE);
        if(finalRanking.isEmpty()) return List.of();

        List<RoomRankingArchive> archives = finalRanking.stream()
                .map(dto -> {
                    Long finalAsset = rankingRedisService
                            .getScore(roomId, dto.memberId(), RankingType.TOTAL_ASSET)
                            .longValue();
                    return RoomRankingArchive.of(
                            roomId,
                            dto.memberId(),
                            finalAsset,
                            BigDecimal.valueOf(dto.score()),
                            dto.rank()
                    );
                })
                .toList();

        archiveRepository.saveAll(archives);
        rankingRedisService.clear(roomId);
        return archives;
    }

    public List<RankingArchiveResponse> getFinalRanking(Long roomId, RankingType type){
        List<RoomRankingArchive> archives = switch(type){
            case RETURN_RATE -> archiveRepository.findByRoomIdOrderByFinalRankAsc(roomId);
            case TOTAL_ASSET -> archiveRepository.findByRoomIdOrderByFinalAssetDesc(roomId);
        };

        Map<Long, String> nicknameByMemberId = memberRepository
                .findAllById(archives.stream().map(RoomRankingArchive::getMemberId).toList()).stream()
                .collect(Collectors.toMap(Member::getId, Member::getNickname));

        return archives.stream()
                .map(archive -> RankingArchiveResponse.from(archive, nicknameByMemberId.get(archive.getMemberId())))
                .toList();
    }


}
