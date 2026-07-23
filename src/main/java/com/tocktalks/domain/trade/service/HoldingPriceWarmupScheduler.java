package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.trade.repository.HoldingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoldingPriceWarmupScheduler {

    private final HoldingRepository holdingRepository;
    private final CurrentPriceProvider currentPriceProvider;

    //현재 보유중인 종목들의 시세를 백그라운드에서 주기적으로 미리 캐시에 채워둠
    //-> 유저가 포트폴리오 조회시 이미 캐시된 값을 가져와 대기 없이 즉시 응답
    //종목 수만큼 KIS를 개별 호출하면 레이트리밋에 바로 걸리므로 배치로 한 번에 조회한다.
    @Scheduled(fixedDelay = 5000)
    public void warmUpHeldStockPrices() {
        List<String> stockCodes = holdingRepository.findDistinctStockCode();

        if (stockCodes.isEmpty()) {
            return;
        }

        try {
            currentPriceProvider.getCurrentPrices(stockCodes);
        } catch (Exception e) {
            log.warn("시세 워밍업 실패 stockCodes={}", stockCodes, e);
        }
    }
}
