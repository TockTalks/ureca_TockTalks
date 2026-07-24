package com.tocktalks.domain.admin.controller;

import com.tocktalks.domain.admin.dto.request.NoticeCreateRequest;
import com.tocktalks.domain.admin.dto.response.NoticeResponse;
import com.tocktalks.domain.admin.service.NoticeService;
import com.tocktalks.global.security.LoginMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    
    // 새로운 공지사항 생성
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(
            @LoginMemberId Long adminId,
            @Valid @RequestBody NoticeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(noticeService.createNotice(adminId, request));
    }
    
    // 공지사항 목록을 페이징하여 처리
    @GetMapping
    public ResponseEntity<Page<NoticeResponse>> getNotices(
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(noticeService.getNotices(pageable));
    }
}
