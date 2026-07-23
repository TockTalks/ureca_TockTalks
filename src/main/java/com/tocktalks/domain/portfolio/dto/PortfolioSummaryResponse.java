package com.tocktalks.domain.portfolio.dto;

import com.tocktalks.domain.ranking.service.ReturnRateCalculator;
import com.tocktalks.domain.room.entity.Room;
import com.tocktalks.domain.room.entity.RoomParticipant;

import java.math.BigDecimal;

public record PortfolioSummaryResponse(
        Long roomParticipantId, Long roomId, String roomName, String roomStatus,
        Long balance, Long stockValuation, Long totalAssetValue, Long initialSeedMoney,
        Long profitAmount, BigDecimal profitRate, int holdingCount, boolean isDefault
) {
    public static PortfolioSummaryResponse of(
            RoomParticipant participant,
            Room room,
            long totalAssetValue,
            long stockValuation,
            int holdingCount
    ) {
       long profitAmount = totalAssetValue - participant.getInitialSeedMoney();
       BigDecimal profitRate = ReturnRateCalculator.calculate(
               totalAssetValue, participant.getInitialSeedMoney()
       );

       return new PortfolioSummaryResponse(
               participant.getId(),
               room.getId(),
               room.getName(),
               room.getStatus(),
               participant.getBalance(),
               stockValuation,
               totalAssetValue,
               participant.getInitialSeedMoney(),
               profitAmount,
               profitRate,
               holdingCount,
               room.isDefault()
       );
    }
}
