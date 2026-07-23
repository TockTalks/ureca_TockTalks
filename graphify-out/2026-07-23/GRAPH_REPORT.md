# Graph Report - C:\Users\howar\Downloads\ureca_TockTalks  (2026-07-23)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1707 nodes · 3704 edges · 137 communities (89 shown, 48 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 342 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a4fc9cf3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- RoomService
- TradeController.java
- RankingService
- Report
- PortfolioService.java
- KisWebSocketClient
- JwtProvider
- DailyStatsService.java
- TradeOrderRequest
- KisAuthService
- HoldingQueryService
- NoticeController.java
- Notice
- RankingSubscriber
- CommentService
- AuthService
- PostController
- AuthController
- PostService
- CommentController.java
- .create
- RoomParticipant.java
- KakaoOAuthClient
- Room
- Comment
- PostLike
- .calculate
- Transaction
- TradeAssetService
- HoldingRepository
- TransactionRepository
- AdminMemberController.java
- RoomParticipant
- LoginMemberIdArgumentResolver.java
- CommentLike
- TradeTransactionCertificateProvider
- MemberRepository
- LoginAttemptService
- PriceController
- MySqlTestContainerConfiguration.java
- KisCurrentPriceProviderTest
- BuyTradeProcessorIntegrationTest.java
- TradeRankingService
- JpaRepository
- TradeSecurityIntegrationTest
- CommunityErrorCode
- FavoriteStock
- RoomRankingArchive
- SellTradeService
- KisRateLimiter
- RoomParticipantRepository
- Post
- FavoriteStockController
- StockQuoteResponse
- StockMasterService
- KisChartService
- RoomRepository
- .createBuy
- README.md
- AdminMemberService
- KisCurrentPriceProvider
- KisPriceService
- Holding
- WebSocketConfig.java
- .updateMember
- KisDailyChartEnvelope
- .getStockName
- RoomParticipantRepositoryTest
- TradeOrderRequestTest
- HoldingRepositoryTest
- CommunityExceptionHandler.java
- HoldingPriceWarmupScheduler
- RedisConfig.java
- BuyTradeConcurrencyTest.java
- BuyTradeRollbackIntegrationTest.java
- DifferentParticipantConcurrencyTest.java
- Transaction.java
- TockTalks Application
- CorsConfig.java
- Spring Shared Datasource Configuration
- PricePublisher
- BuyTradeService
- RoomProperties
- TockTalksApplication
- HealthController.java
- gradlew
- PortfolioHoldingResponse.java
- docker-compose.yml
- SHARED_REDIS_URL
- EmailCheckResponse.java
- KakaoLoginRequest.java
- LoginRequest.java
- ReissueRequest.java
- SignupRequest.java
- TokenResponse.java
- AssetHistoryRepository
- AssetHistoryResponse
- Entity
- ExceptionHandler
- GetMapping
- Getter
- HoldingQueryService
- HoldingSummaryResponse
- KisPriceResponse
- KisPriceService
- NoArgsConstructor
- PortfolioDetailResponse
- PortfolioSummaryResponse
- RankingDto
- RankingService
- RestController
- RestControllerAdvice
- RoomProperties
- PatchMapping
- PostMapping
- PasswordEncoder
- ResponseEntity
- AssetHistoryResponse
- RoomParticipantRepository
- TradeAssetService
- ObjectMapper
- Override
- StringRedisTemplate
- MemberRepository
- RankingService
- RoomParticipantRepository
- Query
- HoldingResponse
- Bean
- Configuration
- PasswordEncoder
- Table
- TradeRankingService
- WebClient

