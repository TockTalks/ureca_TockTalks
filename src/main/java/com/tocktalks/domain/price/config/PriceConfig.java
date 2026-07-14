package com.tocktalks.domain.price.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KisApiProperties.class)
public class PriceConfig {
}
