package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.dto.response.TradeHistoryResponse;
import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeHistoryServiceTest {

    @Mock
    private RoomParticipantRepository
            roomParticipantRepository;

    @Mock
    private TransactionRepository
            transactionRepository;

    @InjectMocks
    private TradeHistoryService tradeHistoryService;

    @Test
    void 본인_거래_내역을_최신순_페이지로_조회한다() {
        Long memberId = 10L;
        Long roomParticipantId = 20L;
        Pageable pageable = PageRequest.of(0, 10);

        RoomParticipant participant =
                org.mockito.Mockito.mock(
                        RoomParticipant.class
                );

        when(participant.getMemberId())
                .thenReturn(memberId);

        Transaction transaction = Transaction.createSell(
                roomParticipantId,
                "005930",
                "삼성전자",
                2L,
                new BigDecimal("75000"),
                new BigDecimal("70000")
        );

        Page<Transaction> transactions =
                new PageImpl<>(
                        List.of(transaction),
                        pageable,
                        1
                );

        when(roomParticipantRepository.findById(
                roomParticipantId
        )).thenReturn(Optional.of(participant));

        when(transactionRepository
                .findAllByRoomParticipantIdOrderByExecutedAtDesc(
                        roomParticipantId,
                        pageable
                )
        ).thenReturn(transactions);

        Page<TradeHistoryResponse> result =
                tradeHistoryService.getTradeHistory(
                        memberId,
                        roomParticipantId,
                        pageable
                );

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .singleElement()
                .satisfies(response -> {
                    assertThat(response.stockCode())
                            .isEqualTo("005930");
                    assertThat(response.stockName())
                            .isEqualTo("삼성전자");
                    assertThat(response.type())
                            .isEqualTo(TradeType.SELL);
                    assertThat(response.quantity())
                            .isEqualTo(2L);
                    assertThat(response.price())
                            .isEqualByComparingTo("75000.00");
                    assertThat(response.profitAmount())
                            .isEqualByComparingTo("10000.00");
                    assertThat(response.profitRate())
                            .isEqualByComparingTo("7.1429");
                    assertThat(response.executedAt())
                            .isNotNull();
                });

        verify(transactionRepository)
                .findAllByRoomParticipantIdOrderByExecutedAtDesc(
                        roomParticipantId,
                        pageable
                );
    }

    @Test
    void 존재하지_않는_참가자의_거래_내역은_조회할_수_없다() {
        Long memberId = 10L;
        Long roomParticipantId = 20L;
        Pageable pageable = PageRequest.of(0, 10);

        when(roomParticipantRepository.findById(
                roomParticipantId
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                tradeHistoryService.getTradeHistory(
                        memberId,
                        roomParticipantId,
                        pageable
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 내역을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
                );

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void 다른_회원의_거래_내역은_조회할_수_없다() {
        Long memberId = 10L;
        Long roomParticipantId = 20L;

        RoomParticipant participant =
                org.mockito.Mockito.mock(
                        RoomParticipant.class
                );

        when(participant.getMemberId())
                .thenReturn(999L);

        when(roomParticipantRepository.findById(
                roomParticipantId
        )).thenReturn(Optional.of(participant));

        assertThatThrownBy(() ->
                tradeHistoryService.getTradeHistory(
                        memberId,
                        roomParticipantId,
                        PageRequest.of(0, 10)
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "거래 내역을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
                );

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void 회원_ID가_올바르지_않으면_거래_내역_Repository를_호출하지_않는다() {
        assertThatThrownBy(() ->
                tradeHistoryService.getTradeHistory(
                        0L,
                        20L,
                        PageRequest.of(0, 10)
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "회원 ID가 올바르지 않습니다."
                );

        verifyNoInteractions(
                roomParticipantRepository,
                transactionRepository
        );
    }

    @Test
    void 방_참가자_ID가_올바르지_않으면_거래_내역_Repository를_호출하지_않는다() {
        assertThatThrownBy(() ->
                tradeHistoryService.getTradeHistory(
                        10L,
                        0L,
                        PageRequest.of(0, 10)
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "방 참가자 ID가 올바르지 않습니다."
                );

        verifyNoInteractions(
                roomParticipantRepository,
                transactionRepository
        );
    }
}