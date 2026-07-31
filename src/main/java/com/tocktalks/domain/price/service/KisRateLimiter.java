package com.tocktalks.domain.price.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

// 예전에는 팀원 전체가 KIS 앱키 하나를 나눠 쓰면서 여러 로컬 프로세스가 서로의 호출을
// 몰라 초당 제한을 넘기던 문제가 있어 Redis로 인스턴스 간 조율을 했다. 지금은 각자 개인
// 앱키를 쓰고 배포도 단일 인스턴스라, 이 프로세스 하나에서 나가는 호출 속도만 지키면
// 충분하다. 단건 조회와 배치 조회 모두 이 acquire()를 거치므로, 캐시가 식어 있는 상태에서
// 보유 종목 수십~수백 개를 한 번에 배치 조회(30개씩)하는 순간에도 이 페이싱 하나로
// KIS 실제 한도(20건/초)를 넘기지 않게 묶인다.
@Log4j2
@Component
public class KisRateLimiter {

    private static final long INTERVAL_MS = 70L; // 초당 약 14건 (KIS 한도 20건/초 대비 안전 마진)

    private long lastCallAt = 0L;

    public synchronized void acquire() {
        long now = System.currentTimeMillis();
        long gap = now - lastCallAt;

        if (gap < INTERVAL_MS) {
            try {
                Thread.sleep(INTERVAL_MS - gap);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("KIS 호출 대기 중 인터럽트 발생", e);
            }
        }

        lastCallAt = System.currentTimeMillis();
    }
}
