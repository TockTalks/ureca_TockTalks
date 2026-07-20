package com.tocktalks.domain.trade.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TradeOrderRequestTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory()
                    .getValidator();

    @Test
    void 올바른_종목_코드와_수량은_검증을_통과한다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        10L
                );

        Set<ConstraintViolation<TradeOrderRequest>>
                violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void 종목_코드는_6자리_숫자여야_한다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "5930",
                        10L
                );

        Set<ConstraintViolation<TradeOrderRequest>>
                violations = validator.validate(request);

        assertThat(violations)
                .extracting(
                        ConstraintViolation::getMessage
                )
                .contains(
                        "종목 코드는 6자리 숫자여야 합니다."
                );
    }

    @Test
    void 종목_코드에_문자가_포함되면_검증에_실패한다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "00A930",
                        10L
                );

        Set<ConstraintViolation<TradeOrderRequest>>
                violations = validator.validate(request);

        assertThat(violations)
                .extracting(
                        ConstraintViolation::getMessage
                )
                .contains(
                        "종목 코드는 6자리 숫자여야 합니다."
                );
    }

    @Test
    void 거래_수량은_필수이다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        null
                );

        Set<ConstraintViolation<TradeOrderRequest>>
                violations = validator.validate(request);

        assertThat(violations)
                .extracting(
                        ConstraintViolation::getMessage
                )
                .contains(
                        "거래 수량은 필수입니다."
                );
    }

    @Test
    void 거래_수량은_1_이상이어야_한다() {
        TradeOrderRequest request =
                new TradeOrderRequest(
                        "005930",
                        0L
                );

        Set<ConstraintViolation<TradeOrderRequest>>
                violations = validator.validate(request);

        assertThat(violations)
                .extracting(
                        ConstraintViolation::getMessage
                )
                .contains(
                        "거래 수량은 1 이상이어야 합니다."
                );
    }
}