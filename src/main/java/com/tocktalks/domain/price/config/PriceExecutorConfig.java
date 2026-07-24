package com.tocktalks.domain.price.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceExecutorConfig {

    // 보유종목 시세 조회를 KIS 응답을 마냥 기다리지 않고 타임아웃 처리하기 위한 백그라운드 실행기.
    // 타임아웃 이후에도 이 스레드에서 KIS 호출은 끝까지 진행되어 캐시를 갱신한다.
    @Bean
    public Executor priceFetchExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}
