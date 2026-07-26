package com.tocktalks.domain.room.service;

import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.ranking.repository.RoomRankingArchiveRepository;
import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.dto.RoomResponse;
import com.tocktalks.domain.room.entity.Room;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceDetailTest {

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

    @Mock
    private RoomRankingArchiveRepository roomRankingArchiveRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void 종료된_방의_참가자수는_모집중에_나간_사람을_제외하고_회원_단위로_센다() {
        LocalDateTime startAt = LocalDateTime.now().minusHours(2);
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(1L);
        when(room.getStatus()).thenReturn("closed");
        when(room.getStartAt()).thenReturn(startAt);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomParticipantRepository.findByRoomIdAndMemberIdAndStatus(1L, 10L, "ACTIVE"))
                .thenReturn(Optional.empty());
        when(roomParticipantRepository.countRealParticipantsByRoomId(1L, startAt)).thenReturn(3L);

        RoomResponse response = roomService.getRoomDetail(1L, 10L);

        assertThat(response.participantCount()).isEqualTo(3L);
        verify(roomParticipantRepository).countRealParticipantsByRoomId(1L, startAt);
        verify(roomParticipantRepository, never()).countByRoomId(anyLong());
    }
}
