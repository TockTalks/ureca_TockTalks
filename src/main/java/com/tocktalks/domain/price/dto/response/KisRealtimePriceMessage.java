package com.tocktalks.domain.price.dto.response;

public record KisRealtimePriceMessage (
        String stockCode,
        String currentPrice,
        String changeSign,
        String priceChange,
        String changeRate,
        String accumulatedVolume
) {
    public static KisRealtimePriceMessage from(String rawData) {
        String[] fields = rawData.split("\\^");
        return new KisRealtimePriceMessage(
                fields[0],   // MKSC_SHRN_ISCD (종목코드)
                fields[2],   // STCK_PRPR (현재가)
                fields[3],   // PRDY_VRSS_SIGN (전일대비 부호)
                fields[4],   // PRDY_VRSS (전일대비)
                fields[5],   // PRDY_CTRT (전일대비율)
                fields[13]   // ACML_VOL (누적거래량)
        );
    }
}
