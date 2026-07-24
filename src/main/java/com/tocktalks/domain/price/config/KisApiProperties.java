package com.tocktalks.domain.price.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external-api.stock")
public record KisApiProperties(
        String appKey,
        String appSecret,
        String restBaseUrl,
        String websocketUrl
) {
}
