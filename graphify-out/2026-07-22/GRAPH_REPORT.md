# Graph Report - C:\Users\howar\Downloads\ureca_TockTalks  (2026-07-22)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1599 nodes · 3518 edges · 103 communities (69 shown, 34 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 325 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `681d62d2`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- TransactionRepository
- RoomService
- RankingService
- TradeController.java
- StockMasterService
- KisAuthService
- Transaction
- JpaRepository
- ReportService
- PortfolioService.java
- HoldingQueryService
- JwtProvider
- TradeOrderRequest
- NoticeController.java
- AuthController
- RankingSubscriber
- AdminMemberController.java
- AuthService
- CommentService
- Room
- PostController
- Comment
- KakaoOAuthClient
- PostLike
- PostService
- CommentController.java
- RoomParticipant
- .calculate
- Member
- KisWebSocketClient
- PriceSubscriptionTracker
- LoginMemberIdArgumentResolver.java
- RoomParticipantRepository
- RoomRepository
- BuySellConcurrencyTest.java
- LoginAttemptService
- Transaction.java
- Post
- CommentLike
- PriceController
- MySqlTestContainerConfiguration.java
- DailyPriceResponse
- CommunityErrorCode
- TradeSecurityIntegrationTest
- SecurityConfig.java
- README.md
- WebSocketConfig.java
- KisChartService
- TradeRankingService
- TradeOrderRequestTest
- CommunityExceptionHandler.java
- PostRepository
- RedisConfig.java
- SellTradeConcurrencyTest.java
- SellTradeService
- KisRealtimeSubscribeRequest.java
- .parsesRealtimeData
- BuyTradeConcurrencyTest.java
- CorsConfig.java
- application.yml
- Spring Shared Datasource Configuration
- PricePublisher
- RoomProperties
- TockTalksApplication
- HealthController.java
- gradlew
- SHARED_REDIS_URL
- EmailCheckResponse.java
- KakaoLoginRequest.java
- LoginRequest.java
- ReissueRequest.java
- SignupRequest.java
- TokenResponse.java
- Entity
- ExceptionHandler
- GetMapping
- Getter
- NoArgsConstructor
- RestController
- RestControllerAdvice
- PatchMapping
- PostMapping
- PasswordEncoder
- ResponseEntity
- AssetHistoryResponse
- HoldingSummaryResponse
- RoomRepository
- TradeAssetService
- ObjectMapper
- Override
- StringRedisTemplate
- Query
- MemberRepository
- RankingService
- RoomRepository
- Query
- Bean
- Configuration
- PasswordEncoder
- Table

## God Nodes (most connected - your core abstractions)
1. `RoomParticipantRepository` - 43 edges
2. `TransactionRepository` - 42 edges
3. `RoomRepository` - 38 edges
4. `HoldingRepository` - 36 edges
5. `RoomService` - 36 edges
6. `TradeOrderRequest` - 35 edges
7. `AuthService` - 24 edges
8. `Transaction` - 22 edges
9. `BuyTradeService` - 22 edges
10. `KisWebSocketClient` - 22 edges

## Surprising Connections (you probably didn't know these)
- `CommentController` --references--> `CommentLikeService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/controller/CommentController.java → src/main/java/com/tocktalks/domain/community/service/CommentLikeService.java
- `CommentController` --references--> `CommentService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/controller/CommentController.java → src/main/java/com/tocktalks/domain/community/service/CommentService.java
- `PostController` --references--> `PostLikeService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/controller/PostController.java → src/main/java/com/tocktalks/domain/community/service/PostLikeService.java
- `PostController` --references--> `PostService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/controller/PostController.java → src/main/java/com/tocktalks/domain/community/service/PostService.java
- `CommentLikeService` --references--> `CommentLikeRepository`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/service/CommentLikeService.java → src/main/java/com/tocktalks/domain/community/repository/CommentLikeRepository.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Shared Infrastructure Profile** — src_main_resources_application_shared_spring_datasource, src_main_resources_application_shared_spring_redis [EXTRACTED 1.00]
- **TockTalks Core Domains** — domain_member, domain_room, domain_trade, domain_price, domain_ranking, domain_portfolio, domain_community [EXTRACTED 1.00]
- **Infrastructure Services** — mysql_db, redis_db, docker_compose [EXTRACTED 1.00]

