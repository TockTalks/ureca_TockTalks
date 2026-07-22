package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.response.ReportResponse;
import com.tocktalks.domain.admin.entity.Report;
import com.tocktalks.domain.admin.repository.ReportRepository;
import com.tocktalks.domain.community.repository.CommentRepository;
import com.tocktalks.domain.community.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    
    //처리 대기 중인 신고 목록을 페이징하여 조회
    public Page<ReportResponse> getPendingRports(Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(STATUS_PENDING, pageable)
            .map(ReportResponse::from);
    }
    
    //특정 신고를 처리 완료 상태로 변경
    @Transactional
    public void resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 내역입니다."));
        
        report.resolve();
    }

    //신고당한 콘텐츠(글/댓글) 강제 삭제 후 신고 완료 처리
    @Transactional
    public void deleteReportedContent(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 내역입니다."));

        switch (report.getTargetType()) {
            case "POST" -> {
                if (!postRepository.existsById(report.getTargetId())) {
                    throw new IllegalArgumentException("이미 삭제된 게시글입니다.");
                }
                postRepository.deleteById(report.getTargetId());
            }
            case "COMMENT" -> {
                if (!commentRepository.existsById(report.getTargetId())) {
                    throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
                }
                commentRepository.deleteById(report.getTargetId());
            }
            default -> throw new IllegalArgumentException(
                    "강제 삭제를 지원하지 않는 신고대상입니다: " + report.getTargetType()
            );
        }
        report.resolve();
    }
    
}
