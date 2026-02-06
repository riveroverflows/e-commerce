# 테스트 레벨별 가이드라인

> 테스트는 범위에 따라 역할과 책임이 나뉜다. 하단(Unit)일수록 빠르고 많이, 상단(E2E)일수록 느리지만 신중하게 구성한다.

## Macro to Micro 진행 원칙

기능 구현 시 테스트 레벨은 **상위에서 하위로** 진행한다:

| 순서 | 레이어 | 테스트 유형 | Mock 대상 |
|------|--------|------------|----------|
| 1 | Controller | E2E Test | Service |
| 2 | Service/Facade | Unit Test | Repository, 외부 API |
| 3 | Domain | Unit Test | 없음 (순수 로직) |
| 4 | Repository | Integration Test | 없음 (실제 DB) |

> 각 레이어에서 Red → Green → Refactor를 완료한 후 다음 레이어로 이동한다.

## Unit Test

- 클래스명: `{Class}Test` (예: `OrderServiceTest`, `OrderFacadeTest`)
- 대상: 도메인 모델(Entity, VO, Domain Service)과 application 계층(Facade)의 비즈니스 로직
  - 도메인 계층: 순수 비즈니스 규칙을 Mock 없이 검증한다
  - application 계층: 테스트 대역(Mock, Stub, Fake)으로 외부 의존성(Repository, 외부 API 등)을 대체하여 오케스트레이션 로직을 검증한다
- 환경: Spring Context **없이** 순수 JVM에서 실행한다 (`@SpringBootTest` 사용 금지)
- 검증 패턴:
  - 예외 검증: `assertThrows<CoreException> { ... }`
  - 복합 검증: `assertAll()` 로 관련 assertion을 묶는다

## Integration Test

- 클래스명: `{Class}IntegrationTest` (예: `OrderFacadeIntegrationTest`)
- 대상: Service, Facade 등 애플리케이션 계층 로직
- 목적: 여러 컴포넌트(Repository, Domain, 외부 API Stub)가 연결된 상태에서 비즈니스 흐름 전체를 검증한다
- `@SpringBootTest` + 생성자 주입으로 구성한다
- `@AfterEach`에서 `databaseCleanUp.truncateAllTables()`로 데이터를 정리한다
- Testcontainers를 사용하여 실제 인프라(MySQL, Redis, Kafka)와 통합 테스트한다

## E2E Test

- 클래스명: `{Class}E2ETest` (예: `OrderV1ControllerE2ETest`)
- 대상: 전체 애플리케이션 (Controller -> Service -> DB)
- 목적: 실제 HTTP 요청 단위의 시나리오를 검증한다
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate`, `MockMvc`, `WebTestClient` 중 선택하여 구성한다
- HTTP 상태 코드와 응답 구조(`ApiResponse`)를 함께 검증한다

## 테스트 더블(Test Doubles)

> 테스트 더블은 **역할(목적)** 이고, `mock()`과 `spy()`는 **도구(생성 방식)** 이다. 하나의 mock 객체에 Stub + Mock 역할을 동시에 부여할 수 있다.

### 역할별 정의

| 역할 | 목적 | 사용 방식 |
|------|------|-----------|
| **Dummy** | 자리만 채움 (호출되지 않음) | 생성자 파라미터 등에 전달 |
| **Stub** | 고정된 응답 반환 (상태 기반) | `whenever().thenReturn()` / `every {} returns` |
| **Mock** | 호출 여부·횟수 검증 (행위 기반) | `verify()` |
| **Spy** | 실제 객체를 감싸고 일부만 조작 | `spy()` + `doReturn()` / `every {} answers` |
| **Fake** | 동작하는 경량 구현체 | `InMemoryRepository`, `FakeEmailSender` 등 |

### 프로젝트 사용 원칙

- 도메인 계층(Entity, VO, Domain Service)은 테스트 더블 없이 순수 로직을 검증한다
- application 계층(Facade)의 외부 의존성(Repository, 외부 API)에만 테스트 더블을 사용한다
- 도구는 JUnit 5 + Mockito-Kotlin(`whenever`, `verify`, `mock()`)을 사용한다
- Spy는 레거시 코드 테스트 등 불가피한 경우에만 허용한다

## 공통 규칙

- 메서드명: `{action}_{condition}()` 형식, 영어로 작성 (예: `create_insufficientStock()`)
- `@DisplayName` 한국어 설명 필수
- `@Nested` inner class로 논리적 그룹핑 (예: `inner class Create`, `inner class Delete`)
- 생성자 주입 사용, 필드 주입(`@Autowired` 필드) 금지
- 테스트 간 상태 공유 금지 (각 테스트는 독립적으로 실행 가능해야 한다)
