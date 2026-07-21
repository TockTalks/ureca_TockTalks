# Graph Report - C:\Users\howar\Downloads\ureca_TockTalks  (2026-07-21)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1357 nodes · 3244 edges · 85 communities (71 shown, 14 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 359 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `01adeabf`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- RoomParticipantRepository
- JpaRepository
- RankingService
- TradeController.java
- FavoriteStock
- NoticeController.java
- PortfolioService.java
- AdminReportController.java
- AuthService
- TradeOrderRequest
- AuthController
- RankingSubscriber
- KisWebSocketClient
- PostController
- KisCurrentPriceProviderTest
- RoomRepository
- .findByRoomParticipantIdAndStockCode
- KisAuthService
- .create
- PostLike
- JwtProvider
- TransactionRepository
- RoomParticipant.java
- Transaction
- TradeHistoryService
- LoginMemberIdArgumentResolver.java
- RefreshTokenService
- Holding.java
- BuyTradeService
- LoginAttemptService
- Post
- KakaoOAuthClient
- StockMasterService
- .calculate
- .validate
- MySqlTestContainerConfiguration.java
- StockMasterStockNameProvider
- SellTradeService
- HoldingRepository
- CommunityException
- Test
- TradeSecurityIntegrationTest
- README.md
- SecurityConfig.java
- .doFilterInternal
- PostService
- .createBuy
- WebSocketConfig.java
- GlobalExceptionHandler
- TradeRankingIntegrationTest.java
- HoldingRepositoryTest
- TransactionRepositoryTest
- TransactionCertificateProvider
- TradeOrderRequestTest
- CommunityExceptionHandler.java
- RedisConfig.java
- BuyTradeProcessorIntegrationTest.java
- SellTradeConcurrencyTest.java
- SellTradeRollbackIntegrationTest.java
- PriceConfig.java
- .from
- TradeType.java
- BuySellConcurrencyTest.java
- CorsConfig.java
- application.yml
- DailyStats
- .createPost
- KisRealtimeSubscribeRequest.java
- TockTalksApplication
- HealthController.java
- BuyTradeRollbackIntegrationTest.java
- gradlew
- KisApprovalResponse.java
- KisTokenResponse.java
- Bean
- Configuration
- ExceptionHandler
- GetMapping
- PasswordEncoder
- ResponseEntity
- RestController
- RestControllerAdvice

## God Nodes (most connected - your core abstractions)
1. `RoomParticipantRepository` - 45 edges
2. `HoldingRepository` - 40 edges
3. `TransactionRepository` - 38 edges
4. `TradeOrderRequest` - 35 edges
5. `RoomRepository` - 33 edges
6. `RoomService` - 33 edges
7. `Room` - 27 edges
8. `Post` - 26 edges
9. `Transaction` - 26 edges
10. `RoomParticipant` - 25 edges

