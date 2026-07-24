package com.tocktalks.domain.ranking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReturnRateCalculator {

    public static BigDecimal calculate(Long finalAsset, Long seedMoney){
        if(seedMoney == null || seedMoney == 0){
            throw new IllegalArgumentException("시드머니가 0");
        }
        return BigDecimal.valueOf(finalAsset - seedMoney)
                .divide(BigDecimal.valueOf(seedMoney), 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);
    }
}
