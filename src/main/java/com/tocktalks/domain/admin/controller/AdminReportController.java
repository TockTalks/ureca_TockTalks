package com.tocktalks.domain.admin.controller;

import com.tocktalks.domain.admin.dto.response.ReportResponse;
import com.tocktalks.domain.admin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private final ReportService reportService;
    
    // 관리자가 처리해야 할 대기 중인 신고 목록을 페이지 단위로 조회
    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReports(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(reportService.getPendingRports(pageable));
    }
    
    // 관리자가 특정 신고 건을 확인 후 '처리 완료' 상태로 변경
    @PatchMapping("/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(@PathVariable Long reportId) {
        reportService.resolveReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
