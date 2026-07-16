package com.tocktalks.domain.price.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KisRealtimeSubscribeRequest (
        Header header,
        Body body
) {
    public record Header(
            @JsonProperty("approval_key") String approvalKey,
            String custtype,
            @JsonProperty("tr_type") String trType,
            @JsonProperty("content-type") String contentType
    ) {
    }

    public record Body(Input input) {
    }
    public record Input(
            @JsonProperty("tr_id") String trId,
            @JsonProperty("tr_key") String trKey
    ) {
    }

    public static KisRealtimeSubscribeRequest of(String approvalKey, String trId, String stockCode) {
        return new KisRealtimeSubscribeRequest(
                new Header(approvalKey, "P", "1", "utf-8"),
                new Body(new Input(trId, stockCode))
        );
    }
}
