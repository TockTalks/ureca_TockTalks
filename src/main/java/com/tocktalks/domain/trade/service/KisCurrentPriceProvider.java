package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import com.tocktalks.domain.price.service.KisPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class KisCurrentPriceProvider
        implements CurrentPriceProvider {

    private final KisPriceService kisPriceService;

    @Override
    public BigDecimal getCurrentPrice(String stockCode) {
        validateStockCode(stockCode);

        KisPriceResponse response =
                kisPriceService.getCurrentPrice(stockCode);

        if (response == null
                || response.currentPrice() == null
                || response.currentPrice().isBlank()) {
            throw new IllegalStateException(
                    "현재가 응답이 올바르지 않습니다."
            );
        }

        BigDecimal currentPrice;

        try {
            currentPrice = new BigDecimal(
                    response.currentPrice().trim()
            );
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "현재가 응답이 숫자 형식이 아닙니다.",
                    exception
            );
        }

        if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "현재가는 0보다 커야 합니다."
            );
        }

        return currentPrice;
    }

    private void validateStockCode(String stockCode) {
        if (stockCode == null || stockCode.isBlank()) {
            throw new IllegalArgumentException(
                    "종목 코드는 필수입니다."
            );
        }
    }
}