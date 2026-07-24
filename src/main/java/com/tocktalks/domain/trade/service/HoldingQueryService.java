package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HoldingQueryService {

    private final RoomParticipantRepository
            roomParticipantRepository;

    private final HoldingRepository holdingRepository;

    private final CurrentPriceProvider currentPriceProvider;

    private static final String LATEST_PRICE_KEY_PREFIX =
            "price:latest:";

    // KIS 응답을 무한정 기다리지 않고, 이 시간이 지나면 캐시/매입가로 즉시 응답한다.
    // 백그라운드 스레드에서는 KIS 호출이 계속 진행되어 다음 요청을 위해 캐시를 채워둔다.
    private static final Duration PRICE_FETCH_TIMEOUT = Duration.ofMillis(500);

    private final StringRedisTemplate redisTemplate;

    @Qualifier("priceFetchExecutor")
    private final Executor priceFetchExecutor;

    public List<HoldingResponse> getHoldings(
            Long memberId,
            Long roomParticipantId
    ) {
        validateId(memberId, "회원 ID");
        validateId(roomParticipantId, "방 참가자 ID");

        RoomParticipant participant = roomParticipantRepository
                .findById(roomParticipantId)
                .orElseThrow(this::participantNotFound);

        if (!participant.getMemberId().equals(memberId)) {
            throw participantNotFound();
        }

        List<Holding> holdings = holdingRepository
                .findAllByRoomParticipantId(roomParticipantId)
                .stream()
                .sorted(
                        Comparator.comparing(
                                Holding::getStockCode
                        )
                )
                .toList();

        // 보유 종목 하나마다 KIS를 개별 호출하면 레이트리밋에 바로 걸리므로,
        // 필요한 종목 코드를 한 번에 모아 배치로 조회한다.
        Map<String, BigDecimal> currentPrices = fetchCurrentPrices(
                holdings.stream().map(Holding::getStockCode).distinct().toList()
        );

        return holdings.stream()
                .map(holding ->
                        HoldingResponse.from(
                                holding,
                                resolveValuationPrice(holding, currentPrices)
                        )
                )
                .toList();
    }

    public HoldingSummaryResponse getHoldingSummary(
            Long memberId,
            Long roomParticipantId
    ) {
        List<HoldingResponse> holdings =
                getHoldings(
                        memberId,
                        roomParticipantId
                );

        return HoldingSummaryResponse.from(holdings);
    }

    private Map<String, BigDecimal> fetchCurrentPrices(
            List<String> stockCodes
    ) {
        if (stockCodes.isEmpty()) {
            return Map.of();
        }

        // KIS 호출은 백그라운드 스레드에서 시작하고, 여기서는 짧게만 기다린다.
        // 시간 안에 못 끝나도 그 호출은 계속 진행되어 캐시(price:quote:*)를 채워두므로
        // 다음 요청부터는 캐시로 즉시 응답할 수 있다.
        CompletableFuture<Map<String, BigDecimal>> future = CompletableFuture.supplyAsync(
                () -> currentPriceProvider.getCurrentPrices(stockCodes),
                priceFetchExecutor
        );

        try {
            return future.get(PRICE_FETCH_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            log.warn(
                    "KIS 다중 시세 조회가 {}ms 안에 끝나지 않아 종목별 캐시/매입가로 대체합니다. "
                            + "stockCodes={}",
                    PRICE_FETCH_TIMEOUT.toMillis(),
                    stockCodes
            );

            return Map.of();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return Map.of();
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();

            if (!(cause instanceof WebClientException) && !(cause instanceof IllegalStateException)) {
                throw new IllegalStateException("KIS 다중 시세 조회 실패", cause);
            }

            log.warn(
                    "KIS 다중 시세 조회 실패, 종목별 캐시/매입가로 대체합니다. "
                            + "stockCodes={}, message={}",
                    stockCodes,
                    cause.getMessage()
            );

            return Map.of();
        }
    }

    private BigDecimal resolveValuationPrice(
            Holding holding,
            Map<String, BigDecimal> currentPrices
    ) {
        String cacheKey =
                LATEST_PRICE_KEY_PREFIX + holding.getStockCode();

        BigDecimal currentPrice = currentPrices.get(holding.getStockCode());

        if (currentPrice != null) {
            redisTemplate.opsForValue().set(
                    cacheKey,
                    currentPrice.toPlainString()
            );

            return currentPrice;
        }

        BigDecimal cachedPrice = getCachedPrice(cacheKey);

        if (cachedPrice != null) {
            log.warn(
                    "KIS 현재가 조회 실패로 마지막 정상 시세를 사용합니다. "
                            + "stockCode={}, roomParticipantId={}, cachedPrice={}",
                    holding.getStockCode(),
                    holding.getRoomParticipantId(),
                    cachedPrice
            );

            return cachedPrice;
        }

        log.warn(
                "KIS 현재가와 캐시가 없어 평균 매입가를 임시 사용합니다. "
                        + "stockCode={}, roomParticipantId={}",
                holding.getStockCode(),
                holding.getRoomParticipantId()
        );

        return holding.getAvgPrice();
    }

    private BigDecimal getCachedPrice(String cacheKey) {
        try {
            String cachedValue =
                    redisTemplate.opsForValue().get(cacheKey);

            if (cachedValue == null || cachedValue.isBlank()) {
                return null;
            }

            BigDecimal cachedPrice =
                    new BigDecimal(cachedValue.trim());

            return cachedPrice.compareTo(BigDecimal.ZERO) > 0
                    ? cachedPrice
                    : null;
        } catch (RuntimeException exception) {
            log.warn(
                    "캐시된 현재가를 읽지 못했습니다. cacheKey={}, message={}",
                    cacheKey,
                    exception.getMessage()
            );

            return null;
        }
    }

    private IllegalArgumentException participantNotFound() {
        return new IllegalArgumentException(
                "보유 종목을 조회할 수 있는 참가자 정보를 찾을 수 없습니다."
        );
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + "가 올바르지 않습니다."
            );
        }
    }

}