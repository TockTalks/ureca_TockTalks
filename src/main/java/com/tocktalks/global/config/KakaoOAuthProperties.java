package com.tocktalks.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external-api.oauth.kakao")
public class KakaoOAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}