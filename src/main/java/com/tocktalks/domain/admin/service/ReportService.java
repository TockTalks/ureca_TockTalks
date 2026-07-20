package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.response.ReportResponse;
import com.tocktalks.domain.admin.entity.Report;
import com.tocktalks.domain.admin.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_RESOLVED = "resolved";
    
    private final ReportRepository reportRepository;
    
    // 처리 대기 중인 신고 목록을 페이징하여 조회
    public Page<ReportResponse> getPendingRports(Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(STATUS_PENDING, pageable)
            .map(ReportResponse::from);
    }
    
    // 특정 신고를 처리 완료 상태로 변경
    @Transactional
    public void resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 내역입니다."));
        
        report.resolve();
    }
    
}
