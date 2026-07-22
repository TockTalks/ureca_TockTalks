package com.tocktalks.domain.backoffice.repository;

import com.tocktalks.domain.backoffice.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

    boolean existsByStatDate(LocalDate statDate);

    List<DailyStats> findAllByOrderByStatDateDesc();
}