## God Nodes (most connected - your core abstractions)
1. `RoomParticipantRepository` - 51 edges
2. `TransactionRepository` - 42 edges
3. `HoldingRepository` - 41 edges
4. `RoomService` - 38 edges
5. `TradeOrderRequest` - 35 edges
6. `RoomRepository` - 34 edges
7. `MemberRepository` - 26 edges
8. `RankingService` - 25 edges
9. `AuthService` - 24 edges
10. `Member` - 24 edges

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
- **Security and Authentication Flow** — src_main_resources_application_jwt, src_main_resources_application_kakao_oauth [INFERRED 0.80]
- **Shared Infrastructure Profile** — src_main_resources_application_shared_spring_datasource, src_main_resources_application_shared_spring_redis [EXTRACTED 1.00]
- **TockTalks Core Domains** — domain_member, domain_room, domain_trade, domain_price, domain_ranking, domain_portfolio, domain_community [EXTRACTED 1.00]
- **Infrastructure Services** — mysql_db, redis_db, docker_compose [EXTRACTED 1.00]

## Communities (137 total, 48 thin omitted)

### Community 0 - "RoomService"
Cohesion: 0.06
Nodes (35): CreateRoomRequest, ExtendWith, RoomParticipantResponse, RoomRankingResponse, RoomResponse, Authentication, DeleteMapping, GetMapping (+27 more)

### Community 1 - "TradeController.java"
Cohesion: 0.06
Nodes (35): MethodArgumentNotValidException, Authentication, GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor (+27 more)

### Community 2 - "RankingService"
Cohesion: 0.06
Nodes (39): AfterEach, RankingListResponse, RankingPublisher, RankingRedisService, RankingType, RoomRankingArchiveRepository, GetMapping, RequestMapping (+31 more)

### Community 3 - "Report"
Cohesion: 0.07
Nodes (33): DeleteMapping, ReportCreateRequest, ReportRepository, AdminReportController, GetMapping, Page, Pageable, RequestMapping (+25 more)

### Community 4 - "PortfolioService.java"
Cohesion: 0.07
Nodes (32): AllArgsConstructor, PortfolioHoldingResponse, ResponseEntity, AssetHistoryResponse, Authentication, GetMapping, RequestMapping, RequiredArgsConstructor (+24 more)

### Community 5 - "KisWebSocketClient"
Cohesion: 0.07
Nodes (27): CloseStatus, Component, EventListener, KisApiProperties, KisAuthService, ObjectMapper, Override, Pattern (+19 more)

### Community 6 - "JwtProvider"
Cohesion: 0.07
Nodes (31): Bean, Claims, Configuration, EnableWebSecurity, FilterChain, HttpSecurity, HttpServletRequest, HttpServletResponse (+23 more)

### Community 7 - "DailyStatsService.java"
Cohesion: 0.09
Nodes (22): DailyStatsController, GetMapping, RequestMapping, RequiredArgsConstructor, RestController, DailyStatsResponse, DailyStats, Entity (+14 more)

### Community 8 - "TradeOrderRequest"
Cohesion: 0.19
Nodes (8): TradeOrderRequest, StockNameProvider, BuyTradeServiceTest, Test, Test, SellTradeServiceTest, Test, Transactional

### Community 9 - "KisAuthService"
Cohesion: 0.10
Nodes (17): EnableConfigurationProperties, ConfigurationProperties, KisApiProperties, Bean, Configuration, WebClient, PriceConfig, KisApprovalRequest (+9 more)

### Community 10 - "HoldingQueryService"
Cohesion: 0.14
Nodes (13): HoldingResponse, HoldingQueryService, CurrentPriceProvider, Holding, HoldingSummaryResponse, RequiredArgsConstructor, Service, Slf4j (+5 more)

### Community 11 - "NoticeController.java"
Cohesion: 0.13
Nodes (21): NoticeCreateRequest, NoticeResponse, NoticeService, Retention, AdminRoomController, PostMapping, RequestMapping, RequiredArgsConstructor (+13 more)

### Community 12 - "Notice"
Cohesion: 0.13
Nodes (16): NoticeCreateRequest, NoticeResponse, Entity, Getter, NoArgsConstructor, Table, Notice, Page (+8 more)

### Community 13 - "RankingSubscriber"
Cohesion: 0.14
Nodes (19): MessageListener, RedisMessageListenerContainer, Bean, Configuration, RedisConnectionFactory, PriceRedisConfig, Message, Override (+11 more)

### Community 14 - "CommentService"
Cohesion: 0.18
Nodes (14): Comment, CommentCreateRequest, CommentLikeRepository, CommentRepository, CommentResponse, CommentUpdateRequest, CommentService, Page (+6 more)

