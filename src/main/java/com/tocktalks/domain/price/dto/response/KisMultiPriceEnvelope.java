package com.tocktalks.domain.price.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KisMultiPriceEnvelope(
        @JsonProperty("rt_cd") String returnCode,
        @JsonProperty("msg_cd") String messageCode,
        @JsonProperty("msg1") String message,
        @JsonProperty("output") KisMultiPriceItem[] output
) {
    public boolean isSuccess() {
        return "0".equals(returnCode);
    }
}