package com.tocktalks.domain.price.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KisTokenRequest (
        @JsonProperty("grant_type") String grantType,
        String appkey,
        String appsecret
) {
    public static KisTokenRequest of(String appKey, String appSecret) {
        return new KisTokenRequest("client_credentials", appKey, appSecret);
    }
}
