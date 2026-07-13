<div align="center">

# 📈 톡톡스 (TalkTocks)
### (S)TOCK + TALKS

**실시간 시세 기반 모의투자 배틀 & 투자 커뮤니티**

방을 만들고, 같은 시드머니로 시작해서, 정해진 기간 동안 수익률로 경쟁하세요.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white)](https://gradle.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![JWT](https://img.shields.io/badge/JWT-black?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Kakao](https://img.shields.io/badge/Kakao%20Login-FFCD00?logo=kakaotalk&logoColor=black)](https://developers.kakao.com/)
[![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)](https://github.com/features/actions)
[![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?logo=intellijidea&logoColor=white)](https://www.jetbrains.com/idea/)

</div>

---

## 💡 소개

**톡톡스**는 실제 증권/암호화폐 시세를 실시간으로 반영한 모의투자 서비스입니다.

기존 모의투자 서비스와 다르게, 유저가 직접 **"방(Room)"**을 만들어 시드머니와 기간을 정하고, 같은 조건에서 시작한 사람들끼리 수익률로 경쟁합니다. 거래 내역은 커뮤니티에 **투자 인증 카드**로 공유할 수 있어 신뢰도 있는 투자 인증 문화를 만듭니다.

## ✨ 주요 기능

| 기능 | 설명 |
|---|---|
| 🔐 회원가입 / 로그인 | 자체 로그인 + 카카오 소셜 로그인 |
| 💰 모의투자 | 실시간 시세 기반 매수/매도, 동시성 제어 |
| 🏆 방(Room) 배틀 | 시드머니·기간 설정, 공개/비공개 방, 실시간 랭킹 |
| 📊 포트폴리오 | 보유 종목, 평가손익, 자산 변동 히스토리 |
| 💬 커뮤니티 | 종목 태그 게시글, 실거래 기반 투자 인증 카드 |
| 🛠️ 백오피스 | 가입자·거래·랭킹 통계 대시보드 |

## 🧱 기술 스택

**Backend**
- Java 21 / Spring Boot 4.1 / Gradle
- Spring Data JPA / MySQL 8.0
- Spring Security / JWT
- Spring WebSocket (실시간 시세·랭킹)
- Redis (실시간 랭킹 Sorted Set, Pub/Sub, 세션)

**Infra**
- Docker / Docker Compose
- GitHub Actions (CI/CD)

**External API**
- 한국투자증권 Open API / 업비트·바이낸스 (실시간 시세)
- Kakao OAuth2 (소셜 로그인)

## 🏗️ 아키텍처

```
Client (WebSocket + REST)
        │
        ▼
Spring Boot Application
   ├── Auth        (JWT 발급/검증)
   ├── Room         (방 생성/참가/랭킹)
   ├── Trade        (매수/매도, 동시성 제어)
   ├── Price         (외부 시세 API 연동, Redis 캐싱)
   ├── Ranking      (Redis Sorted Set 실시간 랭킹)
   ├── Community    (게시글/댓글/인증카드)
   └── Backoffice   (통계 대시보드)
        │
        ├── MySQL (영속 데이터)
        └── Redis  (실시간 캐시 · Pub/Sub)
```

## 📁 프로젝트 구조

```
talktocks/
├── src/main/java/com/talktocks/
│   ├── global/            # 공통 설정 (Security, Exception 등)
│   └── domain/
│       ├── member/        # 회원
│       ├── room/          # 방(모의투자 배틀)
│       ├── trade/         # 매수/매도, 거래내역
│       ├── price/         # 실시간 시세
│       ├── ranking/       # 랭킹
│       ├── portfolio/     # 포트폴리오
│       ├── community/     # 게시글/댓글
│       ├── admin/         # 관리자
│       └── backoffice/    # 통계 대시보드
├── docker-compose.yml     # 로컬 MySQL + Redis
└── Dockerfile
```

## 🚀 시작하기

### 1. 저장소 클론

```bash
git clone https://github.com/TockTalks/ureca_TockTalks.git
cd ureca_TockTalks
```

### 2. 환경변수 설정

프로젝트 루트에 `.env` 파일을 만들고 API 키를 채워주세요.

```bash
STOCK_API_APP_KEY=your_key
STOCK_API_APP_SECRET=your_secret
KAKAO_CLIENT_ID=your_client_id
KAKAO_REDIRECT_URI=your_redirect_uri
```

### 3. MySQL / Redis 실행

```bash
docker-compose up -d
```

### 4. 애플리케이션 실행

IntelliJ에서 `TalktocksApplication` 실행, 또는:

```bash
./gradlew bootRun
```

### 5. 확인

```
http://localhost:8080/api/health
```

## 🗺️ 개발 로드맵

- [x] 프로젝트 뼈대 세팅 (도메인 구조, ERD, Docker 환경)
- [ ] **1차** — 회원가입/로그인, 기본방 모의투자, 실시간 시세, 랭킹, 커뮤니티
- [ ] **2차** — 유저 방 개설/참가, 방별 배틀 시스템, 방 아카이브
- [ ] **챌린지** — 백오피스 통계 대시보드

## 👥 팀원

| 이름 | GitHub | 역할 |
|---|---|---|
| | | |
| | | |
| | | |
| | | |
| | | |

## 📄 라이선스

이 프로젝트는 백엔드 부트캠프 미니프로젝트로 제작되었습니다.
