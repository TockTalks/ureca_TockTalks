package com.tocktalks.domain.room.service;

import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.ranking.entity.RoomRankingArchive;
import com.tocktalks.domain.ranking.repository.RoomRankingArchiveRepository;
import com.tocktalks.domain.ranking.service.RankingService;
import com.tocktalks.domain.room.dto.RoomHistoryResponse;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceHistoryTest {

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
    void 참가했던_종료된_방들의_기록을_최신순으로_반환한다() {
        RoomRankingArchive archive = mock(RoomRankingArchive.class);
        when(archive.getRoomId()).thenReturn(1L);
        when(archive.getFinalRank()).thenReturn(2);
        when(archive.getFinalAsset()).thenReturn(11_000_000L);
        when(archive.getFinalReturnRate()).thenReturn(BigDecimal.valueOf(10.0));

        Room room = mock(Room.class);
        when(room.getId()).thenReturn(1L);
        when(room.getName()).thenReturn("테스트방");

        when(roomRankingArchiveRepository.findByMemberIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(archive));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRankingArchiveRepository.countByRoomId(1L)).thenReturn(5L);

        List<RoomHistoryResponse> history = roomService.getMyRoomHistory(10L);

        assertThat(history).hasSize(1);
        RoomHistoryResponse entry = history.get(0);
        assertThat(entry.roomId()).isEqualTo(1L);
        assertThat(entry.roomName()).isEqualTo("테스트방");
        assertThat(entry.finalRank()).isEqualTo(2);
        assertThat(entry.finalAsset()).isEqualTo(11_000_000L);
        assertThat(entry.participantCount()).isEqualTo(5L);
    }

    @Test
    void 참가_기록이_없으면_빈_목록을_반환한다() {
        when(roomRankingArchiveRepository.findByMemberIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of());

        List<RoomHistoryResponse> history = roomService.getMyRoomHistory(10L);

        assertThat(history).isEmpty();
    }
}
