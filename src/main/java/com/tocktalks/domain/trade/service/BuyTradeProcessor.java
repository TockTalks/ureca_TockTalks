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
public class BuyTradeProcessor {

    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction process(
            Long roomParticipantId,
            String stockCode,
            String stockName,
            long quantity,
            BigDecimal price
    ) {
        Holding holding = holdingRepository
                .findByRoomParticipantIdAndStockCode(
                        roomParticipantId,
                        stockCode
                )
                .map(existingHolding -> {
                    existingHolding.buy(
                            quantity,
                            price
                    );

                    return existingHolding;
                })
                .orElseGet(() ->
                        Holding.create(
                                roomParticipantId,
                                stockCode,
                                stockName,
                                quantity,
                                price
                        )
                );

        holdingRepository.save(holding);

        Transaction transaction =
                Transaction.createBuy(
                        roomParticipantId,
                        stockCode,
                        stockName,
                        quantity,
                        price
                );

        return transactionRepository.save(transaction);
    }
}