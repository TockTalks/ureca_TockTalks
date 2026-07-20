package com.tocktalks.domain.room.service;

import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import com.tocktalks.domain.ranking.repository.RoomRankingArchiveRepository;
import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.dto.CreateRoomRequest;
import com.tocktalks.domain.room.dto.RoomParticipantResponse;
import com.tocktalks.domain.room.dto.RoomRankingResponse;
import com.tocktalks.domain.room.dto.RoomResponse;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.global.config.RoomProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RoomService {

    private static final String STATUS_ONGOING = "ongoing";
    private static final String PARTICIPANT_ACTIVE = "ACTIVE";

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final RoomRankingArchiveRepository roomRankingArchiveRepository;
    private final MemberRepository memberRepository;
    private final RoomProperties roomProperties;
    private final RankingService rankingService;

    @Transactional
    public RoomResponse createRoom(Long ownerId, CreateRoomRequest request) {
        if (!request.endAt().isAfter(request.startAt())) {
            throw new IllegalArgumentException("종료일은 시작일 이후여야 합니다.");
        }

        Long seedMoney = request.seedMoney() != null ? request.seedMoney() : roomProperties.getDefaultSeedMoney();
        Room room = roomRepository.save(Room.createPrivate(
                ownerId, request.name(), request.isPublic(), seedMoney,
                request.startAt(), request.endAt(), request.maxParticipants()));

        joinRoom(room, ownerId);
        return RoomResponse.of(room, 1);
    }

    @Transactional
    public void joinDefaultRoom(Long memberId) {
        joinRoom(getOrCreateDefaultRoom(), memberId);
    }

    @Transactional
    public RoomParticipantResponse joinRoomById(Long roomId, Long memberId) {
        Room room = getRoom(roomId);
        if (room.isDefault()) {
            throw new IllegalArgumentException("기본방은 이미 모두 참가되어 있는 방입니다.");
        }
        if (!STATUS_ONGOING.equals(room.getStatus())) {
            throw new IllegalArgumentException("참가할 수 없는 방입니다.");
        }
        if (!room.isPublic()) {
            throw new IllegalArgumentException("비공개 방은 초대코드로 참가해야 합니다.");
        }
        return RoomParticipantResponse.of(joinRoom(room, memberId));
    }

    @Transactional
    public RoomParticipantResponse joinRoomByInviteCode(String inviteCode, Long memberId) {
        Room room = roomRepository.findByInviteCode(inviteCode)
                .filter(r -> STATUS_ONGOING.equals(r.getStatus()))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대코드입니다."));
        return RoomParticipantResponse.of(joinRoom(room, memberId));
    }

    @Transactional
    public void leaveRoom(Long roomId, Long memberId) {
        Room room = getRoom(roomId);
        if (room.isDefault()) {
            throw new IllegalArgumentException("기본방은 탈퇴할 수 없습니다.");
        }
        RoomParticipant participant = roomParticipantRepository
                .findByRoomIdAndMemberIdAndStatus(roomId, memberId, PARTICIPANT_ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("참가 중인 방이 아닙니다."));
        participant.end();
    }

    public RoomResponse getRoomDetail(Long roomId, Long requesterId) {
        Room room = getRoom(roomId);
        boolean isParticipant = requesterId != null && roomParticipantRepository
                .findByRoomIdAndMemberIdAndStatus(roomId, requesterId, PARTICIPANT_ACTIVE)
                .isPresent();
        long participantCount = roomParticipantRepository.countByRoomIdAndStatus(roomId, PARTICIPANT_ACTIVE);
        return RoomResponse.of(room, participantCount, isParticipant);
    }

    public List<RoomResponse> getPublicRooms() {
        // 기본방은 가입 시 자동 참가되는 방이라 목록에서 별도로 참가할 대상이 아니다
        return roomRepository.findByIsPublicTrueAndIsDefaultFalseAndStatus(STATUS_ONGOING).stream()
                .map(room -> RoomResponse.of(room,
                        roomParticipantRepository.countByRoomIdAndStatus(room.getId(), PARTICIPANT_ACTIVE)))
                .toList();
    }

    public RoomResponse getDefaultRoom() {
        Room room = getOrCreateDefaultRoom();
        return RoomResponse.of(room, roomParticipantRepository.countByRoomIdAndStatus(room.getId(), PARTICIPANT_ACTIVE));
    }

    public List<RoomRankingResponse> getRanking(Long roomId) {
        List<RoomParticipant> ranked = roomParticipantRepository
                .findByRoomIdAndStatus(roomId, PARTICIPANT_ACTIVE).stream()
                .sorted(Comparator.comparing(RoomParticipant::getBalance).reversed()
                        .thenComparing(RoomParticipant::getMemberId))
                .toList();

        Map<Long, String> nicknameByMemberId = memberRepository
                .findAllById(ranked.stream().map(RoomParticipant::getMemberId).toList()).stream()
                .collect(Collectors.toMap(Member::getId, Member::getNickname));

        return IntStream.range(0, ranked.size())
                .mapToObj(i -> {
                    RoomParticipant participant = ranked.get(i);
                    return new RoomRankingResponse(
                            i + 1,
                            participant.getMemberId(),
                            nicknameByMemberId.get(participant.getMemberId()),
                            participant.getBalance());
                })
                .toList();
    }

    public List<RoomResponse> getMyRooms(Long memberId) {
        return roomParticipantRepository.findByMemberIdAndStatus(memberId, PARTICIPANT_ACTIVE).stream()
                .map(participant -> getRoom(participant.getRoomId()))
                .map(room -> RoomResponse.of(room,
                        roomParticipantRepository.countByRoomIdAndStatus(room.getId(), PARTICIPANT_ACTIVE)))
                .toList();
    }

    // 5분마다 종료 시각이 지난 방을 닫고 최종 랭킹을 archive 한다
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void closeExpiredRooms() {
        List<Room> expiredRooms = roomRepository.findByStatusAndEndAtBefore(STATUS_ONGOING, LocalDateTime.now());
        expiredRooms.forEach(this::archiveAndClose);
    }

    private void archiveAndClose(Room room) {
        List<RoomParticipant> participants = roomParticipantRepository
                .findByRoomIdAndStatus(room.getId(), PARTICIPANT_ACTIVE);

        if (!roomRankingArchiveRepository.existsByRoomId(room.getId())
                && rankingService.finalizeRanking(room.getId()).isEmpty()) {
            // Redis에 실시간 랭킹 데이터가 없는 방(트레이드가 아직 없는 지금 같은 경우)은
            // 현금 잔고 기준으로 폴백 계산한다. trade 도메인이 붙어 RankingService.updateRanking()이
            // 호출되기 시작하면 이 폴백 없이 실시간 랭킹 데이터로 자동 전환된다.
            archiveFromCashBalance(room.getId(), participants);
        }

        participants.forEach(RoomParticipant::end);
        room.close();
    }

    private void archiveFromCashBalance(Long roomId, List<RoomParticipant> participants) {
        List<RoomParticipant> ranked = participants.stream()
                .sorted(Comparator.comparing(RoomParticipant::getBalance).reversed()
                        .thenComparing(RoomParticipant::getMemberId))
                .toList();

        for (int i = 0; i < ranked.size(); i++) {
            RoomParticipant participant = ranked.get(i);
            // NOTE: 아직 trade/price 도메인이 없어 보유 종목 평가액을 반영하지 못한다.
            // 지금은 현금 잔고(balance)를 최종 자산으로 취급하고, 매수/매도 기능이 붙으면 확장해야 한다.
            Long finalAsset = participant.getBalance();
            BigDecimal finalReturnRate = BigDecimal.valueOf(finalAsset - participant.getInitialSeedMoney())
                    .divide(BigDecimal.valueOf(participant.getInitialSeedMoney()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            roomRankingArchiveRepository.save(RoomRankingArchive.of(
                    roomId, participant.getMemberId(), finalAsset, finalReturnRate, i + 1));
        }
    }

    private RoomParticipant joinRoom(Room room, Long memberId) {
        roomParticipantRepository.findByRoomIdAndMemberIdAndStatus(room.getId(), memberId, PARTICIPANT_ACTIVE)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("이미 참가 중인 방입니다.");
                });
        if (room.getMaxParticipants() != null
                && roomParticipantRepository.countByRoomIdAndStatus(room.getId(), PARTICIPANT_ACTIVE) >= room.getMaxParticipants()) {
            throw new IllegalArgumentException("정원이 가득 찼습니다.");
        }
        return roomParticipantRepository.save(RoomParticipant.join(room.getId(), memberId, room.getSeedMoney()));
    }

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }

    private Room getOrCreateDefaultRoom() {
        return roomRepository.findByIsDefaultTrue()
                .orElseGet(() -> roomRepository.save(Room.createDefault(roomProperties.getDefaultSeedMoney())));
    }
}
