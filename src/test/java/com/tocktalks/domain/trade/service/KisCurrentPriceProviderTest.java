package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import com.tocktalks.domain.price.service.KisPriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KisCurrentPriceProviderTest {

    @Mock
    private KisPriceService kisPriceService;

    @InjectMocks
    private KisCurrentPriceProvider currentPriceProvider;

    @Test
    void KIS_현재가를_BigDecimal로_변환한다() {
        when(kisPriceService.getCurrentPrice("005930"))
                .thenReturn(priceResponse("70000"));

        BigDecimal result =
                currentPriceProvider.getCurrentPrice(
                        "005930"
                );

        assertThat(result)
                .isEqualByComparingTo("70000");
    }

    @Test
    void 현재가가_비어_있으면_예외가_발생한다() {
        when(kisPriceService.getCurrentPrice("005930"))
                .thenReturn(priceResponse(" "));

        assertThatThrownBy(() ->
                currentPriceProvider.getCurrentPrice(
                        "005930"
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "현재가 응답이 올바르지 않습니다."
                );
    }

    @Test
    void 현재가가_숫자가_아니면_예외가_발생한다() {
        when(kisPriceService.getCurrentPrice("005930"))
                .thenReturn(priceResponse("잘못된 가격"));

        assertThatThrownBy(() ->
                currentPriceProvider.getCurrentPrice(
                        "005930"
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "현재가 응답이 숫자 형식이 아닙니다."
                );
    }

    @Test
    void 현재가가_0_이하면_예외가_발생한다() {
        when(kisPriceService.getCurrentPrice("005930"))
                .thenReturn(priceResponse("0"));

        assertThatThrownBy(() ->
                currentPriceProvider.getCurrentPrice(
                        "005930"
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "현재가는 0보다 커야 합니다."
                );
    }

    @Test
    void 종목_코드가_비어_있으면_KIS를_호출하지_않는다() {
        assertThatThrownBy(() ->
                currentPriceProvider.getCurrentPrice(" ")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종목 코드는 필수입니다.");

        verifyNoInteractions(kisPriceService);
    }

    @Test
    void 여러_종목의_현재가를_배치로_조회한다() {
        Map<String, BigDecimal> prices = Map.of(
                "005930", new BigDecimal("70000"),
                "035420", new BigDecimal("180000")
        );

        when(kisPriceService.getCurrentPrices(
                List.of("005930", "035420")
        )).thenReturn(prices);

        Map<String, BigDecimal> result =
                currentPriceProvider.getCurrentPrices(
                        List.of("005930", "035420")
                );

        assertThat(result).isEqualTo(prices);
    }

    @Test
    void 배치_조회시_종목_코드가_올바르지_않으면_KIS를_호출하지_않는다() {
        assertThatThrownBy(() ->
                currentPriceProvider.getCurrentPrices(
                        List.of("005930", "00A930")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종목 코드는 6자리 숫자여야 합니다.");

        verifyNoInteractions(kisPriceService);
    }

    private KisPriceResponse priceResponse(
            String currentPrice
    ) {
        return new KisPriceResponse(
                currentPrice,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void 종목_코드가_6자리_숫자가_아니면_KIS를_호출하지_않는다() {
        assertThatThrownBy(() ->
                currentPriceProvider.getCurrentPrice(
                        "00A930"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "종목 코드는 6자리 숫자여야 합니다."
                );

        verifyNoInteractions(kisPriceService);
    }
}