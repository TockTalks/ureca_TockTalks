package com.tocktalks.domain.price.service;

import com.tocktalks.domain.price.config.KisApiProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
class KisWebSocketClientTest {

    @Mock
    private KisApiProperties kisApiProperties;

    @Mock
    private KisAuthService kisAuthService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PricePublisher pricePublisher;

    @InjectMocks
    private KisWebSocketClient kisWebSocketClient;

    @Test
    void KIS_세션이_없어도_구독해제는_예외없이_처리된다() {
        // KIS 웹소켓 연결이 아직(혹은 더이상) 없는 상태(session == null)에서
        // 브라우저 연결이 끊겨 구독해제가 들어와도 NPE 없이 조용히 넘어가야 한다.
        assertThatCode(() -> kisWebSocketClient.unsubscribe("005930"))
                .doesNotThrowAnyException();
    }

    @Test
    void KIS_세션이_없어도_구독요청은_예외없이_처리된다() {
        assertThatCode(() -> kisWebSocketClient.subscribe("005930"))
                .doesNotThrowAnyException();
    }
}