### Community 15 - "AuthService"
Cohesion: 0.17
Nodes (14): JwtProvider, KakaoUserInfoResponse, LoginAttemptService, PasswordEncoder, RefreshTokenService, AuthService, KakaoOAuthClient, LoginRequest (+6 more)

### Community 16 - "PostController"
Cohesion: 0.14
Nodes (14): DeleteMapping, GetMapping, Page, Pageable, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+6 more)

### Community 17 - "AuthController"
Cohesion: 0.14
Nodes (14): EmailCheckResponse, KakaoLoginRequest, PostMapping, ReissueRequest, AuthController, GetMapping, KakaoOAuthClient, LoginRequest (+6 more)

### Community 18 - "PostService"
Cohesion: 0.18
Nodes (13): PostCreateRequest, PostLikeRepository, PostResponse, PostUpdateRequest, Page, Pageable, Post, PostRepository (+5 more)

### Community 19 - "CommentController.java"
Cohesion: 0.13
Nodes (14): CommentController, DeleteMapping, GetMapping, Page, Pageable, PatchMapping, PostMapping, RequestMapping (+6 more)

### Community 20 - ".create"
Cohesion: 0.21
Nodes (5): HoldingTest, Test, Test, Test, SellTradeProcessorTest

### Community 21 - "RoomParticipant.java"
Cohesion: 0.15
Nodes (14): BuySellConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional, ExtendWith, AutoConfigureTestDatabase (+6 more)

### Community 22 - "KakaoOAuthClient"
Cohesion: 0.16
Nodes (15): RestClient, Component, RequiredArgsConstructor, KakaoOAuthClient, JsonIgnoreProperties, KakaoTokenResponse, JsonIgnoreProperties, KakaoAccount (+7 more)

### Community 23 - "Room"
Cohesion: 0.19
Nodes (8): Entity, Getter, NoArgsConstructor, Table, Room, TradeAvailabilityValidator, Test, TradeAvailabilityValidatorTest

### Community 24 - "Comment"
Cohesion: 0.14
Nodes (12): Comment, Entity, Getter, NoArgsConstructor, Table, CommentRepository, Page, Pageable (+4 more)

### Community 25 - "PostLike"
Cohesion: 0.15
Nodes (10): Entity, Getter, NoArgsConstructor, Table, PostLike, PostLikeRepository, RequiredArgsConstructor, Service (+2 more)

### Community 26 - ".calculate"
Cohesion: 0.18
Nodes (5): TradeAmountCalculator, Test, TradeExecutionResponseTest, Test, TradeAmountCalculatorTest

### Community 27 - "Transaction"
Cohesion: 0.17
Nodes (7): Entity, Getter, NoArgsConstructor, Table, Transaction, Test, TransactionTest

### Community 28 - "TradeAssetService"
Cohesion: 0.20
Nodes (11): CurrentPriceProvider, Holding, HoldingRepository, RequiredArgsConstructor, RoomParticipant, Service, Transactional, TradeAssetService (+3 more)

### Community 29 - "HoldingRepository"
Cohesion: 0.18
Nodes (13): HoldingRepository, Holding, Query, RequiredArgsConstructor, Service, Transactional, SellTradeProcessor, AutoConfigureTestDatabase (+5 more)

### Community 30 - "TransactionRepository"
Cohesion: 0.23
Nodes (9): Query, Page, Pageable, TransactionRepository, Override, ExtendWith, Test, TradeTransactionCertificateProviderTest (+1 more)

### Community 31 - "AdminMemberController.java"
Cohesion: 0.24
Nodes (10): AdminMemberController, GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+2 more)

### Community 32 - "RoomParticipant"
Cohesion: 0.20
Nodes (7): Entity, Getter, NoArgsConstructor, Table, RoomParticipant, Test, RoomParticipantTest

### Community 33 - "LoginMemberIdArgumentResolver.java"
Cohesion: 0.20
Nodes (12): HandlerMethodArgumentResolver, MethodParameter, ModelAndViewContainer, NativeWebRequest, Configuration, HandlerMethodArgumentResolver, Override, WebMvcConfig (+4 more)

