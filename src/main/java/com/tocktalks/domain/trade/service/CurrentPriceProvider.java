package com.tocktalks.domain.trade.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CurrentPriceProvider {

    BigDecimal getCurrentPrice(String stockCode);

    // 여러 종목의 현재가를 한 번에 조회한다 (KIS 다중시세 API 1회 호출로 처리).
    // 조회에 실패한 종목은 반환되는 맵에서 빠지므로, 호출부가 개별 폴백을 결정한다.
    Map<String, BigDecimal> getCurrentPrices(List<String> stockCodes);
}