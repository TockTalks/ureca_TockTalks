package com.tocktalks.domain.room.service;

import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.service.TradeRankingService;
import com.tocktalks.global.config.RoomProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceRankingTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomParticipantRepository
            roomParticipantRepository;

    @Mock
    private TradeRankingService tradeRankingService;

    @Mock
    private RankingService rankingService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoomProperties roomProperties;

    @InjectMocks
    private RoomService roomService;

    @Test
    void expiredRoomRankingIsFinalizedBeforeRoomCloses() {
        Room room = mock(Room.class);

        RoomParticipant firstParticipant =
                mock(RoomParticipant.class);

        RoomParticipant secondParticipant =
                mock(RoomParticipant.class);

        when(room.getId()).thenReturn(1L);

        when(roomRepository.findByStatusAndEndAtBefore(
                eq("ongoing"),
                any(LocalDateTime.class)
        )).thenReturn(List.of(room));

        when(roomRepository.findByIdForUpdate(1L))
                .thenReturn(java.util.Optional.of(room));

        when(roomParticipantRepository
                .findByRoomIdAndStatus(
                        1L,
                        "ACTIVE"
                ))
                .thenReturn(List.of(
                        firstParticipant,
                        secondParticipant
                ));

        roomService.closeExpiredRooms();

        verify(roomRepository)
                .findByStatusAndEndAtBefore(
                        eq("ongoing"),
                        any(LocalDateTime.class)
                );

        verify(roomParticipantRepository)
                .findByRoomIdAndStatus(
                        1L,
                        "ACTIVE"
                );

        InOrder order = inOrder(
                tradeRankingService,
                rankingService,
                firstParticipant,
                secondParticipant,
                room
        );

        order.verify(tradeRankingService)
                .updateRanking(firstParticipant);

        order.verify(tradeRankingService)
                .updateRanking(secondParticipant);

        order.verify(rankingService)
                .finalizeRanking(1L);

        order.verify(firstParticipant).end();
        order.verify(secondParticipant).end();
        order.verify(room).close();
    }

    @Test
    void 락_획득_사이에_이미_종료된_방은_다시_아카이브하지_않는다() {
        // 만료 목록을 조회한 시점(room)과 락을 잡은 시점(alreadyClosedRoom) 사이에
        // 다른 트랜잭션이 먼저 종료 처리를 끝낸 상황을 시뮬레이션한다.
        Room room = mock(Room.class);
        Room alreadyClosedRoom = mock(Room.class);

        when(room.getId()).thenReturn(1L);
        when(alreadyClosedRoom.getStatus()).thenReturn("closed");

        when(roomRepository.findByStatusAndEndAtBefore(
                eq("ongoing"),
                any(LocalDateTime.class)
        )).thenReturn(List.of(room));

        when(roomRepository.findByIdForUpdate(1L))
                .thenReturn(java.util.Optional.of(alreadyClosedRoom));

        roomService.closeExpiredRooms();

        verifyNoInteractions(tradeRankingService, rankingService);
        verify(alreadyClosedRoom, org.mockito.Mockito.never()).close();
    }

    @Test
    void withdrawalEndsEveryActiveParticipationAndRemovesLiveRankings() {
        RoomParticipant defaultRoomParticipant = mock(RoomParticipant.class);
        RoomParticipant battleRoomParticipant = mock(RoomParticipant.class);

        when(defaultRoomParticipant.getRoomId()).thenReturn(1L);
        when(defaultRoomParticipant.getMemberId()).thenReturn(10L);
        when(battleRoomParticipant.getRoomId()).thenReturn(2L);
        when(battleRoomParticipant.getMemberId()).thenReturn(10L);
        when(roomParticipantRepository.findByMemberIdAndStatus(10L, "ACTIVE"))
                .thenReturn(List.of(defaultRoomParticipant, battleRoomParticipant));

        roomService.endActiveParticipationsForWithdrawal(10L);

        InOrder order = inOrder(
                defaultRoomParticipant,
                battleRoomParticipant,
                rankingService
        );
        order.verify(defaultRoomParticipant).end();
        order.verify(rankingService).removeMemberFromLiveRanking(1L, 10L);
        order.verify(battleRoomParticipant).end();
        order.verify(rankingService).removeMemberFromLiveRanking(2L, 10L);
    }

    @Test
    void legacyWithdrawnActiveParticipationIsCleanedWithoutClosingRoom() {
        RoomParticipant staleParticipant = mock(RoomParticipant.class);
        when(staleParticipant.getRoomId()).thenReturn(1L);
        when(staleParticipant.getMemberId()).thenReturn(10L);
        when(roomParticipantRepository.findActiveParticipantsOfWithdrawnMembers())
                .thenReturn(List.of(staleParticipant));

        roomService.cleanupLegacyWithdrawnParticipations();

        verify(staleParticipant).end();
        verify(rankingService).removeMemberFromLiveRanking(1L, 10L);
        verifyNoInteractions(roomRepository);
    }
}
