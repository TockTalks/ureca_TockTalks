package com.tocktalks.domain.price.controller;

import com.tocktalks.domain.price.dto.response.DailyPriceResponse;
import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import com.tocktalks.domain.price.dto.response.StockQuoteResponse;
import com.tocktalks.domain.price.service.KisChartService;
import com.tocktalks.domain.price.service.KisPriceService;
import com.tocktalks.domain.price.service.KisWebSocketClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tocktalks.domain.price.dto.response.StockInfo;
import com.tocktalks.domain.price.service.StockMasterService;

import java.util.List;
import java.io.IOException;

@RestController
public class PriceController {

    private final KisPriceService kisPriceService;
    private final KisWebSocketClient kisWebSocketClient;
    private final StockMasterService stockMasterService;
    private final KisChartService kisChartService;

    public PriceController(KisPriceService kisPriceService, KisWebSocketClient kisWebSocketClient, StockMasterService stockMasterService, KisChartService kisChartService) {
        this.kisPriceService = kisPriceService;
        this.kisWebSocketClient = kisWebSocketClient;
        this.stockMasterService = stockMasterService;
        this.kisChartService = kisChartService;
    }

    @GetMapping("/api/price/{stockCode}")
    public KisPriceResponse getPrice(@PathVariable String stockCode) {
        return kisPriceService.getCurrentPrice(stockCode);
    }

    @GetMapping("/api/price/subscribe/{stockCode}")
    public String subscribeRealtime(@PathVariable String stockCode) throws IOException {
        kisWebSocketClient.connect();
        kisWebSocketClient.subscribe(stockCode);
        return "구독 요청 완료: " + stockCode;
    }

    @GetMapping("/api/price/stocks")
    public List<StockInfo> getStocks() {
        return stockMasterService.getAllStocks();
    }

    @GetMapping("/api/price/{stockCode}/history")
    public List<DailyPriceResponse> getDailyHistory(
            @PathVariable String stockCode,
            @RequestParam(defaultValue = "30") int days) {
        return kisChartService.getRecentDailyPrices(stockCode, days);
    }

    @GetMapping("/api/price/batch")
    public List<StockQuoteResponse> getMultiplePrices(@RequestParam List<String> codes) {
        return kisPriceService.getMultiplePrices(codes);
    }
}