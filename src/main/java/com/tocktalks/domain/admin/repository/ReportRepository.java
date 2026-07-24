package com.tocktalks.domain.admin.repository;

import com.tocktalks.domain.admin.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<Report> findByStatusAndTargetTypeOrderByCreatedAtDesc(String status, String targetType, Pageable pageable);

    Page<Report> findByStatusInOrderByResolvedAtDesc(List<String> statuses, Pageable pageable);

    Page<Report> findByStatusInAndTargetTypeOrderByResolvedAtDesc(List<String> statuses, String targetType, Pageable pageable);

    long countByStatus(String status);

    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, String targetType, Long targetId);
}