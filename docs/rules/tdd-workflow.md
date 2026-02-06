# TDD 워크플로우 (Red → Green → Refactor)

## Macro to Micro 원칙 (Outside-In TDD)

> 기능 구현은 **상위 레이어에서 하위 레이어로** 진행한다. 각 레이어에서 TDD 사이클(Red → Green → Refactor)을 완료한 후 다음 레이어로 이동한다.

### 레이어별 진행 순서

```
Controller Test (Red) → Controller 구현 (Green/Refactor)
    ↓
Service Test (Red) → Service 구현 (Green/Refactor)
    ↓
Repository Test (Red) → Repository 구현 (Green/Refactor)
```

### 단계별 워크플로우

1. **Controller Layer**
   - Controller 테스트 작성 (Red) - HTTP 요청/응답 검증
   - Controller 구현 (Green) - Service는 Mock/Stub 사용
   - Refactor

2. **Service/Facade Layer**
   - Service 테스트 작성 (Red) - 비즈니스 로직 검증
   - Service 구현 (Green) - Repository는 Mock/Stub 사용
   - Refactor

3. **Domain/Repository Layer**
   - Domain 로직 테스트 작성 (Red)
   - Domain 구현 (Green)
   - Repository 통합 테스트로 영속성 검증
   - Refactor

## 3A 원칙

모든 테스트는 다음 구조를 따른다:

```kotlin
@Test
@DisplayName("주문 생성 시 재고가 부족하면 예외를 던진다")
fun create_insufficientStock() {
    // Arrange - 테스트 데이터 준비
    // Act - 테스트 대상 실행
    // Assert - 결과 검증
}
```

## Red Phase (테스트 작성)

- 실패하는 테스트를 먼저 작성한다
- `@DisplayName`에 한국어로 테스트 의도를 명확히 기술한다
- `@Nested` inner class로 관련 테스트를 논리적으로 그룹핑한다
- 한 테스트 메서드는 하나의 동작만 검증한다

## Green Phase (최소 구현)

- 테스트를 통과하는 **최소한의 코드**만 작성한다
- 오버엔지니어링 금지:
  - 요구사항에 없는 예외 처리를 추가하지 않는다
  - 불필요한 추상화(인터페이스, 추상 클래스)를 만들지 않는다
  - 현재 필요하지 않은 확장 포인트를 만들지 않는다

## Refactor Phase (리팩토링)

- 중복 코드를 제거하고 가독성을 개선한다
- private 함수 추출보다 **객체에게 메시지를 보내는 방식**을 우선한다
- 리팩토링 후 반드시 확인:
  - 모든 테스트 통과: `./gradlew test`
  - 린트 통과: `./gradlew ktlintCheck`
