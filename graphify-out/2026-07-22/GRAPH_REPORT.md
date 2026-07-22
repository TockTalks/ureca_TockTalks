# Graph Report - C:\Users\howar\Downloads\ureca_TockTalks  (2026-07-22)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1457 nodes · 3316 edges · 84 communities (60 shown, 24 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 334 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9a6685dc`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Post
- RankingService
- TransactionRepository
- TradeController.java
- PortfolioService.java
- AuthService
- RoomService
- KisPriceService
- .create
- JpaRepository
- JwtProvider
- FavoriteStock
- NoticeController.java
- RoomParticipant
- TradeOrderRequest
- RankingSubscriber
- HoldingRepository
- KisWebSocketClient
- KakaoOAuthClient
- RoomParticipant.java
- .calculate
- BuyTradeService
- PriceSubscriptionTracker
- LoginMemberIdArgumentResolver.java
- CommentController.java
- RoomParticipantRepository
- CommentLike
- LoginAttemptService
- StockMasterService
- RoomRepository
- .validate
- SellTradeConcurrencyTest.java
- CommentService.java
- StockMasterStockNameProvider
- TradeSecurityIntegrationTest
- TradeRankingService
- CommunityException
- README.md
- Comment
- CommentLikeService.java
- WebSocketConfig.java
- RoomParticipantRepositoryTest
- TradeOrderRequestTest
- CommunityExceptionHandler.java
- RedisConfig.java
- BuyTradeRollbackIntegrationTest.java
- .updateComment
- KisRealtimeSubscribeRequest.java
- .parsesRealtimeData
- DifferentParticipantConcurrencyTest.java
- CorsConfig.java
- application.yml
- DailyStats
- PricePublisher
- RoomProperties
- TockTalksApplication
- HealthController.java
- gradlew
- EmailCheckResponse.java
- KakaoLoginRequest.java
- LoginRequest.java
- ReissueRequest.java
- SignupRequest.java
- TokenResponse.java
- Bean
- Configuration
- ExceptionHandler
- GetMapping
- RestController
- RestControllerAdvice
- PostMapping
- PasswordEncoder
- Entity
- Getter
- NoArgsConstructor
- Table
- ResponseEntity
- ObjectMapper
- Override
- StringRedisTemplate
- Query

## God Nodes (most connected - your core abstractions)
1. `RoomParticipantRepository` - 47 edges
2. `HoldingRepository` - 38 edges
3. `TransactionRepository` - 38 edges
4. `TradeOrderRequest` - 35 edges
5. `RoomService` - 33 edges
6. `RoomRepository` - 31 edges
7. `Post` - 26 edges
8. `Transaction` - 26 edges
9. `KisWebSocketClient` - 25 edges
10. `AuthService` - 24 edges

