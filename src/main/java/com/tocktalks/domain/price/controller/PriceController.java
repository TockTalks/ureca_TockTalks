package com.tocktalks.domain.price.controller;

import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import com.tocktalks.domain.price.service.KisPriceService;
import com.tocktalks.domain.price.service.KisWebSocketClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PriceController {

    private final KisPriceService kisPriceService;
    private final KisWebSocketClient kisWebSocketClient;

    public PriceController(KisPriceService kisPriceService, KisWebSocketClient kisWebSocketClient) {
        this.kisPriceService = kisPriceService;
        this.kisWebSocketClient = kisWebSocketClient;
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
}