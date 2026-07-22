package com.tocktalks.domain.room.service;

import com.tocktalks.domain.member.entity.Member;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.ranking.dto.response.RankingDto;
import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.ranking.type.RankingType;
import com.tocktalks.domain.trade.service.TradeRankingService;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoomService {

    private static final String STATUS_ONGOING = "ongoing";
    private static final String PARTICIPANT_ACTIVE = "ACTIVE";

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final TradeRankingService tradeRankingService;
    private final RankingService rankingService;
    private final MemberRepository memberRepository;
    private final RoomProperties roomProperties;

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

        // 나가는 시점의 총자산(현금+보유종목 평가액)을 Redis 랭킹에 확정 반영한다.
        // 한 번 나가면 이 방에 다시 못 들어오므로, 이 값이 그 사람의 최종 결과로 남는다.
        tradeRankingService.updateRanking(participant);

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

    // Redis에 실시간 랭킹 데이터가 있으면(trade 도메인이 updateRanking()을 호출하기 시작하면)
    // 그걸 우선 쓰고, 없으면(지금처럼 트레이드가 아직 없는 방) 현금 잔고로 폴백한다.
    public List<RoomRankingResponse> getRanking(Long roomId) {
        List<RankingDto> live = rankingService.getAllRanking(roomId, RankingType.TOTAL_ASSET);
        return live.isEmpty() ? getRankingFromCashBalance(roomId) : toRoomRankingResponses(live);
    }

    private List<RoomRankingResponse> toRoomRankingResponses(List<RankingDto> live) {
        Map<Long, String> nicknameByMemberId = memberRepository
                .findAllById(live.stream().map(RankingDto::memberId).toList()).stream()
                .collect(Collectors.toMap(Member::getId, Member::getNickname));

        return live.stream()
                .map(dto -> new RoomRankingResponse(
                        dto.rank(),
                        dto.memberId(),
                        nicknameByMemberId.get(dto.memberId()),
                        dto.score().longValue()))
                .toList();
    }

    private List<RoomRankingResponse> getRankingFromCashBalance(Long roomId) {
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
        for (Room room : expiredRooms) {
            try {
                archiveAndClose(room);
            } catch (Exception e) {
                // 특정 방(예: 시세 조회 실패)에서 터져도 다른 만료된 방들은 계속 정상 종료되도록 격리한다.
                log.error("방 종료 처리 실패 (roomId={})", room.getId(), e);
            }
        }
    }

    //관리자에 의한 방 강제 종료 (이상거래/신고 대응)
    @Transactional
    public void terminateRoomByAdmin(Long roomId, Long adminId) {
        Room room = getRoom(roomId);

        if (room.isDefault()) {
            throw new IllegalArgumentException("기본방은 강제 종료할 수 없습니다.");
        }
        if (!STATUS_ONGOING.equals(room.getStatus())) {
            throw new IllegalArgumentException("이미 종료된 방입니다.");
        }

        archiveAndClose(room);

        log.warn("관리자에 의한 방 강제 종료 (roomId={}, adminId={})", roomId, adminId);
    }

    private void archiveAndClose(Room room) {
        List<RoomParticipant> participants =
                roomParticipantRepository
                        .findByRoomIdAndStatus(
                                room.getId(),
                                PARTICIPANT_ACTIVE
                        );

        for (RoomParticipant participant : participants) {
            tradeRankingService.updateRanking(
                    participant
            );
        }

        rankingService.finalizeRanking(
                room.getId()
        );

        participants.forEach(
                RoomParticipant::end
        );

        room.close();
    }

    private RoomParticipant joinRoom(Room room, Long memberId) {
        // 상태 무관하게 한 번이라도 참가한 적 있으면 재입장 불가 (한 번 나간 방은 다시 못 들어옴)
        if (roomParticipantRepository.existsByRoomIdAndMemberId(room.getId(), memberId)) {
            throw new IllegalArgumentException("이미 참가했거나 나간 적이 있는 방입니다.");
        }
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
                .orElseGet(this::createDefaultRoom);
    }

    private Room createDefaultRoom() {
        try {
            return roomRepository.save(Room.createDefault(roomProperties.getDefaultSeedMoney()));
        } catch (DataIntegrityViolationException e) {
            // 동시에 들어온 다른 요청이 먼저 기본방을 생성한 경우.
            // uk_room_default_marker 유니크 제약 위반이므로 새로 만들지 않고 그 방을 그대로 쓴다.
            return roomRepository.findByIsDefaultTrue().orElseThrow(() -> e);
        }
    }

}
