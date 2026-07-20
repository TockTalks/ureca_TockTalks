package com.tocktalks.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest (
        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
        String content,
        String stockCode,
        Long transactionId
){}
