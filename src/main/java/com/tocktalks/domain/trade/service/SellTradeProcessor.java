package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.trade.entity.Holding;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.HoldingRepository;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SellTradeProcessor {

    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction process(
            Long roomParticipantId,
            String stockCode,
            long quantity,
            BigDecimal price
    ) {
        Holding holding = holdingRepository
                .findForUpdate(
                        roomParticipantId,
                        stockCode
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "매도할 보유 종목을 찾을 수 없습니다."
                        )
                );

        BigDecimal avgPrice = holding.getAvgPrice();
        String stockName = holding.getStockName();

        holding.sell(quantity);

        if (holding.isEmpty()) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        Transaction transaction =
                Transaction.createSell(
                        roomParticipantId,
                        stockCode,
                        stockName,
                        quantity,
                        price,
                        avgPrice
                );

        return transactionRepository.save(transaction);
    }
}