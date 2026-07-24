package com.tocktalks.domain.price.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@EnableConfigurationProperties(KisApiProperties.class)
public class PriceConfig {
    @Bean
    public WebClient kisWebClient(KisApiProperties kisApiProperties) {
        // 커넥션에 타임아웃이 하나도 없어서, KIS 쪽에서 응답 없이 연결만 붙들고 있으면
        // 이 호출을 기다리는 스레드가 영원히 안 풀렸다. 그래서 배치 조회를 백그라운드로
        // 돌려도 스레드풀이 결국 다 막혀서 전부 타임아웃나는 상태가 됐다.
        // maxIdleTime은 KIS 서버가 먼저 끊어버린 유휴 커넥션을 재사용하려다 나는
        // PrematureCloseException도 같이 줄여준다.
        // responseTimeout은 HoldingQueryService.PRICE_FETCH_TIMEOUT(4.5초)보다는 짧게 잡아서,
        // KIS가 계속 응답이 없을 때 상위(전체 배치 대기) 타임아웃보다 먼저 끊고 정리되게 한다.
        // 종목 수가 많은 배치(예: 보유종목 워밍업)일수록 KIS 응답이 오래 걸려 3초로는
        // ReadTimeoutException이 잦았다.
        ConnectionProvider connectionProvider = ConnectionProvider.builder("kis-connection-pool")
                .maxIdleTime(Duration.ofSeconds(10))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(4));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(kisApiProperties.restBaseUrl())
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .build();
    }
}
