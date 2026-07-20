package com.tocktalks.domain.price.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(KisApiProperties.class)
public class PriceConfig {
    @Bean
    public WebClient kisWebClient(KisApiProperties kisApiProperties) {
        return WebClient.builder()
                .baseUrl(kisApiProperties.restBaseUrl())
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .build();
    }
}
