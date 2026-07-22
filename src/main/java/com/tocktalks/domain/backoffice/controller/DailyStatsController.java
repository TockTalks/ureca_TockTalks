package com.tocktalks.domain.backoffice.controller;

import com.tocktalks.domain.backoffice.dto.response.DailyStatsResponse;
import com.tocktalks.domain.backoffice.service.DailyStatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/daily-stats")
@RequiredArgsConstructor
public class DailyStatsController {

    private final DailyStatsService dailyStatsService;

    @GetMapping
    public List<DailyStatsResponse> getDailyStats() {
        return dailyStatsService.getDailyStats();
    }
}
