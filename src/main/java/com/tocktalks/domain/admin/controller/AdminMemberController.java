package com.tocktalks.domain.admin.controller;

import com.tocktalks.domain.admin.dto.response.AdminMemberResponse;
import com.tocktalks.domain.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    // 전체 회원 검색
    @GetMapping
    public ResponseEntity<Page<AdminMemberResponse>> getMembers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(adminMemberService.getMembers(keyword, pageable));
    }

    // 회원 상세 상태 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<AdminMemberResponse> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(adminMemberService.getMember(memberId));
    }

    // 회원 차단 처리
    @PostMapping("/{memberId}/block")
    public ResponseEntity<Void> blockMember(@PathVariable Long memberId) {
        adminMemberService.blockMember(memberId);
        return ResponseEntity.noContent().build();
    }

    // 회원 강제 탈퇴 처리 (비밀번호 확인 없이 관리자가 직접 처리)
    @PostMapping("/{memberId}/withdraw")
    public ResponseEntity<Void> withdrawMember(@PathVariable Long memberId) {
        adminMemberService.withdrawMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reported")
    public ResponseEntity<Page<AdminMemberResponse>> getReportedMembers(@PageableDefault(size = 20) Pageable pageable){
        return ResponseEntity.ok(adminMemberService.getReportedMembers(pageable));
    }

    @PostMapping("/{memberId}/reset-default-room")
    public ResponseEntity<Void> resetDefaultRoomAssets(@PathVariable Long memberId) {
        adminMemberService.resetDefaultRoomAssets(memberId);
        return ResponseEntity.noContent().build();
    }

}