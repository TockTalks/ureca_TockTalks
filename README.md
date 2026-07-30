# 📈 톡톡스 (TockTalks)

> (S)TOCK + TALKS
> 실시간 시세 기반 모의투자 배틀 백엔드

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.x-red)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-latest-blue)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-black)](https://jwt.io/)

---

## 📌 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [팀원 소개](#2-팀원-소개)
3. [기술 스택](#3-기술-스택)
4. [시스템 아키텍처](#4-시스템-아키텍처)
5. [ERD](#5-erd)
6. [핵심 기능](#6-핵심-기능)
   - [팀 공유 Redis 최적화](#6-1-팀-공유-redis-최적화)
   - [동시성 제어 - 매수/매도](#6-2-동시성-제어--매수매도)
   - [실시간 시세 파이프라인](#6-3-실시간-시세-파이프라인)
   - [장애 대응 (Fail-open)](#6-4-장애-대응-fail-open)
   - [인증 / 보안](#6-5-인증--보안)
7. [인프라 & 배포](#7-인프라--배포)
8. [트러블슈팅](#8-트러블슈팅)
9. [AI 협업 개발 방식](#9-ai-협업-개발-방식)

---

## 1. 프로젝트 소개

### 배경

기존 모의투자 서비스는 대부분 개인이 혼자 가상 자산으로 매매 연습을 하는 데 그칩니다. 톡톡스는 유저가 직접 **"방(Room)"**을 만들어 시드머니와 기간을 정하면, 같은 조건에서 시작한 사람들끼리 실시간 시세를 기반으로 수익률을 겨루는 서비스입니다.

### 목표

| 목표 | 해결 기술 |
|---|---|
| 팀 전체가 외부 API 키 하나를 공유해도 안정적으로 개발 | Redis + Lua 스크립트 기반 분산 Rate Limiter |
| 동시 매수/매도에서도 잔고·보유 수량 정합성 보장 | 참가자 단위 비관적 락(`PESSIMISTIC_WRITE`) + 트랜잭션 경계 |
| 실시간 시세를 다수 사용자에게 효율적으로 전달 | KIS WebSocket → Redis Pub/Sub → STOMP 팬아웃 |
| 외부 API·캐시 인프라 장애에도 서비스 지속 | Fail-open 캐시 폴백, Redis 타임아웃 단축 |

### 프로젝트 기간

```
2026.07.16 ~ 2026.07.27 (2주)
```

---

## 2. 팀원 소개

| 역할 | 이름 | 담당 도메인 |
|---|---|---|
| 팀장 | 이진희 | Portfolio, Backoffice — 포트폴리오, 평가손익, 통계 스냅샷/대시보드 |
| 팀원 | 윤태형 | Auth, Room — 회원가입/로그인, JWT, 카카오 소셜로그인, WebSocket 인증, 모의투자 배틀 |
| 팀원 | 안제홍 | Ranking, Community — Redis 실시간 랭킹, 게시글/댓글/인증카드, 방 아카이브 |
| 팀원 | 최재웅 | Price, Member — 외부 시세 API 연동, WebSocket 시세 브로드캐스트, 관심종목 설정 |
| 팀원 | 박지훈 | Trade — 매수/매도 동시성 제어 |

---

## 3. 기술 스택

### Backend

| 분류 | 기술 |
|---|---|
| 언어 / 프레임워크 | Java 21, Spring Boot 4.1, Gradle |
| ORM | Spring Data JPA |
| 인증 | Spring Security, JWT |
| 분산 캐시 / 락 / Pub-Sub | Redis 7 |
| 실시간 통신 | Spring WebSocket + STOMP |
| 소셜 로그인 | Kakao OAuth2 |

### Database

| 분류 | 기술 |
|---|---|
| RDBMS | MySQL 8.0 |
| 캐시 / 세션 / 분산락 스토어 | Redis 7 (Upstash) |

### Infra

| 분류 | 기술 |
|---|---|
| 컨테이너 | Docker, Docker Compose |
| 외부 API | 한국투자증권(KIS) Open API |

### 협업 도구

| 분류 | 도구 |
|---|---|
| AI 개발 도구 | Claude Code |

---

## 4. 시스템 아키텍처

```
[클라이언트(웹)]
        │ REST / STOMP over WebSocket
        ▼
[Spring Boot Application]
   ├─ Controller 계층 (도메인별 API)
   ├─ Service 계층 (비즈니스 로직 · 트랜잭션)
   ├─ Repository 계층 (JPA 데이터 접근)
   │
   ├─▶ [MySQL]   회원 / 방 / 거래 / 커뮤니티 등 영속 데이터
   └─▶ [Redis]   랭킹 Sorted Set, 시세 캐시, 인증 토큰 캐시,
                 분산 Rate Limiter, 접속자 집계, Pub/Sub

[한국투자증권 KIS Open API] ◀─ WebSocket(실시간 시세) / REST(현재가·차트)
[카카오 로그인 API]        ◀─ OAuth2
```

### 로컬/팀 공유 개발 환경

```
Docker 환경
   ├─ MySQL (local 프로필: 로컬 컨테이너 / shared 프로필: 팀 공유 AWS RDS)
   └─ Redis (local 프로필: 로컬 컨테이너 / shared 프로필: 팀 공유 Upstash)
```

---

## 5. ERD

전체 컬럼을 포함한 정식 ERD 대신, 도메인 간 관계를 단순화한 다이어그램입니다.

```
회원(MEMBER)
  ├─▶ 관심종목(FAVORITE_STOCK)
  ├─▶ 게시글(POST) ──▶ 댓글(COMMENT) ──▶ 댓글좋아요(COMMENT_LIKE)
  │         └────────▶ 게시글좋아요(POST_LIKE)
  ├─▶ 신고(REPORT)
  ├─▶ 공지(NOTICE)                [관리자 권한으로 작성]
  └─▶ 방참가(ROOM_PARTICIPANT) ◀── 방(ROOM)
              │
              ├─▶ 보유종목(HOLDING)
              ├─▶ 거래내역(TRANSACTION)
              ├─▶ 자산 변동 이력(ASSET_HISTORY)
              └─▶ 방 종료 시 ─┬─▶ 랭킹 아카이브(ROOM_RANKING_ARCHIVE)
                              └─▶ 보유종목 아카이브(HOLDING_ARCHIVE)

일일 통계(DAILY_STATS)  [스케줄러가 가입자/방/거래/게시글 수 집계]
```

---

## 6. 핵심 기능

---

### 6-1. 팀 공유 Redis 최적화

#### 배경

한국투자증권(KIS) Open API는 계정 하나·앱키 하나를 팀 전체가 공유합니다. KIS 공식 호출 제한은 계좌당 초당 20건이지만, 팀원 여러 명이 같은 키로 시세 조회·인증·차트 등 여러 API를 동시에 쓰다 보니 실제로는 훨씬 낮은 수준에서도 제한에 걸렸습니다.

#### 왜 Redis + Lua 스크립트인가

| 방식 | 문제점 |
|---|---|
| 각 서버가 로컬 변수로만 페이싱 | 팀원 간 조율 불가 — 합산하면 여전히 제한 초과 |
| Redis GET → 계산 → SET을 자바 코드에서 순차 실행 | 두 프로세스가 동시에 GET 하면 같은 옛날 값을 읽어 레이스 컨디션 발생 |
| **Redis + Lua 스크립트 ✅** | "조회 + 계산 + 갱신"이 원자적으로 처리되어 여러 인스턴스가 동시에 호출해도 정확히 순서대로 시간표 배정 |

```lua
-- "다음 호출 가능 시각"을 원자적으로 조회하고, 곧바로 갱신한다
local nextAllowedAt = tonumber(redis.call('GET', KEYS[1]) or '0')
local base = now > nextAllowedAt and now or nextAllowedAt
redis.call('SET', KEYS[1], base + interval, 'PX', interval * 4)
return base - now   -- 이만큼 기다렸다가 호출하라고 응답
```

Redis 장애 시에는 이 분산 조율 로직이 JVM 로컬 페이싱으로 자동 폴백됩니다 (조율은 못 하지만, 각 프로세스는 여전히 자기 호출 속도를 지킴).

#### 그 밖의 최적화

| 기법 | 내용 |
|---|---|
| 인증 토큰 2단 캐시 | KIS accessToken(1h)/approvalKey(12h)를 Redis로 공유하되, 60초 JVM 로컬 캐시로 반복 조회 최소화 |
| MGET · 파이프라이닝 | 여러 종목 시세를 한 번의 네트워크 왕복으로 조회/저장 (원격 Redis는 호출마다 RTT 비용 발생) |
| 쓰기 스로틀링 | 초당 여러 번 오는 시세 틱을 5초 간격으로 묶어서 기록, 커맨드 수 폭증 방지 |

> 이후 팀원 각자가 개인 API 키를 발급받는 방식으로 전환하면서, 분산 조율 자체의 실질적 필요성은 줄었습니다. 다만 appKey 단위로 스코프를 분리해두어, 키를 다시 공유하거나 인스턴스를 늘리는 상황에도 안전하게 동작합니다.

---

### 6-2. 동시성 제어 — 매수/매도

같은 방에서 여러 참가자가 동시에 매수·매도를 요청해도 잔고·보유 수량·거래 내역이 항상 정확해야 합니다.

#### 왜 비관적 락인가

| 구분 | 낙관적 락 | 분산 락(Redis) | 비관적 락 ✅ |
|---|---|---|---|
| 관리 주체 | JPA `@Version` | Redis | DB (InnoDB) |
| 실패 전략 | 재시도 필요 (경합 심하면 재시도 폭발) | 별도 인프라 의존 | DB가 순서를 보장, 대기 후 처리 |
| 이 프로젝트 적합도 | 거래 경합이 잦아 재시도 비용 큼 | 이미 DB 트랜잭션으로 처리 가능해 오버엔지니어링 | ✅ 참가자 단위로 좁게 걸어 병목 최소화 |

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT rp FROM RoomParticipant rp WHERE rp.id = :id AND rp.memberId = :memberId AND rp.status = 'ACTIVE'")
Optional<RoomParticipant> findActiveForUpdate(Long id, Long memberId);
```

락 범위를 **참가자 단위**로 좁혀서, 같은 방의 다른 참가자끼리는 서로 막지 않고 병렬로 처리됩니다.

#### 동시성 테스트 검증

```
테스트: 같은 참가자가 보유한 주식 1주를 동시에 두 번 매도 요청

ExecutorService로 스레드 2개 생성
CountDownLatch 2단 신호(readySignal → startSignal)로 "정확히 같은 순간" 출발 강제

결과: 정확히 1건만 성공, 나머지 1건은 예외로 실패
      DB 최종 잔고 · 보유 수량 · 거래 내역까지 일관성 확인
```

Mock이 아니라 MySQL Testcontainer 위에서 실제 스레드로 검증했습니다.

---

### 6-3. 실시간 시세 파이프라인

KIS와의 WebSocket 연결은 계정당 세션이 제한적이라 서버당 하나만 유지하고, Redis Pub/Sub으로 팬아웃합니다.

```
KIS(WebSocket) → 백엔드(PUBLISH price:{종목코드}) → Redis Pub/Sub
      → 백엔드(SUBSCRIBE) → STOMP(/topic/price/{종목코드}) → 브라우저(다수)
```

#### 왜 WebSocket(+STOMP)인가

| 방식 | 특징 |
|---|---|
| Polling | 요청 간격만큼 지연 발생, 사용자·종목이 늘수록 서버 부하 급증 |
| SSE | 단방향 스트리밍만 가능 |
| **WebSocket + STOMP ✅** | 양방향, Spring의 STOMP pub/sub가 Redis 채널 구조와 자연스럽게 맞물림, 추후 채팅처럼 진짜 양방향 기능 확장 여지 |

시청자 수와 무관하게 **종목당 KIS 구독은 하나만 유지**되며(마지막 시청자가 나가야 구독 해제), REST 조회도 8초 캐시로 묶여 사용자 증가가 KIS 호출량에 거의 영향을 주지 않습니다.

---

### 6-4. 장애 대응 (Fail-open)

KIS와 Redis 각각의 장애 상황에 대해 별도로 대비했습니다.

| | Redis 정상 | Redis 장애 |
|---|---|---|
| **KIS 정상** | 캐시 조회 → 없으면 KIS 호출 → 캐싱 | 캐시 조회를 건너뛰고 KIS 직접 호출로 계속 진행 |
| **KIS 장애** | Redis에 보관된 마지막 성공 시세로 폴백 | 폴백할 데이터 자체가 없어 오류 응답 |

- Redis 커맨드 타임아웃을 60초 → 3초로 단축해, 순간 장애가 전체 서비스 지연으로 번지지 않도록 방어
- 인증 토큰 폐기 여부 확인처럼 매 요청마다 거치는 Redis 의존 체크는 fail-open 처리 — Redis 장애가 로그인 상태 자체를 막지 않도록 함

---

### 6-5. 인증 / 보안

- 자체 로그인(BCrypt) + 카카오 OAuth2 소셜 로그인
- Access Token(1시간, JWT) + Refresh Token(14일, Redis 저장) 구조, 재발급 시 이전 Refresh Token 자동 무효화(1회용 로테이션)
- 이메일 기준 로그인 실패 잠금(5회 실패 시 5분, Redis 공유 카운터 — 인스턴스가 바뀌어도 방어 유지)
- 회원탈퇴 시 Access/Refresh Token 즉시 무효화

---

## 7. 인프라 & 배포

이번 프로젝트는 별도 클라우드 프로덕션 배포 없이, **로컬 및 팀 공유(Docker) 환경**에서 개발·검증했습니다.

```
로컬 개발 (local 프로필)
   └─ docker-compose up -d  →  로컬 MySQL / Redis

팀 공유 개발 (shared 프로필)
   └─ 팀 전체가 같은 AWS RDS(MySQL) · Upstash(Redis)를 함께 봄
```

- CI/CD 파이프라인은 구성하지 않았습니다.
- 별도 컨테이너 오케스트레이션(EC2 등) 배포 대신, 로컬/팀 공유 환경 검증에 집중했습니다.

---

## 8. 트러블슈팅

### ① KIS API 키 초과

**문제**: 팀 전체가 KIS Open API 키를 하나만 공유해서 사용하다가, 초당 요청 제한을 자주 초과해 API 요청이 거부되는 상황이 반복됨

**원인**: KIS의 호출 제한은 appKey 단위로 걸리는데, 팀 전체가 키 하나를 같이 쓰다 보니 각자는 몰라도 합쳐서 쉽게 한도를 넘김

**해결**: 처음엔 Redis 기반 분산 Rate Limiter로 팀 전체의 초당 호출량을 조율했으나, 하나의 키로는 팀 전체 테스트 트래픽 자체를 감당하기 어려워 최종적으로는 각자 개별 API 키를 발급받는 방식으로 전환. 이후에도 Redis 조율 로직은 appKey 단위로 스코프를 분리해 그대로 유지

### ② Redis 장애가 전체 서비스 장애로 전파

**문제**: Redis 연결이 잠깐 끊기면 인증 등 모든 요청이 최대 60초씩 멈추며 사이트 전체가 응답 없음 상태가 됨

**원인**: JWT 인증 필터가 모든 요청마다 Redis를 거치는데, 기본 커맨드 타임아웃(60초)이 너무 길어서 그 사이 들어온 요청이 전부 대기하다 실패로 이어짐

**해결**: Redis 커맨드 타임아웃을 3초로 단축. 인증 토큰 폐기 확인, 시세 캐시 조회 등 요청마다 거치는 Redis 의존 로직에 fail-open 처리를 추가해, Redis 장애가 요청 자체를 막지 않도록 함

### ③ 방 종료 처리의 체크-후-저장 레이스 컨디션

**문제**: 방을 종료 처리하는 경로가 스케줄러(만료 방 자동 종료), 관리자 강제 종료, 유저 요청 시 지연(lazy) 체크까지 3곳이라, 같은 방에 대해 동시에 여러 경로가 실행되면 랭킹 아카이브가 중복 저장될 여지가 있음

**원인**: "이미 archive됐는지 확인 → 저장"이 하나의 원자적 연산으로 묶여있지 않고, DB 유니크 제약도 걸려있지 않음

**해결**: 아직 해결하지 않은 상태로, 알려진 한계로 남겨두고 있음 — Redis Rate Limiter에 적용한 것과 같은 Lua 스크립트/유니크 제약 방식으로 보강할 예정

---

## 9. AI 협업 개발 방식

이번 프로젝트는 개발 중간 점검과 마무리 단계에서 **Claude Code**를 활용했습니다.

- 코드베이스 전반의 아키텍처/보안 리뷰 (동시성 처리, 인증 흐름, Redis 장애 대응 등을 실제 코드 기준으로 검증)
- 발견된 이슈에 대한 실제 수정 (예: Redis 장애 시 시세 조회 fail-open 처리)
- 발표 자료·문서(본 README 포함) 작성 시 기술적 근거를 코드로 직접 확인하며 정리

AI가 제안한 내용은 실제 코드를 다시 읽어 검증한 뒤에만 반영했으며, 특히 "왜 이렇게 설계했는가"에 대한 설명은 커밋 히스토리와 코드 주석을 근거로 재구성했습니다.

---

> **TockTalks Team**
