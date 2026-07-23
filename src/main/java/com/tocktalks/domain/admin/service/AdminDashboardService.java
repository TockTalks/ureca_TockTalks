package com.tocktalks.domain.admin.service;

import com.tocktalks.domain.admin.dto.response.DashboardSummaryResponse;
import com.tocktalks.domain.member.repository.MemberRepository;
import com.tocktalks.domain.room.repository.RoomRepository;
import com.tocktalks.global.activity.ActiveMemberTracker;
import com.tocktalks.global.activity.OnlineUserTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final ActiveMemberTracker activeMemberTracker;
    private final OnlineUserTracker onlineUserTracker;

    public DashboardSummaryResponse getSummary() {
        return new DashboardSummaryResponse(
                memberRepository.count(),
                activeMemberTracker.countToday(),
                activeMemberTracker.countLast7Days(),
                roomRepository.count(),
                roomRepository.countByStatus("recruiting"),
                roomRepository.countByStatus("ongoing"),
                roomRepository.countByStatus("closed"),
                onlineUserTracker.getOnlineCount()
        );
    }
}