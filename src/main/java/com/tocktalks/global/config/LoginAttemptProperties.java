package com.tocktalks.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.login-attempt")
public class LoginAttemptProperties {
    private int maxAttempts;
    private long lockDurationMs;
}
