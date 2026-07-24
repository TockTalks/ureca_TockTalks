package com.tocktalks.domain.price.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KisApprovalRequest (
        @JsonProperty("grant_type") String grantType,
        String appkey,
        String secretkey
) {
    public static KisApprovalRequest of(String appKey, String appSecret){
        return new KisApprovalRequest("client_credentials", appKey, appSecret);
    }
}
