# Domain Glossary

> 도메인 모델의 기준 문서. 구현이 확정된 내용과 아직 설계 예정인 내용을 구분한다.

## 핵심 관계

```text
Product
  └─ PricingPolicy

QuoteRequest
  └─ Product + 수량 + 약정 + 견적일 + 조건
              ↓
        PricingPolicy 선택
              ↓
        DiscountRule 평가
              ↓
          QuoteResult
           ├─ QuoteItem
           ├─ AppliedDiscount
           └─ CalculationReason
              ↓
            확정
              ↓
           Snapshot
```

## Product — 구현됨

견적 가능한 가상 상품. 실제 통신사의 내부 상품을 복제하지 않는다.

- `id`: PostgreSQL 내부 식별자(Long)
- `code`: 공백 제거 후 대문자 저장. `[A-Z0-9_]` 1~64자, unique, 생성 후 불변
- `name`: 공백 제거 후 1~100자
- `category`: `INTERNET`, `PHONE`, `WIFI`, `IPTV`, `SECURITY`, `ETC`
- `description`: 선택, 최대 2000자
- `active`: 신규 true. false면 새 견적에서 사용할 수 없는 상태

물리 DELETE를 제공하지 않는다. 과거 Quote/Snapshot이 참조할 수 있기 때문이다.

## PricingPolicy — 다음 구현 대상

특정 Product의 가격을 **약정조건과 유효기간**에 따라 표현하는 정책.

필수로 표현해야 하는 개념:

- 대상 Product
- 월 이용료
- 약정기간(contractMonths)
- 적용 시작일(validFrom)
- 적용 종료일(validTo 또는 open-ended 정책)

핵심 규칙:

- 월 이용료는 음수가 될 수 없다.
- 약정기간은 지원하는 양의 개월 수여야 한다.
- 종료일이 있다면 시작일보다 빠를 수 없다.
- 하나의 Product에는 여러 과거/현재/미래 정책이 존재할 수 있다.
- 견적 시점에는 **견적일 + 약정기간**에 맞는 정책 하나를 결정론적으로 선택해야 한다.
- 동일 조건에서 복수 정책이 동시에 유효해 선택이 모호해지는 경우를 어떻게 막거나 해결할지 Issue #5에서 명시한다.

## QuoteRequest — 설계 예정

견적 계산을 위한 비식별 입력.

최소 후보:

- quoteDate
- productId
- quantity
- contractMonths
- 할인 판별에 필요한 조건

고객 이름, 전화번호, 주민등록번호 등은 핵심 계산에 필요하지 않으므로 저장하지 않는다.

## QuoteResult / Quote — 설계 예정

Quote Engine이 반환하는 계산 결과.

최소한 다음을 설명할 수 있어야 한다.

- 선택한 Product
- 선택한 PricingPolicy
- 수량
- 기본 금액
- 적용된 할인
- 제외된 할인과 이유
- 최종 금액
- 계산 근거

저장 전 계산 결과와 저장되는 Quote aggregate의 이름/경계는 Issue #6에서 최종 확정한다.

## QuoteItem — 설계 예정

견적 안의 상품별 계산 단위.

MVP 초기에 단일 Product 견적으로 시작할 수 있지만, 이후 복수 상품을 지원할 수 있도록 계산 책임을 분리할지 Quote Engine 구현 시 결정한다.

## DiscountRule — 설계 예정

조건을 만족할 때 적용되는 데이터 기반 할인 규칙.

표현 대상:

- 할인 방식: 정액 / 정률
- 할인 값
- 적용 조건
- 유효기간
- 우선순위
- 다른 할인과의 중복 가능 여부

기간 한정 프로모션도 별도 Promotion 도메인을 만들기보다 초기에는 DiscountRule로 표현한다.

## AppliedDiscount — 설계 예정

DiscountRule 정의와 실제 견적에서의 적용 결과를 분리한다.

예:

- 어떤 Rule이 후보였는가
- 실제 적용됐는가
- 얼마가 할인됐는가
- 제외됐다면 이유가 무엇인가

## CalculationReason — 개념

최종 견적의 설명 가능성을 위한 정보.

별도 Entity가 될 필요는 없으며 DTO/value 형태일 수 있다.

예:

- “2026-10-02에 유효한 36개월 가격정책 선택”
- “결합할인 조건 충족”
- “신규가입 할인은 결합할인과 중복 불가하여 제외”

핵심은 엔진이 최종 숫자만 반환하지 않는 것이다.

## Snapshot — 설계 예정

Quote 확정 시점의 계산 결과를 복사해 보존한 데이터.

정책 원본의 ID만 다시 조회해 현재 상태로 재계산하지 않는다.

Snapshot에 최소 보존할 후보:

- 상품 code/name
- 약정기간
- 적용 가격
- 적용 가격정책 식별 정보
- 적용 할인명/금액
- 기본 금액
- 최종 금액
- 확정 시각

이후 정책이 수정·비활성화되어도 확정 견적 결과는 변하지 않아야 한다.

## User / Role — 후반 구현

최소 역할은 STAFF / ADMIN.

- STAFF: 견적 계산 및 확정
- ADMIN: Product / PricingPolicy / DiscountRule 관리

인증은 프로젝트의 핵심 주제가 아니므로 견적 엔진보다 뒤에서 최소 범위로 구현한다.

## RequiredItem / QuoteChecklist — Stretch

공식 접수 전에 확인할 항목을 관리하는 보조 기능.

초기 기획에서는 MVP였지만 2026-10 재정의에서 핵심 견적 엔진 밖으로 이동했다. 엔진·Frontend·Snapshot이 완성된 뒤 여유가 있을 때만 구현한다.

## AuditLog — Stretch

정책 변경 이력. 포트폴리오 핵심 시나리오 완료 후 필요성을 재검토한다.
