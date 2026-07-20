package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeTransactionCertificateProviderTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TradeTransactionCertificateProvider provider;

    @Test
    void 본인의_매도_거래를_인증한다() {
        Transaction sellTransaction =
                Transaction.createSell(
                        1L,
                        "005930",
                        "삼성전자",
                        2L,
                        new BigDecimal("13000"),
                        new BigDecimal("10000")
                );

        when(transactionRepository
                .findOwnedByIdAndMemberId(10L, 20L))
                .thenReturn(Optional.of(sellTransaction));

        var snapshot =
                provider.certifySellTransaction(10L, 20L);

        assertThat(snapshot.stockCode())
                .isEqualTo("005930");
        assertThat(snapshot.profitAmount())
                .isEqualByComparingTo("6000.00");
        assertThat(snapshot.profitRate())
                .isEqualByComparingTo("30.0000");
    }

    @Test
    void 매수_거래는_인증할_수_없다() {
        Transaction buyTransaction =
                Transaction.createBuy(
                        1L,
                        "005930",
                        "삼성전자",
                        2L,
                        new BigDecimal("10000")
                );

        when(transactionRepository
                .findOwnedByIdAndMemberId(10L, 20L))
                .thenReturn(Optional.of(buyTransaction));

        assertThatThrownBy(
                () -> provider
                        .certifySellTransaction(10L, 20L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "매도 거래만 투자 인증에 사용할 수 있습니다."
                );
    }

    @Test
    void 다른_회원의_거래는_인증할_수_없다() {
        when(transactionRepository
                .findOwnedByIdAndMemberId(10L, 20L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> provider
                        .certifySellTransaction(10L, 20L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "인증할 수 있는 거래를 찾을 수 없습니다."
                );
    }
}