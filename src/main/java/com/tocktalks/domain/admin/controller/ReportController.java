package com.tocktalks.domain.admin.controller;

import com.tocktalks.domain.admin.dto.request.ReportCreateRequest;
import com.tocktalks.domain.admin.dto.response.ReportResponse;
import com.tocktalks.domain.admin.repository.ReportRepository;
import com.tocktalks.domain.admin.service.ReportService;
import com.tocktalks.global.security.LoginMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @LoginMemberId Long memberId,
            @Valid @RequestBody ReportCreateRequest request
    ) {
        reportService.createReport(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
