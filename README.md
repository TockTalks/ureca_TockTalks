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
