package com.tocktalks.domain.trade.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record TradeOrderRequest(

        @NotBlank(message = "종목 코드는 필수입니다.")
        @Pattern(
                regexp = "\\d{6}",
                message = "종목 코드는 6자리 숫자여야 합니다."
        )
        String stockCode,

        @NotNull(message = "거래 수량은 필수입니다.")
        @Positive(message = "거래 수량은 1 이상이어야 합니다.")
        Long quantity
) {
}