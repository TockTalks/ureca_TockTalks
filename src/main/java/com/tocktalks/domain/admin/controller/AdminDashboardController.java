package com.tocktalks.domain.admin.controller;

import com.tocktalks.domain.admin.dto.response.DashboardCommunityResponse;
import com.tocktalks.domain.admin.dto.response.DashboardMembersTradesResponse;
import com.tocktalks.domain.admin.dto.response.DashboardRoomsRanksResponse;
import com.tocktalks.domain.admin.dto.response.DashboardSummaryResponse;
import com.tocktalks.domain.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(adminDashboardService.getSummary());
    }

    @GetMapping("/members-trades")
    public ResponseEntity<DashboardMembersTradesResponse> getMembersTrades(
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(adminDashboardService.getMembersTrades(topN));
    }

    @GetMapping("/rooms-ranks")
    public ResponseEntity<DashboardRoomsRanksResponse> getRoomsRanks(
            @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(adminDashboardService.getRoomsRanks(topN));
    }

    @GetMapping("/community")
    public ResponseEntity<DashboardCommunityResponse> getCommunity(
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(adminDashboardService.getCommunity(topN));
    }
}