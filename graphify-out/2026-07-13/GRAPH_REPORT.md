# Graph Report - .  (2026-07-13)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 129 nodes · 124 edges · 25 communities (23 shown, 2 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 2 edges (avg confidence: 0.95)
- Token cost: 583 input · 233 output

## Graph Freshness
- Built from commit: `4b77d753`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Security Configuration
- Member Data Access
- Global Exception Handling
- Notice Entity
- Report Entity
- Daily Statistics Entity
- Comment Entity
- Post Entity
- Post Like Entity
- Favorite Stock Entity
- Asset History Entity
- Room Ranking Archive
- Room Entity
- Room Participant Entity
- Stock Holding Entity
- Transaction Entity
- Health Check Controller
- Gradle Wrapper Scripts
- Application Entry Point
- Infrastructure Configuration
- External API Integrations

## God Nodes (most connected - your core abstractions)
1. `Member` - 8 edges
2. `Notice` - 5 edges
3. `Report` - 5 edges
4. `DailyStats` - 5 edges
5. `Comment` - 5 edges
6. `Post` - 5 edges
7. `PostLike` - 5 edges
8. `FavoriteStock` - 5 edges
9. `AssetHistory` - 5 edges
10. `RoomRankingArchive` - 5 edges

## Surprising Connections (you probably didn't know these)
- `MemberRepository` --references--> `Member`  [EXTRACTED]
  src/main/java/com/talktocks/domain/member/repository/MemberRepository.java → src/main/java/com/talktocks/domain/member/entity/Member.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Infrastructure Stack** — docker_compose_mysql, docker_compose_redis, src_main_resources_application_yml [EXTRACTED 0.90]

## Communities (25 total, 2 thin omitted)

### Community 0 - "Security Configuration"
Cohesion: 0.36
Nodes (7): Bean, Configuration, EnableWebSecurity, HttpSecurity, PasswordEncoder, SecurityFilterChain, SecurityConfig

### Community 1 - "Member Data Access"
Cohesion: 0.27
Nodes (7): JpaRepository, Entity, Getter, NoArgsConstructor, Table, Member, MemberRepository

### Community 2 - "Global Exception Handling"
Cohesion: 0.52
Nodes (4): ExceptionHandler, ResponseEntity, RestControllerAdvice, GlobalExceptionHandler

### Community 3 - "Notice Entity"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, Notice

### Community 4 - "Report Entity"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, Report

### Community 5 - "Daily Statistics Entity"
Cohesion: 0.33
Nodes (5): DailyStats, Entity, Getter, NoArgsConstructor, Table

### Community 6 - "Comment Entity"
Cohesion: 0.33
Nodes (5): Comment, Entity, Getter, NoArgsConstructor, Table

### Community 7 - "Post Entity"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, Post

### Community 8 - "Post Like Entity"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, PostLike

### Community 9 - "Favorite Stock Entity"
Cohesion: 0.33
Nodes (5): FavoriteStock, Entity, Getter, NoArgsConstructor, Table

### Community 10 - "Asset History Entity"
Cohesion: 0.33
Nodes (5): AssetHistory, Entity, Getter, NoArgsConstructor, Table

### Community 11 - "Room Ranking Archive"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, RoomRankingArchive

### Community 12 - "Room Entity"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, Room

### Community 13 - "Room Participant Entity"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, RoomParticipant

### Community 14 - "Stock Holding Entity"
Cohesion: 0.33
Nodes (5): Holding, Entity, Getter, NoArgsConstructor, Table

### Community 15 - "Transaction Entity"
Cohesion: 0.33
Nodes (5): Entity, Getter, NoArgsConstructor, Table, Transaction

### Community 16 - "Health Check Controller"
Cohesion: 0.60
Nodes (3): GetMapping, RestController, HealthController

### Community 17 - "Gradle Wrapper Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 20 - "External API Integrations"
Cohesion: 0.67
Nodes (3): Kakao OAuth API, Stock Market API, TalkTocks Application

## Knowledge Gaps
- **4 isolated node(s):** `MySQL Service`, `Redis Service`, `Stock Market API`, `Kakao OAuth API`
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What connects `MySQL Service`, `Redis Service`, `Stock Market API` to the rest of the system?**
  _4 weakly-connected nodes found - possible documentation gaps or missing edges._