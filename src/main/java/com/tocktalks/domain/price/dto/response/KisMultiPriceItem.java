package com.tocktalks.domain.price.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KisMultiPriceItem(
        @JsonProperty("inter_shrn_iscd") String stockCode,
        @JsonProperty("inter2_prpr") String currentPrice,
        @JsonProperty("prdy_ctrt") String changeRate
) {
}