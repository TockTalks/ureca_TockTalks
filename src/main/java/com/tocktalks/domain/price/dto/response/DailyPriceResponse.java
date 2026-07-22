package com.tocktalks.domain.price.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record DailyPriceResponse(
        LocalDate date,
        long openPrice,
        long highPrice,
        long lowPrice,
        long closePrice,
        long volume
) {
    private static final DateTimeFormatter KIS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static DailyPriceResponse from(KisDailyPriceItem item) {
        return new DailyPriceResponse(
                LocalDate.parse(item.date(), KIS_DATE_FORMAT),
                Long.parseLong(item.openPrice()),
                Long.parseLong(item.highPrice()),
                Long.parseLong(item.lowPrice()),
                Long.parseLong(item.closePrice()),
                Long.parseLong(item.volume())
        );
    }
}