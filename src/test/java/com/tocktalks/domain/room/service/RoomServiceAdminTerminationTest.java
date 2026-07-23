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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceAdminTerminationTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomParticipantRepository roomParticipantRepository;

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
    void 진행중인_방을_관리자가_강제종료하면_참가자_랭킹이_확정되고_방이_닫힌다() {
        Room room = mock(Room.class);
        RoomParticipant participant = mock(RoomParticipant.class);

        when(room.getId()).thenReturn(1L);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("ongoing");
        when(room.getEndAt()).thenReturn(null);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomParticipantRepository.findByRoomIdAndStatus(1L, "ACTIVE"))
                .thenReturn(List.of(participant));

        roomService.terminateRoomByAdmin(1L, 999L);

        InOrder order = inOrder(tradeRankingService, rankingService, participant, room);
        order.verify(tradeRankingService).updateRanking(participant);
        order.verify(rankingService).finalizeRanking(1L);
        order.verify(participant).end();
        order.verify(room).close();
    }

    @Test
    void 기본방은_강제종료할_수_없다() {
        Room room = mock(Room.class);
        when(room.isDefault()).thenReturn(true);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.terminateRoomByAdmin(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기본방");

        verify(rankingService, never()).finalizeRanking(anyLong());
    }

    @Test
    void 이미_종료된_방은_다시_강제종료할_수_없다() {
        Room room = mock(Room.class);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("closed");
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.terminateRoomByAdmin(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 종료된");

        verify(rankingService, never()).finalizeRanking(anyLong());
    }
}
