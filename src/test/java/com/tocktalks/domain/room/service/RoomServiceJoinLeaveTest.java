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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceJoinLeaveTest {

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
    void 모집중인_방은_참가할_수_있다() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(1L);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("recruiting");
        when(room.getStartAt()).thenReturn(LocalDateTime.now().plusHours(1));
        when(room.isPublic()).thenReturn(true);
        when(room.getSeedMoney()).thenReturn(10_000_000L);
        when(room.getMaxParticipants()).thenReturn(null);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(roomParticipantRepository.existsByRoomIdAndMemberIdAndStatus(1L, 10L, "ACTIVE")).thenReturn(false);
        when(roomParticipantRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> roomService.joinRoomById(1L, 10L)).doesNotThrowAnyException();
    }

    @Test
    void 나갔던_방도_모집중이면_다시_참가할_수_있다() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(1L);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("recruiting");
        when(room.getStartAt()).thenReturn(LocalDateTime.now().plusHours(1));
        when(room.isPublic()).thenReturn(true);
        when(room.getSeedMoney()).thenReturn(10_000_000L);
        when(room.getMaxParticipants()).thenReturn(null);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        // 예전에 나간 적 있어도(ENDED 이력 존재) 현재 ACTIVE만 아니면 재참가 가능해야 한다.
        when(roomParticipantRepository.existsByRoomIdAndMemberIdAndStatus(1L, 10L, "ACTIVE")).thenReturn(false);
        when(roomParticipantRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> roomService.joinRoomById(1L, 10L)).doesNotThrowAnyException();
    }

    @Test
    void 이미_참가중인_방은_중복_참가할_수_없다() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(1L);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("recruiting");
        when(room.getStartAt()).thenReturn(LocalDateTime.now().plusHours(1));
        when(room.isPublic()).thenReturn(true);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(roomParticipantRepository.existsByRoomIdAndMemberIdAndStatus(1L, 10L, "ACTIVE")).thenReturn(true);

        assertThatThrownBy(() -> roomService.joinRoomById(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 참가 중");

        verify(roomParticipantRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 이미_시작된_방은_참가할_수_없다() {
        Room room = mock(Room.class);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("ongoing");
        when(room.getEndAt()).thenReturn(null);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.joinRoomById(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 시작된");

        verify(roomParticipantRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 모집중인_방은_나갈_수_있다() {
        Room room = mock(Room.class);
        RoomParticipant participant = mock(RoomParticipant.class);

        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("recruiting");
        when(room.getStartAt()).thenReturn(LocalDateTime.now().plusHours(1));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomParticipantRepository.findByRoomIdAndMemberIdAndStatus(1L, 10L, "ACTIVE"))
                .thenReturn(Optional.of(participant));

        roomService.leaveRoom(1L, 10L);

        verify(participant).end();
        verify(rankingService).removeMemberFromLiveRanking(1L, 10L);
        verify(tradeRankingService, never()).updateRanking(participant);
    }

    @Test
    void 이미_시작된_방은_나갈_수_없다() {
        Room room = mock(Room.class);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("ongoing");
        when(room.getEndAt()).thenReturn(null);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.leaveRoom(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 시작된");

        verify(tradeRankingService, never()).updateRanking(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void 조회_시점에_시작시각이_지났으면_모집중_방이_바로_진행중으로_전환된다() {
        // mock은 start() 호출로 getStatus() 반환값이 실제로 바뀌지 않으므로,
        // 여기서는 "조회 시점에 startIfDue가 room.start()를 호출하는지"만 검증한다.
        // "시작된 방은 참가 불가"는 위의 이미_시작된_방은_참가할_수_없다()에서 별도로 검증한다.
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(1L);
        when(room.isDefault()).thenReturn(false);
        when(room.getStatus()).thenReturn("recruiting");
        when(room.getStartAt()).thenReturn(LocalDateTime.now().minusSeconds(1));
        when(room.isPublic()).thenReturn(true);
        when(room.getMaxParticipants()).thenReturn(null);
        when(room.getSeedMoney()).thenReturn(10_000_000L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(roomParticipantRepository.existsByRoomIdAndMemberIdAndStatus(1L, 10L, "ACTIVE")).thenReturn(false);
        when(roomParticipantRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        roomService.joinRoomById(1L, 10L);

        verify(room).start();
    }
}