## Communities (103 total, 34 thin omitted)

### Community 0 - "TransactionRepository"
Cohesion: 0.05
Nodes (46): Query, Holding, Entity, Getter, NoArgsConstructor, Table, HoldingRepository, Page (+38 more)

### Community 1 - "RoomService"
Cohesion: 0.06
Nodes (37): CreateRoomRequest, RankingDto, RankingService, RoomParticipantResponse, RoomProperties, RoomRankingResponse, RoomResponse, AdminRoomController (+29 more)

### Community 2 - "RankingService"
Cohesion: 0.06
Nodes (37): AfterEach, GetMapping, RequestMapping, RequiredArgsConstructor, RestController, RankingController, RankingArchiveResponse, RankingDto (+29 more)

### Community 3 - "TradeController.java"
Cohesion: 0.07
Nodes (31): MethodArgumentNotValidException, Authentication, GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor (+23 more)

### Community 4 - "StockMasterService"
Cohesion: 0.06
Nodes (34): Retention, FavoriteStockController, DeleteMapping, GetMapping, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+26 more)

### Community 5 - "KisAuthService"
Cohesion: 0.06
Nodes (31): EnableConfigurationProperties, ConfigurationProperties, KisApiProperties, Bean, Configuration, WebClient, PriceConfig, KisApprovalRequest (+23 more)

### Community 6 - "Transaction"
Cohesion: 0.07
Nodes (26): Primary, Component, Override, StubTransactionCertificateProvider, TransactionCertificateProvider, TransactionSnapshot, Entity, Getter (+18 more)

### Community 7 - "JpaRepository"
Cohesion: 0.06
Nodes (28): AllArgsConstructor, JpaRepository, DailyStatsController, GetMapping, RequestMapping, RequiredArgsConstructor, RestController, DailyStatsResponse (+20 more)

### Community 8 - "ReportService"
Cohesion: 0.07
Nodes (33): DeleteMapping, PatchMapping, ReportRepository, AdminReportController, GetMapping, Page, Pageable, RequestMapping (+25 more)

### Community 9 - "PortfolioService.java"
Cohesion: 0.08
Nodes (29): AssetHistoryRepository, AssetHistoryResponse, HoldingQueryService, HoldingSummaryResponse, PortfolioDetailResponse, PortfolioSummaryResponse, ResponseEntity, AssetHistoryResponse (+21 more)

### Community 10 - "HoldingQueryService"
Cohesion: 0.09
Nodes (22): CurrentPriceProvider, Holding, HoldingRepository, HoldingQueryService, HoldingResponse, HoldingSummaryResponse, RequiredArgsConstructor, Service (+14 more)

### Community 11 - "JwtProvider"
Cohesion: 0.09
Nodes (23): Claims, FilterChain, HttpServletRequest, HttpServletResponse, OncePerRequestFilter, SecretKey, RedisTemplate, RequiredArgsConstructor (+15 more)

### Community 12 - "TradeOrderRequest"
Cohesion: 0.17
Nodes (7): TradeOrderRequest, TradeExecutionResponse, Test, Test, Test, Test, Transactional

### Community 13 - "NoticeController.java"
Cohesion: 0.10
Nodes (25): GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+17 more)

### Community 14 - "AuthController"
Cohesion: 0.12
Nodes (16): EmailCheckResponse, KakaoLoginRequest, PostMapping, ReissueRequest, AuthController, Authentication, GetMapping, KakaoOAuthClient (+8 more)

