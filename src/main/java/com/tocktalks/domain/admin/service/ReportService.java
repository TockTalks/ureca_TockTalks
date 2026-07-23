package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.request.ReportCreateRequest;
import com.tocktalks.domain.admin.dto.response.ReportResponse;
import com.tocktalks.domain.admin.entity.Report;
import com.tocktalks.domain.admin.repository.ReportRepository;
import com.tocktalks.domain.community.exception.CommunityException;
import com.tocktalks.domain.community.service.CommentService;
import com.tocktalks.domain.community.service.PostService;
import com.tocktalks.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_REJECTED = "rejected";
    private static final String STATUS_DELETED = "deleted";
    private static final List<String> HISTORY_STATUSES = List.of(STATUS_REJECTED, STATUS_DELETED);
    private static final List<String> VALID_TARGET_TYPES = List.of("POST", "COMMENT", "ROOM");
    
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;
    private final CommentService commentService;

    //게시글/댓글 신고 접수
    @Transactional
    public void createReport(Long reporterId, ReportCreateRequest request) {
        if (!VALID_TARGET_TYPES.contains(request.targetType())) {
            throw new IllegalArgumentException("지원하지 않는 신고 대상입니다: " + request.targetType());
        }
        String targetContent;
        try {
                targetContent = switch (request.targetType()) {
                case "POST" -> postService.getContentForReport(request.targetId());
                case "COMMENT" -> commentService.getContentForReport(request.targetId());
                default -> null;
            };
        } catch (CommunityException e) {
            throw new IllegalArgumentException("존재하지 않는 신고 대상입니다.");
        }

        reportRepository.save(Report.create(
                reporterId, request.targetType(), request.targetId(), request.targetMemberId(), request.reason(), targetContent
        ));
    }

    //처리 대기 중인 신고 목록을 페이징하여 조회
    public Page<ReportResponse> getPendingReports(String targetType, Pageable pageable) {
        Page<Report> reports = StringUtils.hasText(targetType) ?
                reportRepository.findByStatusAndTargetTypeOrderByCreatedAtDesc(STATUS_PENDING, targetType, pageable) :
                reportRepository.findByStatusOrderByCreatedAtDesc(STATUS_PENDING, pageable);
        return reports.map(ReportResponse::from);
    }

    public Page<ReportResponse> getReportHistory(String targetType, Pageable pageable){
        Page<Report> reports = StringUtils.hasText(targetType) ?
                reportRepository.findByStatusInAndTargetTypeOrderByCreatedAtDesc(HISTORY_STATUSES, targetType, pageable) :
                reportRepository.findByStatusInOrderByCreatedAtDesc(HISTORY_STATUSES, pageable);
        return reports.map(ReportResponse::from);
    }
    
    //반려 처리
    @Transactional
    public void rejectReport(Long reportId) {
        Report report = getPendingReportOrThrow(reportId);
        report.reject();
    }

    //신고당한 콘텐츠(글/댓글) 강제 삭제 후 reportedCount + 1
    @Transactional
    public void deleteReportedContent(Long reportId) {
        Report report = getPendingReportOrThrow(reportId);
        try {
            switch (report.getTargetType()) {
                case "POST" ->
                        postService.deletePostByAdmin(report.getTargetId());
                case "COMMENT" ->
                        commentService.deleteCommentByAdmin(report.getTargetId());
                default -> throw new IllegalArgumentException("강제 삭제를 지원하지 않는 신고대상입니다:" + report.getTargetType());
            }
        } catch (CommunityException e) {
            throw new IllegalArgumentException("이미 삭제되었거나 존재하지 않는 콘텐츠입니다.");
        }

        report.delete();
        memberRepository.findById(report.getTargetMemberId()).ifPresent(member -> member.increaseReportedCount());
    }

    private Report getPendingReportOrThrow(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 내역입니다."));
        if(!STATUS_PENDING.equals(report.getStatus())){
            throw new IllegalArgumentException("이미 처리된 신고입니다.");
        }

        return report;
    }
}
