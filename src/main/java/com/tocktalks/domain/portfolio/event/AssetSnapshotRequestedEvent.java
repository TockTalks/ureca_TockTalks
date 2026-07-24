package com.tocktalks.domain.portfolio.event;

import java.math.BigDecimal;

//거래(매수/매도)이후 자산 스냅샷 기록을 요청하는 이벤트
public record AssetSnapshotRequestedEvent(
        Long roomParticipantId,
        Long memberId,
        Long balance,
        Long transactionId,
        String stockCode,
        String stockName,
        String tradeType,
        Long quantity,
        BigDecimal price,
        BigDecimal profitAmount,
        BigDecimal profitRate
) {
}
