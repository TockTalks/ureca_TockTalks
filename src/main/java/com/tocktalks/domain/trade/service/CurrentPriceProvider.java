package com.tocktalks.domain.trade.service;

import java.math.BigDecimal;

public interface CurrentPriceProvider {

    BigDecimal getCurrentPrice(String stockCode);
}