## Surprising Connections (you probably didn't know these)
- `CommentController` --references--> `CommentLikeService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/controller/CommentController.java → src/main/java/com/tocktalks/domain/community/service/CommentLikeService.java
- `CommentController` --references--> `CommentService`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/controller/CommentController.java → src/main/java/com/tocktalks/domain/community/service/CommentService.java
- `CommentRepository` --references--> `Comment`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/repository/CommentRepository.java → src/main/java/com/tocktalks/domain/community/entity/Comment.java
- `CommentLikeService` --references--> `CommentLikeRepository`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/service/CommentLikeService.java → src/main/java/com/tocktalks/domain/community/repository/CommentLikeRepository.java
- `CommentService` --references--> `CommentLikeRepository`  [EXTRACTED]
  src/main/java/com/tocktalks/domain/community/service/CommentService.java → src/main/java/com/tocktalks/domain/community/repository/CommentLikeRepository.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **TockTalks Core Domains** — domain_member, domain_room, domain_trade, domain_price, domain_ranking, domain_portfolio, domain_community [EXTRACTED 1.00]
- **Infrastructure Services** — mysql_db, redis_db, docker_compose [EXTRACTED 1.00]

## Communities (84 total, 24 thin omitted)

### Community 0 - "Post"
Cohesion: 0.06
Nodes (38): DeleteMapping, GetMapping, Page, Pageable, PatchMapping, PostMapping, RequestMapping, RequiredArgsConstructor (+30 more)

### Community 1 - "RankingService"
Cohesion: 0.06
Nodes (37): AfterEach, GetMapping, RequestMapping, RequiredArgsConstructor, RestController, RankingController, RankingArchiveResponse, RankingDto (+29 more)

### Community 2 - "TransactionRepository"
Cohesion: 0.06
Nodes (37): Primary, Component, Override, StubTransactionCertificateProvider, TransactionCertificateProvider, TransactionSnapshot, Entity, Getter (+29 more)

### Community 3 - "TradeController.java"
Cohesion: 0.06
Nodes (34): MethodArgumentNotValidException, Authentication, GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor (+26 more)

### Community 4 - "PortfolioService.java"
Cohesion: 0.06
Nodes (39): AssetHistoryRepository, CurrentPriceProvider, Holding, HoldingRepository, ResponseEntity, AssetHistoryResponse, Authentication, GetMapping (+31 more)

### Community 5 - "AuthService"
Cohesion: 0.06
Nodes (37): EmailCheckResponse, Entity, Getter, JwtProvider, KakaoLoginRequest, KakaoUserInfoResponse, LoginAttemptService, NoArgsConstructor (+29 more)

### Community 6 - "RoomService"
Cohesion: 0.07
Nodes (31): CreateRoomRequest, RankingDto, RoomParticipantResponse, RoomProperties, RoomRankingResponse, RoomResponse, Authentication, DeleteMapping (+23 more)

### Community 7 - "KisPriceService"
Cohesion: 0.06
Nodes (31): EnableConfigurationProperties, ConfigurationProperties, KisApiProperties, Bean, Configuration, WebClient, PriceConfig, KisApprovalRequest (+23 more)

### Community 8 - ".create"
Cohesion: 0.08
Nodes (26): Holding, Entity, Getter, NoArgsConstructor, Table, RequiredArgsConstructor, Service, Transactional (+18 more)

### Community 9 - "JpaRepository"
Cohesion: 0.06
Nodes (34): AllArgsConstructor, JpaRepository, AdminReportController, GetMapping, Page, Pageable, PatchMapping, RequestMapping (+26 more)

### Community 10 - "JwtProvider"
Cohesion: 0.07
Nodes (31): Claims, EnableWebSecurity, FilterChain, HttpSecurity, HttpServletRequest, HttpServletResponse, OncePerRequestFilter, SecretKey (+23 more)

### Community 11 - "FavoriteStock"
Cohesion: 0.08
Nodes (23): Retention, FavoriteStockController, DeleteMapping, GetMapping, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+15 more)

### Community 12 - "NoticeController.java"
Cohesion: 0.10
Nodes (25): GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+17 more)

### Community 13 - "RoomParticipant"
Cohesion: 0.09
Nodes (16): Entity, Getter, NoArgsConstructor, Table, RoomParticipant, RequiredArgsConstructor, Service, Transactional (+8 more)

### Community 14 - "TradeOrderRequest"
Cohesion: 0.19
Nodes (8): TradeOrderRequest, BuyTradeServiceTest, ExtendWith, Test, Test, SellTradeServiceTest, Test, Transactional

### Community 15 - "RankingSubscriber"
Cohesion: 0.14
Nodes (19): MessageListener, RedisMessageListenerContainer, Bean, Configuration, RedisConnectionFactory, PriceRedisConfig, Message, Override (+11 more)

### Community 16 - "HoldingRepository"
Cohesion: 0.16
Nodes (15): HoldingRepository, BuyTradeProcessor, RequiredArgsConstructor, Service, Transactional, BuyTradeProcessorTest, ExtendWith, Test (+7 more)

### Community 17 - "KisWebSocketClient"
Cohesion: 0.17
Nodes (12): CloseStatus, KisApiProperties, KisAuthService, ObjectMapper, Override, PricePublisher, Service, KisWebSocketClient (+4 more)

### Community 18 - "KakaoOAuthClient"
Cohesion: 0.16
Nodes (15): RestClient, Component, RequiredArgsConstructor, KakaoOAuthClient, JsonIgnoreProperties, KakaoTokenResponse, JsonIgnoreProperties, KakaoAccount (+7 more)

### Community 19 - "RoomParticipant.java"
Cohesion: 0.13
Nodes (13): RequiredArgsConstructor, Service, Transactional, SellTradeService, BuySellConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import (+5 more)

### Community 20 - ".calculate"
Cohesion: 0.18
Nodes (5): TradeAmountCalculator, Test, TradeExecutionResponseTest, Test, TradeAmountCalculatorTest

### Community 21 - "BuyTradeService"
Cohesion: 0.20
Nodes (10): BuyTradeService, RequiredArgsConstructor, Service, Transactional, CurrentPriceProvider, StockNameProvider, AutoConfigureTestDatabase, DataJpaTest (+2 more)

### Community 22 - "PriceSubscriptionTracker"
Cohesion: 0.20
Nodes (8): Component, EventListener, Pattern, SessionDisconnectEvent, SessionSubscribeEvent, SessionUnsubscribeEvent, PriceSubscriptionTracker, StockCodeValidator

### Community 23 - "LoginMemberIdArgumentResolver.java"
Cohesion: 0.20
Nodes (12): HandlerMethodArgumentResolver, MethodParameter, ModelAndViewContainer, NativeWebRequest, Configuration, HandlerMethodArgumentResolver, Override, WebMvcConfig (+4 more)

### Community 24 - "CommentController.java"
Cohesion: 0.18
Nodes (11): CommentController, DeleteMapping, GetMapping, Page, Pageable, PostMapping, RequestMapping, RequiredArgsConstructor (+3 more)

### Community 25 - "RoomParticipantRepository"
Cohesion: 0.20
Nodes (10): Lock, Query, RoomParticipant, RoomParticipantRepository, BuyTradeConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import (+2 more)

### Community 26 - "CommentLike"
Cohesion: 0.18
Nodes (6): CommentLike, Entity, Getter, NoArgsConstructor, Table, CommentLikeRepository

### Community 27 - "LoginAttemptService"
Cohesion: 0.24
Nodes (9): RedisTemplate, RequiredArgsConstructor, Service, LoginAttemptService, Component, ConfigurationProperties, Getter, Setter (+1 more)

### Community 28 - "StockMasterService"
Cohesion: 0.26
Nodes (7): GetMapping, RestController, PriceController, StockInfo, PostConstruct, Service, StockMasterService

### Community 29 - "RoomRepository"
Cohesion: 0.19
Nodes (6): Entity, Getter, NoArgsConstructor, Table, Room, RoomRepository

### Community 30 - ".validate"
Cohesion: 0.31
Nodes (3): TradeAvailabilityValidator, Test, TradeAvailabilityValidatorTest

### Community 31 - "SellTradeConcurrencyTest.java"
Cohesion: 0.22
Nodes (11): MySQLContainer, ServiceConnection, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional, SellTradeConcurrencyTest (+3 more)

### Community 32 - "CommentService.java"
Cohesion: 0.29
Nodes (7): CommentResponse, CommentService, Page, Pageable, RequiredArgsConstructor, Service, Transactional

### Community 33 - "StockMasterStockNameProvider"
Cohesion: 0.26
Nodes (7): Component, Override, RequiredArgsConstructor, StockMasterStockNameProvider, ExtendWith, Test, StockMasterStockNameProviderTest

### Community 34 - "TradeSecurityIntegrationTest"
Cohesion: 0.26
Nodes (7): BeforeEach, MockMvc, SpringBootTest, Test, TradeSecurityIntegrationTest, WebAppConfiguration, WebApplicationContext

### Community 35 - "TradeRankingService"
Cohesion: 0.24
Nodes (9): RankingService, RequiredArgsConstructor, Service, Slf4j, TradeAssetService, TradeRankingService, ExtendWith, Test (+1 more)

### Community 36 - "CommunityException"
Cohesion: 0.23
Nodes (9): CommunityErrorCode, COMMENT_ACCESS_DENIED, COMMENT_NOT_FOUND, POST_ACCESS_DENIED, POST_NOT_FOUND, Getter, RequiredArgsConstructor, CommunityException (+1 more)

### Community 37 - "README.md"
Cohesion: 0.18
Nodes (9): Admin Domain, Backoffice Domain, Community Domain, Member Domain, Portfolio Domain, Price Domain, Ranking Domain, Room Domain (+1 more)

### Community 38 - "Comment"
Cohesion: 0.24
Nodes (5): Comment, Entity, Getter, NoArgsConstructor, Table

### Community 39 - "CommentLikeService.java"
Cohesion: 0.29
Nodes (7): CommentRepository, Page, Pageable, CommentLikeService, RequiredArgsConstructor, Service, Transactional

### Community 40 - "WebSocketConfig.java"
Cohesion: 0.33
Nodes (7): EnableWebSocketMessageBroker, MessageBrokerRegistry, Configuration, Override, WebSocketConfig, StompEndpointRegistry, WebSocketMessageBrokerConfigurer

### Community 41 - "RoomParticipantRepositoryTest"
Cohesion: 0.39
Nodes (6): AutoConfigureTestDatabase, DataJpaTest, EntityManager, Import, Test, RoomParticipantRepositoryTest

### Community 42 - "TradeOrderRequestTest"
Cohesion: 0.39
Nodes (3): Test, TradeOrderRequestTest, Validator

### Community 43 - "CommunityExceptionHandler.java"
Cohesion: 0.43
Nodes (6): CommunityExceptionHandler, ErrorResponse, ExceptionHandler, ResponseEntity, RestControllerAdvice, Slf4j

### Community 44 - "RedisConfig.java"
Cohesion: 0.50
Nodes (5): Bean, Configuration, RedisConnectionFactory, RedisTemplate, RedisConfig

### Community 45 - "BuyTradeRollbackIntegrationTest.java"
Cohesion: 0.43
Nodes (6): BuyTradeRollbackIntegrationTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 47 - "KisRealtimeSubscribeRequest.java"
Cohesion: 0.71
Nodes (4): Body, Header, Input, KisRealtimeSubscribeRequest

### Community 48 - ".parsesRealtimeData"
Cohesion: 0.33
Nodes (3): KisRealtimePriceMessage, Test, KisRealtimePriceMessageTest

### Community 49 - "DifferentParticipantConcurrencyTest.java"
Cohesion: 0.43
Nodes (6): DifferentParticipantConcurrencyTest, AutoConfigureTestDatabase, DataJpaTest, Import, Test, Transactional

### Community 50 - "CorsConfig.java"
Cohesion: 0.53
Nodes (4): CorsConfigurationSource, CorsConfig, Bean, Configuration

### Community 51 - "application.yml"
Cohesion: 0.40
Nodes (4): Kakao OAuth API, External Stock API, MySQL Database, Redis Cache

### Community 52 - "DailyStats"
Cohesion: 0.33
Nodes (5): DailyStats, Entity, Getter, NoArgsConstructor, Table

### Community 53 - "PricePublisher"
Cohesion: 0.53
Nodes (3): Service, StringRedisTemplate, PricePublisher

### Community 54 - "RoomProperties"
Cohesion: 0.60
Nodes (5): Component, ConfigurationProperties, Getter, Setter, RoomProperties

### Community 55 - "TockTalksApplication"
Cohesion: 0.60
Nodes (3): EnableScheduling, SpringBootApplication, TockTalksApplication

### Community 56 - "HealthController.java"
Cohesion: 0.60
Nodes (3): HealthController, GetMapping, RestController

### Community 57 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **24 isolated node(s):** `EmailCheckResponse`, `KakaoLoginRequest`, `LoginRequest`, `ReissueRequest`, `SignupRequest` (+19 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **24 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `RoomParticipantRepository` connect `RoomParticipantRepository` to `TradeController.java`, `PortfolioService.java`, `TradeRankingService`, `RoomService`, `JpaRepository`, `RoomParticipantRepositoryTest`, `BuyTradeRollbackIntegrationTest.java`, `TradeOrderRequest`, `HoldingRepository`, `DifferentParticipantConcurrencyTest.java`, `RoomParticipant.java`, `BuyTradeService`, `SellTradeConcurrencyTest.java`?**
  _High betweenness centrality (0.199) - this node is a cross-community bridge._
- **Why does `RoomService` connect `RoomService` to `RoomParticipantRepository`, `TradeRankingService`, `AuthService`?**
  _High betweenness centrality (0.108) - this node is a cross-community bridge._
- **Why does `RankingService` connect `RankingService` to `RoomParticipant`, `TradeRankingService`, `BuyTradeService`?**
  _High betweenness centrality (0.096) - this node is a cross-community bridge._
- **Are the 25 inferred relationships involving `TradeOrderRequest` (e.g. with `.로그인_회원이_종목을_매도한다()` and `.로그인_회원이_종목을_매수한다()`) actually correct?**
  _`TradeOrderRequest` has 25 INFERRED edges - model-reasoned connections that need verification._
- **What connects `EmailCheckResponse`, `KakaoLoginRequest`, `LoginRequest` to the rest of the system?**
  _24 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Post` be split into smaller, more focused modules?**
  _Cohesion score 0.05582603050957481 - nodes in this community are weakly interconnected._
- **Should `RankingService` be split into smaller, more focused modules?**
  _Cohesion score 0.058738738738738736 - nodes in this community are weakly interconnected._