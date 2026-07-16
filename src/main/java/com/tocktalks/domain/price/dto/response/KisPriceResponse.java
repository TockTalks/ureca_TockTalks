package com.tocktalks.domain.price.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KisPriceResponse(
        @JsonProperty("stck_prpr") String currentPrice,
        @JsonProperty("prdy_vrss") String priceChange,
        @JsonProperty("prdy_vrss_sign") String changeSign,
        @JsonProperty("prdy_ctrt") String changeRate,
        @JsonProperty("acml_vol") String accumulatedVolume,
        @JsonProperty("stck_oprc") String openPrice,
        @JsonProperty("stck_hgpr") String highPrice,
        @JsonProperty("stck_lwpr") String lowPrice
) {
}