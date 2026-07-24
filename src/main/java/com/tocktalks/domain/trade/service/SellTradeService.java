package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.portfolio.event.AssetSnapshotRequestedEvent;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.room.repository.RoomParticipantRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.domain.trade.dto.request.TradeOrderRequest;
import com.tocktalks.domain.trade.dto.response.TradeExecutionResponse;
import com.tocktalks.domain.trade.entity.StockCodeValidator;
import com.tocktalks.domain.trade.entity.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellTradeService {

    private final RoomParticipantRepository
            roomParticipantRepository;

    private final RoomRepository roomRepository;

    private final CurrentPriceProvider
            currentPriceProvider;

    private final SellTradeProcessor
            sellTradeProcessor;

    private final TradeRankingService
            tradeRankingService;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TradeExecutionResponse sell(
            Long memberId,
            Long roomParticipantId,
            TradeOrderRequest request
    ) {
        validateId(memberId, "회원 ID");
        validateId(
                roomParticipantId,
                "방 참가자 ID"
        );
        validateRequest(request);

        RoomParticipant participant =
                roomParticipantRepository
                        .findActiveForUpdate(
                                roomParticipantId,
                                memberId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "거래 가능한 참가자 정보를 찾을 수 없습니다."
                                )
                        );

        Room room = roomRepository
                .findById(participant.getRoomId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "거래할 방 정보를 찾을 수 없습니다."
                        )
                );

        TradeAvailabilityValidator.validate(
                room,
                LocalDateTime.now()
        );

        BigDecimal currentPrice =
                currentPriceProvider.getCurrentPrice(
                        request.stockCode()
                );

        long tradeAmount =
                TradeAmountCalculator.calculate(
                        currentPrice,
                        request.quantity()
                );

        Transaction transaction =
                sellTradeProcessor.process(
                        roomParticipantId,
                        request.stockCode(),
                        request.quantity(),
                        currentPrice
                );

        participant.deposit(tradeAmount);

        tradeRankingService.updateRanking(
                participant
        );

        log.info("[매도 체결] roomParticipantId={}, stockCode={}, quantity={}, price={}",
                roomParticipantId, request.stockCode(), request.quantity(), currentPrice);

        eventPublisher.publishEvent(new AssetSnapshotRequestedEvent(
                participant.getId(),
                participant.getMemberId(),
                participant.getBalance(),
                transaction.getId(),
                transaction.getStockCode(),
                transaction.getStockName(),
                transaction.getType().name(),
                transaction.getQuantity(),
                transaction.getPrice(),
                transaction.getProfitAmount(),
                transaction.getProfitRate()
        ));

        return TradeExecutionResponse.from(
                transaction,
                tradeAmount,
                participant.getBalance()
        );
    }

    private void validateRequest(
            TradeOrderRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "매도 요청은 필수입니다."
            );
        }

        StockCodeValidator.validate(
                request.stockCode()
        );

        if (request.quantity() == null
                || request.quantity() <= 0) {
            throw new IllegalArgumentException(
                    "거래 수량은 1 이상이어야 합니다."
            );
        }
    }

    private void validateId(
            Long id,
            String fieldName
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + "가 올바르지 않습니다."
            );
        }
    }
}