### Community 34 - "CommentLike"
Cohesion: 0.17
Nodes (6): CommentLike, Entity, Getter, NoArgsConstructor, Table, CommentLikeRepository

### Community 35 - "TradeTransactionCertificateProvider"
Cohesion: 0.19
Nodes (10): Primary, Component, Override, StubTransactionCertificateProvider, TransactionCertificateProvider, TransactionSnapshot, RequiredArgsConstructor, Service (+2 more)

### Community 36 - "MemberRepository"
Cohesion: 0.20
Nodes (8): Entity, Getter, NoArgsConstructor, Table, Member, Page, Pageable, MemberRepository

### Community 37 - "LoginAttemptService"
Cohesion: 0.24
Nodes (9): RedisTemplate, RequiredArgsConstructor, Service, LoginAttemptService, Component, ConfigurationProperties, Getter, Setter (+1 more)

### Community 38 - "PriceController"
Cohesion: 0.27
Nodes (8): KisWebSocketClient, DailyPriceResponse, GetMapping, KisPriceResponse, RestController, PriceController, StockInfo, StockMasterService

### Community 39 - "MySqlTestContainerConfiguration.java"
Cohesion: 0.22
Nodes (11): MySQLContainer, ServiceConnection, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional, SellTradeRollbackIntegrationTest (+3 more)

### Community 40 - "KisCurrentPriceProviderTest"
Cohesion: 0.36
Nodes (4): Override, ExtendWith, Test, KisCurrentPriceProviderTest

### Community 41 - "BuyTradeProcessorIntegrationTest.java"
Cohesion: 0.24
Nodes (10): BuyTradeProcessor, RequiredArgsConstructor, Service, Transactional, BuyTradeProcessorIntegrationTest, AutoConfigureTestDatabase, DataJpaTest, EntityManager (+2 more)

### Community 42 - "TradeRankingService"
Cohesion: 0.23
Nodes (10): RankingService, RequiredArgsConstructor, RoomParticipant, Service, Slf4j, TradeAssetService, TradeRankingService, ExtendWith (+2 more)

### Community 43 - "JpaRepository"
Cohesion: 0.26
Nodes (6): JpaRepository, FavoriteStockRepository, FavoriteStockService, RequiredArgsConstructor, Service, Transactional

### Community 44 - "TradeSecurityIntegrationTest"
Cohesion: 0.26
Nodes (7): BeforeEach, MockMvc, SpringBootTest, Test, TradeSecurityIntegrationTest, WebAppConfiguration, WebApplicationContext

### Community 45 - "CommunityErrorCode"
Cohesion: 0.21
Nodes (10): CommunityErrorCode, COMMENT_ACCESS_DENIED, COMMENT_NOT_FOUND, MEMBER_BLOCKED, POST_ACCESS_DENIED, POST_NOT_FOUND, Getter, RequiredArgsConstructor (+2 more)

### Community 46 - "FavoriteStock"
Cohesion: 0.19
Nodes (7): GetMapping, FavoriteStockResponse, FavoriteStock, Entity, Getter, NoArgsConstructor, Table

### Community 47 - "RoomRankingArchive"
Cohesion: 0.21
Nodes (6): Entity, Getter, NoArgsConstructor, Table, RoomRankingArchive, RoomRankingArchiveRepository

### Community 48 - "SellTradeService"
Cohesion: 0.23
Nodes (9): CurrentPriceProvider, RequiredArgsConstructor, Service, Transactional, SellTradeService, AutoConfigureTestDatabase, DataJpaTest, Import (+1 more)

### Community 49 - "KisRateLimiter"
Cohesion: 0.29
Nodes (6): DefaultRedisScript, PostConstruct, Component, Log4j2, StringRedisTemplate, KisRateLimiter

### Community 50 - "RoomParticipantRepository"
Cohesion: 0.26
Nodes (4): Lock, Query, RoomParticipant, RoomParticipantRepository

### Community 51 - "Post"
Cohesion: 0.20
Nodes (5): Entity, Getter, NoArgsConstructor, Table, Post

