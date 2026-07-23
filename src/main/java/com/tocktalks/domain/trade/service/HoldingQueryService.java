package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.trade.dto.response.HoldingResponse;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.dto.response.HoldingSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

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

    private final StringRedisTemplate redisTemplate;

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

        return holdingRepository
                .findAllByRoomParticipantId(roomParticipantId)
                .stream()
                .sorted(
                        Comparator.comparing(
                                Holding::getStockCode
                        )
                )
                .map(holding ->
                        HoldingResponse.from(
                                holding,
                                getValuationPrice(holding)
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

    private BigDecimal getValuationPrice(
            Holding holding
    ) {
        String cacheKey =
                LATEST_PRICE_KEY_PREFIX + holding.getStockCode();

        try {
            BigDecimal currentPrice =
                    currentPriceProvider.getCurrentPrice(
                            holding.getStockCode()
                    );

            redisTemplate.opsForValue().set(
                    cacheKey,
                    currentPrice.toPlainString()
            );

            return currentPrice;
        } catch (WebClientException | IllegalStateException exception) {
            BigDecimal cachedPrice = getCachedPrice(cacheKey);

            if (cachedPrice != null) {
                log.warn(
                        "KIS 현재가 조회 실패로 마지막 정상 시세를 사용합니다. "
                                + "stockCode={}, roomParticipantId={}, cachedPrice={}, message={}",
                        holding.getStockCode(),
                        holding.getRoomParticipantId(),
                        cachedPrice,
                        exception.getMessage()
                );

                return cachedPrice;
            }

            log.warn(
                    "KIS 현재가와 캐시가 없어 평균 매입가를 임시 사용합니다. "
                            + "stockCode={}, roomParticipantId={}, message={}",
                    holding.getStockCode(),
                    holding.getRoomParticipantId(),
                    exception.getMessage()
            );

            return holding.getAvgPrice();
        }
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