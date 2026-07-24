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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceLeaveTest {

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
    void memberCanLeaveBeforeBattleStarts() {
        Room room = mock(Room.class);
        RoomParticipant participant = mock(RoomParticipant.class);

        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("ongoing");
        when(room.getEndAt()).thenReturn(LocalDateTime.now().plusHours(2));
        when(room.getStartAt()).thenReturn(LocalDateTime.now().plusHours(1));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomParticipantRepository.findByRoomIdAndMemberIdAndStatus(1L, 9L, "ACTIVE"))
                .thenReturn(Optional.of(participant));

        roomService.leaveRoom(1L, 9L);

        verify(participant).end();
        verify(rankingService).removeMemberFromLiveRanking(1L, 9L);
        verifyNoInteractions(tradeRankingService);
    }

    @Test
    void memberCannotLeaveAfterBattleStarts() {
        Room room = mock(Room.class);

        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("ongoing");
        when(room.getEndAt()).thenReturn(LocalDateTime.now().plusHours(1));
        when(room.getStartAt()).thenReturn(LocalDateTime.now().minusMinutes(1));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.leaveRoom(1L, 9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("배틀이 시작된 후에는 방에서 나갈 수 없습니다.");

        verify(roomParticipantRepository, never())
                .findByRoomIdAndMemberIdAndStatus(1L, 9L, "ACTIVE");
        verifyNoInteractions(tradeRankingService, rankingService);
    }

    @Test
    void memberCannotLeaveDefaultRoom() {
        Room room = mock(Room.class);

        when(room.isDefault()).thenReturn(true);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.leaveRoom(1L, 9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기본방은 탈퇴할 수 없습니다.");

        verifyNoInteractions(tradeRankingService, rankingService);
    }
}
