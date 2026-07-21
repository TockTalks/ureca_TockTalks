package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.room.entity.RoomParticipant;
import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeAssetService {

    private final HoldingRepository holdingRepository;
    private final CurrentPriceProvider currentPriceProvider;

    public long calculateTotalAsset(
            RoomParticipant participant
    ) {
        validateParticipant(participant);

        List<Holding> holdings =
                holdingRepository.findAllByRoomParticipantId(
                        participant.getId()
                );

        long totalAsset = participant.getBalance();

        for (Holding holding : holdings) {
            BigDecimal currentPrice =
                    currentPriceProvider.getCurrentPrice(
                            holding.getStockCode()
                    );

            long valuation =
                    TradeAmountCalculator.calculate(
                            currentPrice,
                            holding.getQuantity()
                    );

            try {
                totalAsset = Math.addExact(
                        totalAsset,
                        valuation
                );
            } catch (ArithmeticException exception) {
                throw new IllegalArgumentException(
                        "총자산이 허용 범위를 초과합니다.",
                        exception
                );
            }
        }

        return totalAsset;
    }

    private void validateParticipant(
            RoomParticipant participant
    ) {
        if (participant == null
                || participant.getId() == null
                || participant.getId() <= 0) {
            throw new IllegalArgumentException(
                    "방 참가자 정보가 올바르지 않습니다."
            );
        }

        if (participant.getBalance() == null
                || participant.getBalance() < 0) {
            throw new IllegalArgumentException(
                    "방 참가자의 잔액이 올바르지 않습니다."
            );
        }
    }
}