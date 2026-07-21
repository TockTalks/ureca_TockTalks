package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.price.service.StockMasterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockMasterStockNameProviderTest {

    @Mock
    private StockMasterService stockMasterService;

    @InjectMocks
    private StockMasterStockNameProvider stockNameProvider;

    @Test
    void 종목_코드로_종목명을_조회한다() {
        when(stockMasterService.getStockName("005930"))
                .thenReturn(" 삼성전자 ");

        String result =
                stockNameProvider.getStockName("005930");

        assertThat(result).isEqualTo("삼성전자");
    }

    @Test
    void 종목_코드가_올바르지_않으면_StockMasterService를_호출하지_않는다() {
        assertThatThrownBy(() ->
                stockNameProvider.getStockName("00A930")
        )
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(stockMasterService);
    }

    @Test
    void 조회된_종목명이_비어_있으면_예외가_발생한다() {
        when(stockMasterService.getStockName("005930"))
                .thenReturn(" ");

        assertThatThrownBy(() ->
                stockNameProvider.getStockName("005930")
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("종목명 응답이 올바르지 않습니다.");
    }
}