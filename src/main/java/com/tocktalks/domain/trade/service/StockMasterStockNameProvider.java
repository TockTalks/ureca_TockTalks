package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.price.service.StockMasterService;
import com.tocktalks.domain.trade.entity.StockCodeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockMasterStockNameProvider implements StockNameProvider {

    private final StockMasterService stockMasterService;

    @Override
    public String getStockName(String stockCode) {
        StockCodeValidator.validate(stockCode);

        String stockName = stockMasterService.getStockName(stockCode);

        if (stockName == null || stockName.isBlank()) {
            throw new IllegalStateException("종목명 응답이 올바르지 않습니다.");
        }

        return stockName.trim();
    }
}