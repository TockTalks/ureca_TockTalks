package com.tocktalks.domain.price.controller;

import com.tocktalks.domain.price.dto.response.KisPriceResponse;
import com.tocktalks.domain.price.service.KisPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceController {

    private final KisPriceService kisPriceService;
    public PriceController(KisPriceService kisPriceService) {
        this.kisPriceService = kisPriceService;
    }

    @GetMapping("/api/price/{stockCode}")
    public KisPriceResponse getPrice(@PathVariable String stockCode) {
        return kisPriceService.getCurrentPrice(stockCode);
    }
}