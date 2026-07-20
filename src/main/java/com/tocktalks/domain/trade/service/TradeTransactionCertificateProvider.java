package com.tocktalks.domain.trade.service;

import com.tocktalks.domain.community.service.TransactionCertificateProvider;
import com.tocktalks.domain.trade.entity.TradeType;
import com.tocktalks.domain.trade.entity.Transaction;
import com.tocktalks.domain.trade.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Primary;

@Primary
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeTransactionCertificateProvider
        implements TransactionCertificateProvider {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionSnapshot certifySellTransaction(
            Long transactionId,
            Long memberId
    ) {
        validateId(transactionId, "거래 ID");
        validateId(memberId, "회원 ID");

        Transaction transaction = transactionRepository
                .findOwnedByIdAndMemberId(
                        transactionId,
                        memberId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "인증할 수 있는 거래를 찾을 수 없습니다."
                        )
                );

        if (transaction.getType() != TradeType.SELL) {
            throw new IllegalArgumentException(
                    "매도 거래만 투자 인증에 사용할 수 있습니다."
            );
        }

        return new TransactionSnapshot(
                transaction.getProfitAmount(),
                transaction.getProfitRate(),
                transaction.getStockCode()
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