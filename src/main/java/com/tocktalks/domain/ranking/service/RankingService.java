package com.tocktalks.domain.ranking.service;

import com.tocktalks.domain.ranking.dto.response.RankingArchiveResponse;
import com.tocktalks.domain.ranking.dto.response.RankingDto;
import com.tocktalks.domain.ranking.dto.response.RankingListResponse;
import com.tocktalks.domain.ranking.dto.response.RankingUpdateEvent;
import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import com.tocktalks.domain.ranking.repository.RoomRankingArchiveRepository;
import com.tocktalks.domain.ranking.type.RankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRedisService rankingRedisService;
    private final RankingPublisher rankingPublisher;
    private final RoomRankingArchiveRepository archiveRepository;

    public void updateRanking(Long roomId, Long memberId, Long finalAsset, Long seedMoney) {
        BigDecimal returnRate = ReturnRateCalculator.calculate(finalAsset, seedMoney);

        Integer prevRank = rankingRedisService.getRank(roomId, memberId, RankingType.RETURN_RATE);
        rankingRedisService.updateRanking(roomId, memberId, returnRate, finalAsset);
        Integer newRank = rankingRedisService.getRank(roomId, memberId, RankingType.RETURN_RATE);

        if (!Objects.equals(prevRank, newRank)) {
            rankingPublisher.publish(roomId, new RankingUpdateEvent(memberId, newRank));
        }
    }

    public RankingListResponse getRanking(Long roomId, Long memberId, RankingType type, int topN){
        List<RankingDto> top = rankingRedisService.getTopN(roomId, type, topN);
        RankingDto my = rankingRedisService.getMyRank(roomId, memberId, type);
        return new RankingListResponse(top, my);
    }

    @Transactional
    public void finalizeRanking(Long roomId){
        if(archiveRepository.existsByRoomId(roomId)) return;

        List<RankingDto> finalRanking = rankingRedisService.getAll(roomId, RankingType.RETURN_RATE);
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
    }

    public List<RankingArchiveResponse> getFinalRanking(Long roomId, RankingType type){
        List<RoomRankingArchive> archives = switch(type){
            case RETURN_RATE -> archiveRepository.findByRoomIdOrderByFinalRankAsc(roomId);
            case TOTAL_ASSET -> archiveRepository.findByRoomIdOrderByFinalAssetDesc(roomId);
        };
        return archives.stream().map(RankingArchiveResponse::from).toList();
    }


}
