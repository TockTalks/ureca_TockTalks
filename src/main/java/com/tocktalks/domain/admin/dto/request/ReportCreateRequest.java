package com.tocktalks.domain.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportCreateRequest(
        @NotBlank String targetType,
        @NotNull Long targetId,
        @NotNull Long targetMemberId,
        @NotBlank String reason
) {}