### Community 15 - "RankingSubscriber"
Cohesion: 0.14
Nodes (19): MessageListener, RedisMessageListenerContainer, Bean, Configuration, RedisConnectionFactory, PriceRedisConfig, Message, Override (+11 more)

### Community 16 - "AdminMemberController.java"
Cohesion: 0.16
Nodes (16): AdminMemberController, GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+8 more)

### Community 17 - "AuthService"
Cohesion: 0.17
Nodes (14): JwtProvider, KakaoUserInfoResponse, LoginAttemptService, PasswordEncoder, RefreshTokenService, AuthService, KakaoOAuthClient, LoginRequest (+6 more)

### Community 18 - "CommentService"
Cohesion: 0.18
Nodes (12): CommentCreateRequest, CommentLikeRepository, CommentRepository, CommentUpdateRequest, CommentResponse, CommentService, Page, Pageable (+4 more)

### Community 19 - "Room"
Cohesion: 0.17
Nodes (8): Entity, Getter, NoArgsConstructor, Table, Room, TradeAvailabilityValidator, Test, TradeAvailabilityValidatorTest

### Community 20 - "PostController"
Cohesion: 0.15
Nodes (14): DeleteMapping, GetMapping, Page, Pageable, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+6 more)

### Community 21 - "Comment"
Cohesion: 0.14
Nodes (12): Comment, Entity, Getter, NoArgsConstructor, Table, CommentRepository, Page, Pageable (+4 more)

### Community 22 - "KakaoOAuthClient"
Cohesion: 0.16
Nodes (15): RestClient, Component, RequiredArgsConstructor, KakaoOAuthClient, JsonIgnoreProperties, KakaoTokenResponse, JsonIgnoreProperties, KakaoAccount (+7 more)

### Community 23 - "PostLike"
Cohesion: 0.14
Nodes (10): Entity, Getter, NoArgsConstructor, Table, PostLike, PostLikeRepository, RequiredArgsConstructor, Service (+2 more)

### Community 24 - "PostService"
Cohesion: 0.20
Nodes (12): PostCreateRequest, PostLikeRepository, PostResponse, PostUpdateRequest, Page, Pageable, Post, RequiredArgsConstructor (+4 more)

### Community 25 - "CommentController.java"
Cohesion: 0.15
Nodes (13): CommentController, DeleteMapping, GetMapping, Page, Pageable, PatchMapping, PostMapping, RequestMapping (+5 more)

### Community 26 - "RoomParticipant"
Cohesion: 0.17
Nodes (7): Entity, Getter, NoArgsConstructor, Table, RoomParticipant, Test, RoomParticipantTest

### Community 27 - ".calculate"
Cohesion: 0.19
Nodes (5): TradeAmountCalculator, Test, TradeExecutionResponseTest, Test, TradeAmountCalculatorTest

### Community 28 - "Member"
Cohesion: 0.15
Nodes (8): Entity, Getter, NoArgsConstructor, Table, Member, Page, Pageable, MemberRepository

### Community 29 - "KisWebSocketClient"
Cohesion: 0.22
Nodes (8): CloseStatus, Override, PricePublisher, Service, KisWebSocketClient, TextMessage, TextWebSocketHandler, WebSocketSession

### Community 30 - "PriceSubscriptionTracker"
Cohesion: 0.18
Nodes (8): Component, EventListener, Pattern, SessionDisconnectEvent, SessionSubscribeEvent, SessionUnsubscribeEvent, PriceSubscriptionTracker, StockCodeValidator

### Community 31 - "LoginMemberIdArgumentResolver.java"
Cohesion: 0.20
Nodes (12): HandlerMethodArgumentResolver, MethodParameter, ModelAndViewContainer, NativeWebRequest, Configuration, HandlerMethodArgumentResolver, Override, WebMvcConfig (+4 more)

### Community 32 - "RoomParticipantRepository"
Cohesion: 0.18
Nodes (9): Lock, RoomParticipant, RoomParticipantRepository, DifferentParticipantConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test (+1 more)

