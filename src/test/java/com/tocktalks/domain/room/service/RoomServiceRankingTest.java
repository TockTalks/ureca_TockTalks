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
}