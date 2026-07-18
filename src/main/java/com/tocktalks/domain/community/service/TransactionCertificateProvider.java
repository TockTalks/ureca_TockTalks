package com.tocktalks.domain.community.service;

import java.math.BigDecimal;

public interface TransactionCertificateProvider {

    TransactionSnapshot certifySellTransaction(Long transactionId, Long memberId);

    record TransactionSnapshot(BigDecimal profitAmount, BigDecimal profitRate, String stockCode){
    }
}