### Community 33 - "RoomRepository"
Cohesion: 0.11
Nodes (20): Room, RoomRepository, BuyTradeService, RequiredArgsConstructor, Service, Transactional, StockNameProvider, ExtendWith (+12 more)

### Community 34 - "BuySellConcurrencyTest.java"
Cohesion: 0.43
Nodes (6): BuySellConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 35 - "LoginAttemptService"
Cohesion: 0.24
Nodes (9): RedisTemplate, RequiredArgsConstructor, Service, LoginAttemptService, Component, ConfigurationProperties, Getter, Setter (+1 more)

### Community 36 - "Transaction.java"
Cohesion: 0.19
Nodes (8): TradeType, BUY, SELL, BuyTradeServiceTest, ExtendWith, ExtendWith, SellTradeServiceTest, ExtendWith

### Community 38 - "Post"
Cohesion: 0.16
Nodes (5): Entity, Getter, NoArgsConstructor, Table, Post

### Community 39 - "CommentLike"
Cohesion: 0.21
Nodes (6): CommentLike, Entity, Getter, NoArgsConstructor, Table, CommentLikeRepository

### Community 40 - "PriceController"
Cohesion: 0.31
Nodes (8): KisPriceResponse, KisPriceService, KisWebSocketClient, GetMapping, RestController, PriceController, StockInfo, StockMasterService

### Community 41 - "MySqlTestContainerConfiguration.java"
Cohesion: 0.26
Nodes (10): MySQLContainer, ServiceConnection, AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, RoomParticipantRepositoryTest, Bean (+2 more)

### Community 42 - "DailyPriceResponse"
Cohesion: 0.22
Nodes (5): DailyPriceResponse, JsonIgnoreProperties, KisDailyChartEnvelope, JsonIgnoreProperties, KisDailyPriceItem

### Community 43 - "CommunityErrorCode"
Cohesion: 0.23
Nodes (9): CommunityErrorCode, COMMENT_ACCESS_DENIED, COMMENT_NOT_FOUND, POST_ACCESS_DENIED, POST_NOT_FOUND, Getter, RequiredArgsConstructor, CommunityException (+1 more)

### Community 44 - "TradeSecurityIntegrationTest"
Cohesion: 0.29
Nodes (7): BeforeEach, MockMvc, SpringBootTest, Test, TradeSecurityIntegrationTest, WebAppConfiguration, WebApplicationContext

### Community 45 - "SecurityConfig.java"
Cohesion: 0.31
Nodes (8): Bean, Configuration, EnableWebSecurity, HttpSecurity, JwtAuthenticationFilter, SecurityFilterChain, RequiredArgsConstructor, SecurityConfig

### Community 46 - "README.md"
Cohesion: 0.18
Nodes (9): Admin Domain, Backoffice Domain, Community Domain, Member Domain, Portfolio Domain, Price Domain, Ranking Domain, Room Domain (+1 more)

### Community 47 - "WebSocketConfig.java"
Cohesion: 0.33
Nodes (7): EnableWebSocketMessageBroker, MessageBrokerRegistry, Configuration, Override, WebSocketConfig, StompEndpointRegistry, WebSocketMessageBrokerConfigurer

### Community 48 - "KisChartService"
Cohesion: 0.49
Nodes (7): KisApiProperties, KisAuthService, ObjectMapper, Service, KisChartService, StringRedisTemplate, WebClient

### Community 49 - "TradeRankingService"
Cohesion: 0.36
Nodes (7): RankingService, RequiredArgsConstructor, RoomParticipant, Service, Slf4j, TradeAssetService, TradeRankingService

### Community 50 - "TradeOrderRequestTest"
Cohesion: 0.39
Nodes (3): Test, TradeOrderRequestTest, Validator

### Community 51 - "CommunityExceptionHandler.java"
Cohesion: 0.43
Nodes (6): CommunityExceptionHandler, ErrorResponse, ExceptionHandler, ResponseEntity, RestControllerAdvice, Slf4j

