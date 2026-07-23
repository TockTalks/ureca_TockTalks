package com.tocktalks.domain.admin.dto.response;

import java.math.BigDecimal;

public record TopUserResponse(
        Long memberId,
        String nickname,
        Long roomId,
        BigDecimal returnRate
) {
}