### Community 52 - "FavoriteStockController"
Cohesion: 0.23
Nodes (8): FavoriteStockController, DeleteMapping, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, FavoriteStockRequest

### Community 53 - "StockQuoteResponse"
Cohesion: 0.23
Nodes (5): JsonIgnoreProperties, KisMultiPriceEnvelope, JsonIgnoreProperties, KisMultiPriceItem, StockQuoteResponse

### Community 54 - "StockMasterService"
Cohesion: 0.29
Nodes (7): StockInfo, PostConstruct, Service, StockMasterService, Component, RequiredArgsConstructor, StockMasterStockNameProvider

### Community 55 - "KisChartService"
Cohesion: 0.36
Nodes (8): DailyPriceResponse, KisApiProperties, KisAuthService, ObjectMapper, Service, StringRedisTemplate, WebClient, KisChartService

### Community 56 - "RoomRepository"
Cohesion: 0.30
Nodes (5): Room, RoomRepository, ExtendWith, Test, RoomServiceRankingTest

### Community 57 - ".createBuy"
Cohesion: 0.32
Nodes (6): AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, Test, TransactionRepositoryTest

### Community 58 - "README.md"
Cohesion: 0.18
Nodes (9): Admin Domain, Backoffice Domain, Community Domain, Member Domain, Portfolio Domain, Price Domain, Ranking Domain, Room Domain (+1 more)

### Community 59 - "AdminMemberService"
Cohesion: 0.33
Nodes (6): AdminMemberService, Page, Pageable, RequiredArgsConstructor, Service, Transactional

### Community 60 - "KisCurrentPriceProvider"
Cohesion: 0.25
Nodes (7): JsonIgnoreProperties, KisPriceEnvelope, JsonIgnoreProperties, KisPriceResponse, Component, RequiredArgsConstructor, KisCurrentPriceProvider

### Community 61 - "KisPriceService"
Cohesion: 0.38
Nodes (8): KisApiProperties, KisAuthService, KisPriceResponse, ObjectMapper, Service, StringRedisTemplate, WebClient, KisPriceService

### Community 62 - "Holding"
Cohesion: 0.29
Nodes (5): Holding, Entity, Getter, NoArgsConstructor, Table

### Community 63 - "WebSocketConfig.java"
Cohesion: 0.33
Nodes (7): EnableWebSocketMessageBroker, MessageBrokerRegistry, Configuration, Override, WebSocketConfig, StompEndpointRegistry, WebSocketMessageBrokerConfigurer

### Community 64 - ".updateMember"
Cohesion: 0.24
Nodes (3): PatchMapping, Authentication, MemberUpdateRequest

### Community 65 - "KisDailyChartEnvelope"
Cohesion: 0.24
Nodes (5): DailyPriceResponse, JsonIgnoreProperties, KisDailyChartEnvelope, JsonIgnoreProperties, KisDailyPriceItem

### Community 66 - ".getStockName"
Cohesion: 0.38
Nodes (4): Override, ExtendWith, Test, StockMasterStockNameProviderTest

### Community 67 - "RoomParticipantRepositoryTest"
Cohesion: 0.36
Nodes (6): AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, Test, RoomParticipantRepositoryTest

### Community 68 - "TradeOrderRequestTest"
Cohesion: 0.39
Nodes (3): Test, TradeOrderRequestTest, Validator

### Community 69 - "HoldingRepositoryTest"
Cohesion: 0.39
Nodes (6): HoldingRepositoryTest, AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, Test

### Community 70 - "CommunityExceptionHandler.java"
Cohesion: 0.43
Nodes (6): CommunityExceptionHandler, ErrorResponse, ExceptionHandler, ResponseEntity, RestControllerAdvice, Slf4j

### Community 71 - "HoldingPriceWarmupScheduler"
Cohesion: 0.39
Nodes (6): HoldingPriceWarmupScheduler, Component, CurrentPriceProvider, RequiredArgsConstructor, Scheduled, Slf4j

### Community 72 - "RedisConfig.java"
Cohesion: 0.50
Nodes (5): Bean, Configuration, RedisConnectionFactory, RedisTemplate, RedisConfig

