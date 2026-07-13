package com.talktocks.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // 서버 기동 확인용 - 나중에 지워도 됨
    @GetMapping("/api/health")
    public String health() {
        return "talktocks server is running";
    }
}
