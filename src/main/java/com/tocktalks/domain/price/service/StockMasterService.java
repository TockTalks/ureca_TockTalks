package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.dto.response.StockInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockMasterService {

    private static final String CSV_PATH = "stock-master/kospi_stock_master.csv";

    private List<StockInfo> stocks;
    private Map<String, String> stockNameByCode;

    @PostConstruct
    public void loadStocks() {
        List<StockInfo> loaded = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8))) {

            String line = reader.readLine(); // 헤더 줄 건너뜀
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    loaded.add(new StockInfo(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("종목 마스터 파일 로딩 실패", e);
        }

        this.stocks = loaded;
        this.stockNameByCode = loaded.stream()
                .collect(Collectors.toMap(StockInfo::stockCode, StockInfo::stockName, (a, b) -> a));
    }

    public List<StockInfo> getAllStocks() {
        return stocks;
    }

    public String getStockName(String stockCode) {
        String name = stockNameByCode.get(stockCode);
        if (name == null) {
            throw new IllegalArgumentException("존재하지 않는 종목코드입니다: " + stockCode);
        }
        return name;
    }
}