### Community 52 - "PostRepository"
Cohesion: 0.54
Nodes (4): Page, Pageable, Post, PostRepository

### Community 53 - "RedisConfig.java"
Cohesion: 0.50
Nodes (5): Bean, Configuration, RedisConnectionFactory, RedisTemplate, RedisConfig

### Community 54 - "SellTradeConcurrencyTest.java"
Cohesion: 0.43
Nodes (6): AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional, SellTradeConcurrencyTest

### Community 55 - "SellTradeService"
Cohesion: 0.19
Nodes (11): CurrentPriceProvider, RequiredArgsConstructor, Service, Transactional, SellTradeService, AutoConfigureTestDatabase, DataJpaTest, Import (+3 more)

### Community 56 - "KisRealtimeSubscribeRequest.java"
Cohesion: 0.71
Nodes (4): Body, Header, Input, KisRealtimeSubscribeRequest

### Community 57 - ".parsesRealtimeData"
Cohesion: 0.33
Nodes (3): KisRealtimePriceMessage, Test, KisRealtimePriceMessageTest

### Community 58 - "BuyTradeConcurrencyTest.java"
Cohesion: 0.43
Nodes (6): BuyTradeConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 59 - "CorsConfig.java"
Cohesion: 0.53
Nodes (4): CorsConfigurationSource, CorsConfig, Bean, Configuration

### Community 60 - "application.yml"
Cohesion: 0.40
Nodes (4): Kakao OAuth API, External Stock API, MySQL Database, Redis Cache

### Community 61 - "Spring Shared Datasource Configuration"
Cohesion: 0.33
Nodes (6): SHARED_DB_HOST, SHARED_DB_NAME, SHARED_DB_PASSWORD, SHARED_DB_PORT, SHARED_DB_USERNAME, Spring Shared Datasource Configuration

### Community 62 - "PricePublisher"
Cohesion: 0.53
Nodes (3): Service, StringRedisTemplate, PricePublisher

### Community 63 - "RoomProperties"
Cohesion: 0.60
Nodes (5): Component, ConfigurationProperties, Getter, Setter, RoomProperties

### Community 64 - "TockTalksApplication"
Cohesion: 0.60
Nodes (3): EnableScheduling, SpringBootApplication, TockTalksApplication

### Community 65 - "HealthController.java"
Cohesion: 0.60
Nodes (3): HealthController, GetMapping, RestController

### Community 66 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **31 isolated node(s):** `EmailCheckResponse`, `KakaoLoginRequest`, `LoginRequest`, `ReissueRequest`, `SignupRequest` (+26 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **34 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `RoomRepository` connect `RoomRepository` to `RoomParticipantRepository`, `RoomService`, `BuySellConcurrencyTest.java`, `Transaction.java`, `JpaRepository`, `PortfolioService.java`, `SellTradeConcurrencyTest.java`, `SellTradeService`, `BuyTradeConcurrencyTest.java`?**
  _High betweenness centrality (0.205) - this node is a cross-community bridge._
- **Why does `RoomService` connect `RoomService` to `AuthService`, `Member`, `RoomRepository`?**
  _High betweenness centrality (0.157) - this node is a cross-community bridge._
- **Why does `PostRepository` connect `PostRepository` to `PostService`, `CommentService`, `PostLike`, `JpaRepository`?**
  _High betweenness centrality (0.110) - this node is a cross-community bridge._
- **What connects `EmailCheckResponse`, `KakaoLoginRequest`, `LoginRequest` to the rest of the system?**
  _31 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `TransactionRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.054392236976506636 - nodes in this community are weakly interconnected._
- **Should `RoomService` be split into smaller, more focused modules?**
  _Cohesion score 0.060350877192982454 - nodes in this community are weakly interconnected._
- **Should `RankingService` be split into smaller, more focused modules?**
  _Cohesion score 0.058738738738738736 - nodes in this community are weakly interconnected._