### Community 73 - "BuyTradeConcurrencyTest.java"
Cohesion: 0.43
Nodes (6): BuyTradeConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 74 - "BuyTradeRollbackIntegrationTest.java"
Cohesion: 0.43
Nodes (6): BuyTradeRollbackIntegrationTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 75 - "DifferentParticipantConcurrencyTest.java"
Cohesion: 0.43
Nodes (6): DifferentParticipantConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 76 - "Transaction.java"
Cohesion: 0.43
Nodes (3): BuyTradeProcessorTest, ExtendWith, Test

### Community 77 - "TockTalks Application"
Cohesion: 0.29
Nodes (7): MySQL Datasource, JWT Configuration, Kakao OAuth, Redis Cache, Room Configuration, External Stock API, TockTalks Application

### Community 78 - "CorsConfig.java"
Cohesion: 0.53
Nodes (4): CorsConfigurationSource, CorsConfig, Bean, Configuration

### Community 79 - "Spring Shared Datasource Configuration"
Cohesion: 0.33
Nodes (6): SHARED_DB_HOST, SHARED_DB_NAME, SHARED_DB_PASSWORD, SHARED_DB_PORT, SHARED_DB_USERNAME, Spring Shared Datasource Configuration

### Community 80 - "PricePublisher"
Cohesion: 0.53
Nodes (3): Service, StringRedisTemplate, PricePublisher

### Community 81 - "BuyTradeService"
Cohesion: 0.47
Nodes (4): BuyTradeService, RequiredArgsConstructor, Service, Transactional

### Community 82 - "RoomProperties"
Cohesion: 0.60
Nodes (5): Component, ConfigurationProperties, Getter, Setter, RoomProperties

### Community 83 - "TockTalksApplication"
Cohesion: 0.60
Nodes (3): EnableScheduling, SpringBootApplication, TockTalksApplication

### Community 84 - "HealthController.java"
Cohesion: 0.60
Nodes (3): HealthController, GetMapping, RestController

### Community 85 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **38 isolated node(s):** `EmailCheckResponse`, `KakaoLoginRequest`, `LoginRequest`, `ReissueRequest`, `SignupRequest` (+33 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **48 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MemberRepository` connect `MemberRepository` to `RoomService`, `RankingService`, `Report`, `DailyStatsService.java`, `JpaRepository`, `CommentService`, `PostService`, `RoomRepository`, `AdminMemberService`?**
  _High betweenness centrality (0.227) - this node is a cross-community bridge._
- **Why does `RoomParticipantRepository` connect `RoomParticipantRepository` to `RoomService`, `TradeController.java`, `RoomParticipantRepositoryTest`, `PortfolioService.java`, `MySqlTestContainerConfiguration.java`, `TradeOrderRequest`, `BuyTradeConcurrencyTest.java`, `HoldingQueryService`, `JpaRepository`, `BuyTradeRollbackIntegrationTest.java`, `DifferentParticipantConcurrencyTest.java`, `SellTradeService`, `BuyTradeService`, `RoomParticipant.java`, `RoomRepository`?**
  _High betweenness centrality (0.180) - this node is a cross-community bridge._
- **Why does `RoomService` connect `RoomService` to `RankingService`, `MemberRepository`, `NoticeController.java`, `AuthService`, `RoomParticipantRepository`, `RoomRepository`?**
  _High betweenness centrality (0.167) - this node is a cross-community bridge._
- **Are the 25 inferred relationships involving `TradeOrderRequest` (e.g. with `.로그인_회원이_종목을_매도한다()` and `.로그인_회원이_종목을_매수한다()`) actually correct?**
  _`TradeOrderRequest` has 25 INFERRED edges - model-reasoned connections that need verification._
- **What connects `EmailCheckResponse`, `KakaoLoginRequest`, `LoginRequest` to the rest of the system?**
  _38 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `RoomService` be split into smaller, more focused modules?**
  _Cohesion score 0.05886075949367089 - nodes in this community are weakly interconnected._
- **Should `TradeController.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06398390342052314 - nodes in this community are weakly interconnected._