## Surprising Connections (you probably didn't know these)
- `AuthController` --references--> `KakaoOAuthClient`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/auth/controller/AuthController.java → src/main/java/com/tocktalks/domain/auth/client/KakaoOAuthClient.java
- `AuthService` --references--> `KakaoOAuthClient`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/auth/service/AuthService.java → src/main/java/com/tocktalks/domain/auth/client/KakaoOAuthClient.java
- `AuthController` --references--> `AuthService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/auth/controller/AuthController.java → src/main/java/com/tocktalks/domain/auth/service/AuthService.java
- `AuthService` --references--> `LoginAttemptService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/auth/service/AuthService.java → src/main/java/com/tocktalks/domain/auth/service/LoginAttemptService.java
- `AuthService` --references--> `RefreshTokenService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/auth/service/AuthService.java → src/main/java/com/tocktalks/domain/auth/service/RefreshTokenService.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **TockTalks Core Domains** — domain_member, domain_room, domain_trade, domain_price, domain_ranking, domain_portfolio, domain_community [EXTRACTED 1.00]
- **Infrastructure Services** — mysql_db, redis_db, docker_compose [EXTRACTED 1.00]

## Communities (85 total, 14 thin omitted)

### Community 0 - "RoomParticipantRepository"
Cohesion: 0.06
Nodes (35): Lock, Authentication, DeleteMapping, GetMapping, PostMapping, RequestMapping, RequiredArgsConstructor, RestController (+27 more)

### Community 1 - "JpaRepository"
Cohesion: 0.05
Nodes (39): JpaRepository, CommentController, DeleteMapping, GetMapping, Page, Pageable, PatchMapping, PostMapping (+31 more)

### Community 2 - "RankingService"
Cohesion: 0.06
Nodes (38): AfterEach, GetMapping, RequestMapping, RequiredArgsConstructor, RestController, RankingController, RankingArchiveResponse, RankingDto (+30 more)

### Community 3 - "TradeController.java"
Cohesion: 0.08
Nodes (24): Authentication, GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+16 more)

### Community 4 - "FavoriteStock"
Cohesion: 0.08
Nodes (23): Retention, FavoriteStockController, DeleteMapping, GetMapping, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+15 more)

### Community 5 - "NoticeController.java"
Cohesion: 0.10
Nodes (25): GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+17 more)

### Community 6 - "PortfolioService.java"
Cohesion: 0.10
Nodes (22): AllArgsConstructor, Authentication, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, PortfolioController (+14 more)

### Community 7 - "AdminReportController.java"
Cohesion: 0.10
Nodes (24): AdminReportController, GetMapping, Page, Pageable, PatchMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+16 more)

### Community 8 - "AuthService"
Cohesion: 0.13
Nodes (15): JsonIgnoreProperties, KakaoAccount, KakaoUserInfoResponse, Profile, AuthService, PasswordEncoder, RequiredArgsConstructor, Service (+7 more)

### Community 9 - "TradeOrderRequest"
Cohesion: 0.23
Nodes (6): TradeOrderRequest, Test, BuyTradeServiceTest, Test, Test, SellTradeServiceTest

### Community 10 - "AuthController"
Cohesion: 0.11
Nodes (14): AuthController, Authentication, GetMapping, PostMapping, RequestMapping, RequiredArgsConstructor, RestController, EmailCheckResponse (+6 more)

### Community 11 - "RankingSubscriber"
Cohesion: 0.14
Nodes (19): MessageListener, RedisMessageListenerContainer, Bean, Configuration, RedisConnectionFactory, PriceRedisConfig, Message, Override (+11 more)

### Community 12 - "KisWebSocketClient"
Cohesion: 0.16
Nodes (12): CloseStatus, ObjectMapper, Override, Service, StringRedisTemplate, KisWebSocketClient, Service, StringRedisTemplate (+4 more)

### Community 13 - "PostController"
Cohesion: 0.15
Nodes (13): DeleteMapping, GetMapping, Page, Pageable, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+5 more)

### Community 14 - "KisCurrentPriceProviderTest"
Cohesion: 0.18
Nodes (11): JsonIgnoreProperties, KisPriceEnvelope, JsonIgnoreProperties, KisPriceResponse, Component, Override, RequiredArgsConstructor, KisCurrentPriceProvider (+3 more)

### Community 15 - "RoomRepository"
Cohesion: 0.19
Nodes (6): Entity, Getter, NoArgsConstructor, Table, Room, RoomRepository

### Community 16 - ".findByRoomParticipantIdAndStockCode"
Cohesion: 0.16
Nodes (13): RequiredArgsConstructor, Service, Transactional, SellTradeProcessor, AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import (+5 more)

### Community 17 - "KisAuthService"
Cohesion: 0.15
Nodes (11): ConfigurationProperties, KisApiProperties, KisApprovalRequest, KisTokenRequest, Service, StringRedisTemplate, WebClient, KisAuthService (+3 more)

### Community 18 - ".create"
Cohesion: 0.19
Nodes (7): Holding, Entity, Getter, NoArgsConstructor, Table, HoldingTest, Test

### Community 19 - "PostLike"
Cohesion: 0.16
Nodes (10): Entity, Getter, NoArgsConstructor, Table, PostLike, PostLikeRepository, RequiredArgsConstructor, Service (+2 more)

### Community 20 - "JwtProvider"
Cohesion: 0.21
Nodes (6): Claims, SecretKey, Component, PostConstruct, RequiredArgsConstructor, JwtProvider

### Community 21 - "TransactionRepository"
Cohesion: 0.24
Nodes (10): Primary, TransactionRepository, Override, RequiredArgsConstructor, Service, Transactional, TradeTransactionCertificateProvider, ExtendWith (+2 more)

### Community 22 - "RoomParticipant.java"
Cohesion: 0.17
Nodes (12): RequiredArgsConstructor, Service, TradeRankingService, BuyTradeConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test (+4 more)

### Community 23 - "Transaction"
Cohesion: 0.19
Nodes (9): TradeHistoryResponse, TradeType, BUY, SELL, Entity, Getter, NoArgsConstructor, Table (+1 more)

### Community 24 - "TradeHistoryService"
Cohesion: 0.22
Nodes (9): Page, Pageable, RequiredArgsConstructor, Service, Transactional, TradeHistoryService, ExtendWith, Test (+1 more)

### Community 25 - "LoginMemberIdArgumentResolver.java"
Cohesion: 0.20
Nodes (12): HandlerMethodArgumentResolver, MethodParameter, ModelAndViewContainer, NativeWebRequest, Configuration, HandlerMethodArgumentResolver, Override, WebMvcConfig (+4 more)

### Community 26 - "RefreshTokenService"
Cohesion: 0.20
Nodes (9): RedisTemplate, RequiredArgsConstructor, Service, RefreshTokenService, Component, ConfigurationProperties, Getter, Setter (+1 more)

### Community 27 - "Holding.java"
Cohesion: 0.24
Nodes (7): RequiredArgsConstructor, Service, Transactional, TradeAssetService, ExtendWith, Test, TradeAssetServiceTest

### Community 28 - "BuyTradeService"
Cohesion: 0.17
Nodes (12): BuyTradeService, RequiredArgsConstructor, Service, Transactional, CurrentPriceProvider, StockNameProvider, DifferentParticipantConcurrencyTest, AutoConfigureTestDatabase (+4 more)

### Community 29 - "LoginAttemptService"
Cohesion: 0.23
Nodes (9): RedisTemplate, RequiredArgsConstructor, Service, LoginAttemptService, Component, ConfigurationProperties, Getter, Setter (+1 more)

### Community 30 - "Post"
Cohesion: 0.19
Nodes (8): Entity, Getter, NoArgsConstructor, Table, Post, Page, Pageable, PostRepository

### Community 31 - "KakaoOAuthClient"
Cohesion: 0.23
Nodes (11): RestClient, Component, RequiredArgsConstructor, KakaoOAuthClient, JsonIgnoreProperties, KakaoTokenResponse, Component, ConfigurationProperties (+3 more)

### Community 32 - "StockMasterService"
Cohesion: 0.26
Nodes (7): GetMapping, RestController, PriceController, StockInfo, PostConstruct, Service, StockMasterService

### Community 33 - ".calculate"
Cohesion: 0.26
Nodes (3): TradeAmountCalculator, Test, TradeAmountCalculatorTest

### Community 34 - ".validate"
Cohesion: 0.31
Nodes (3): TradeAvailabilityValidator, Test, TradeAvailabilityValidatorTest

### Community 35 - "MySqlTestContainerConfiguration.java"
Cohesion: 0.26
Nodes (10): MySQLContainer, ServiceConnection, AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, RoomParticipantRepositoryTest, Bean (+2 more)

### Community 36 - "StockMasterStockNameProvider"
Cohesion: 0.26
Nodes (7): Component, Override, RequiredArgsConstructor, StockMasterStockNameProvider, ExtendWith, Test, StockMasterStockNameProviderTest

### Community 37 - "SellTradeService"
Cohesion: 0.23
Nodes (6): Pattern, StockCodeValidator, RequiredArgsConstructor, Service, Transactional, SellTradeService

### Community 38 - "HoldingRepository"
Cohesion: 0.29
Nodes (8): HoldingRepository, BuyTradeProcessor, RequiredArgsConstructor, Service, Transactional, BuyTradeProcessorTest, ExtendWith, Test

### Community 39 - "CommunityException"
Cohesion: 0.23
Nodes (9): CommunityErrorCode, COMMENT_ACCESS_DENIED, COMMENT_NOT_FOUND, POST_ACCESS_DENIED, POST_NOT_FOUND, Getter, RequiredArgsConstructor, CommunityException (+1 more)

### Community 41 - "TradeSecurityIntegrationTest"
Cohesion: 0.29
Nodes (7): BeforeEach, MockMvc, SpringBootTest, Test, TradeSecurityIntegrationTest, WebAppConfiguration, WebApplicationContext

### Community 42 - "README.md"
Cohesion: 0.18
Nodes (9): Admin Domain, Backoffice Domain, Community Domain, Member Domain, Portfolio Domain, Price Domain, Ranking Domain, Room Domain (+1 more)

### Community 43 - "SecurityConfig.java"
Cohesion: 0.33
Nodes (8): EnableWebSecurity, HttpSecurity, SecurityFilterChain, Bean, Configuration, PasswordEncoder, RequiredArgsConstructor, SecurityConfig

### Community 44 - ".doFilterInternal"
Cohesion: 0.33
Nodes (8): FilterChain, HttpServletRequest, HttpServletResponse, OncePerRequestFilter, Component, Override, RequiredArgsConstructor, JwtAuthenticationFilter

### Community 45 - "PostService"
Cohesion: 0.33
Nodes (6): Page, Pageable, RequiredArgsConstructor, Service, Transactional, PostService

### Community 47 - "WebSocketConfig.java"
Cohesion: 0.33
Nodes (7): EnableWebSocketMessageBroker, MessageBrokerRegistry, Configuration, Override, WebSocketConfig, StompEndpointRegistry, WebSocketMessageBrokerConfigurer

### Community 48 - "GlobalExceptionHandler"
Cohesion: 0.42
Nodes (5): MethodArgumentNotValidException, GlobalExceptionHandler, ExceptionHandler, ResponseEntity, RestControllerAdvice

### Community 49 - "TradeRankingIntegrationTest.java"
Cohesion: 0.43
Nodes (6): AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional, TradeRankingIntegrationTest

### Community 50 - "HoldingRepositoryTest"
Cohesion: 0.36
Nodes (6): HoldingRepositoryTest, AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, Test

### Community 51 - "TransactionRepositoryTest"
Cohesion: 0.21
Nodes (9): Page, Pageable, Query, AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, Test (+1 more)

### Community 52 - "TransactionCertificateProvider"
Cohesion: 0.31
Nodes (5): Component, Override, StubTransactionCertificateProvider, TransactionCertificateProvider, TransactionSnapshot

### Community 53 - "TradeOrderRequestTest"
Cohesion: 0.39
Nodes (3): Test, TradeOrderRequestTest, Validator

### Community 54 - "CommunityExceptionHandler.java"
Cohesion: 0.43
Nodes (6): CommunityExceptionHandler, ErrorResponse, ExceptionHandler, ResponseEntity, RestControllerAdvice, Slf4j

### Community 55 - "RedisConfig.java"
Cohesion: 0.50
Nodes (5): Bean, Configuration, RedisConnectionFactory, RedisTemplate, RedisConfig

### Community 56 - "BuyTradeProcessorIntegrationTest.java"
Cohesion: 0.43
Nodes (6): BuyTradeProcessorIntegrationTest, AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, Test

### Community 57 - "SellTradeConcurrencyTest.java"
Cohesion: 0.22
Nodes (8): ExtendWith, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional, SellTradeConcurrencyTest, ExtendWith

### Community 58 - "SellTradeRollbackIntegrationTest.java"
Cohesion: 0.43
Nodes (6): AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional, SellTradeRollbackIntegrationTest

### Community 59 - "PriceConfig.java"
Cohesion: 0.48
Nodes (5): EnableConfigurationProperties, Bean, Configuration, WebClient, PriceConfig

### Community 60 - ".from"
Cohesion: 0.33
Nodes (3): KisRealtimePriceMessage, Test, KisRealtimePriceMessageTest

### Community 62 - "BuySellConcurrencyTest.java"
Cohesion: 0.43
Nodes (6): BuySellConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 63 - "CorsConfig.java"
Cohesion: 0.53
Nodes (4): CorsConfigurationSource, CorsConfig, Bean, Configuration

### Community 64 - "application.yml"
Cohesion: 0.40
Nodes (4): Kakao OAuth API, External Stock API, MySQL Database, Redis Cache

### Community 65 - "DailyStats"
Cohesion: 0.33
Nodes (5): DailyStats, Entity, Getter, NoArgsConstructor, Table

### Community 67 - "KisRealtimeSubscribeRequest.java"
Cohesion: 0.73
Nodes (4): Body, Header, Input, KisRealtimeSubscribeRequest

### Community 68 - "TockTalksApplication"
Cohesion: 0.60
Nodes (3): EnableScheduling, SpringBootApplication, TockTalksApplication

### Community 69 - "HealthController.java"
Cohesion: 0.60
Nodes (3): HealthController, GetMapping, RestController

### Community 70 - "BuyTradeRollbackIntegrationTest.java"
Cohesion: 0.43
Nodes (6): BuyTradeRollbackIntegrationTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 71 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **18 isolated node(s):** `POST_NOT_FOUND`, `POST_ACCESS_DENIED`, `COMMENT_NOT_FOUND`, `COMMENT_ACCESS_DENIED`, `RETURN_RATE` (+13 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `RoomParticipantRepository` connect `RoomParticipantRepository` to `JpaRepository`, `TradeController.java`, `MySqlTestContainerConfiguration.java`, `SellTradeService`, `PortfolioService.java`, `BuyTradeRollbackIntegrationTest.java`, `TradeOrderRequest`, `TradeRankingIntegrationTest.java`, `RoomParticipant.java`, `TradeHistoryService`, `SellTradeConcurrencyTest.java`, `SellTradeRollbackIntegrationTest.java`, `BuyTradeService`, `BuySellConcurrencyTest.java`?**
  _High betweenness centrality (0.153) - this node is a cross-community bridge._
- **Why does `RoomService` connect `RoomParticipantRepository` to `AuthService`, `RankingService`, `RoomParticipant.java`, `RoomRepository`?**
  _High betweenness centrality (0.131) - this node is a cross-community bridge._
- **Why does `AuthService` connect `AuthService` to `RoomParticipantRepository`, `AuthController`, `JwtProvider`, `RefreshTokenService`, `LoginAttemptService`, `KakaoOAuthClient`?**
  _High betweenness centrality (0.099) - this node is a cross-community bridge._
- **Are the 25 inferred relationships involving `TradeOrderRequest` (e.g. with `.로그인_회원이_종목을_매도한다()` and `.로그인_회원이_종목을_매수한다()`) actually correct?**
  _`TradeOrderRequest` has 25 INFERRED edges - model-reasoned connections that need verification._
- **What connects `POST_NOT_FOUND`, `POST_ACCESS_DENIED`, `COMMENT_NOT_FOUND` to the rest of the system?**
  _18 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `RoomParticipantRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.062037037037037036 - nodes in this community are weakly interconnected._
- **Should `JpaRepository` be split into smaller, more focused modules?**
  _Cohesion score 0.053554040895813046 - nodes in this community are weakly interconnected._