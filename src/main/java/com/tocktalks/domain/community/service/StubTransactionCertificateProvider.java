package com.tocktalks.domain.community.service;

import java.math.BigDecimal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(TransactionCertificateProvider.class)
public class StubTransactionCertificateProvider implements TransactionCertificateProvider {

    @Override
    public TransactionSnapshot certifySellTransaction(Long transactionId, Long memberId) {
        return new TransactionSnapshot(
                BigDecimal.valueOf(10000),   // profitAmount 더미값
                BigDecimal.valueOf(5.0),     // profitRate 더미값 (5%)
                "005930"                     // stockCode 더미값 (예: 삼성전자)
        );
    }
}