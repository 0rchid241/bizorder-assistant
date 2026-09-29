# Domain Glossary

개발 중 용어가 변경되면 코드와 문서를 함께 갱신한다.

## Product
판매 가능한 통신 상품. 실제 통신사의 내부 상품을 복제하지 않고 가상 상품을 사용한다.

## PricingPolicy
Product의 가격을 결정하는 정책. 월 이용료, 설치비, 약정기간, 유효기간 등을 가진다.

## DiscountRule
조건을 만족할 때 적용되는 할인 규칙. 정액/정률, 유효기간, 중복 가능 여부, 우선순위 등을 가진다. 초기에는 기간 한정 프로모션도 이 모델로 표현한다.

## QuoteRequest
견적 계산에 필요한 입력조건. 고객 개인정보보다 상품·수량·약정 등 비식별 조건 중심으로 구성한다.

## Quote
계산된 견적의 aggregate root 후보. 초기 상태는 DRAFT / CONFIRMED를 고려한다.

## QuoteItem
견적에 포함된 개별 상품 항목.

## AppliedDiscount
DiscountRule 자체와 구분되는 실제 할인 적용 결과.

## Snapshot
견적 확정 시점의 상품·가격·할인 결과를 복사해 보존한 데이터.

## RequiredItem
공식 접수 전에 확인할 항목의 정의. 실제 개인정보나 문서 원본 저장 용도가 아니다.

## QuoteChecklist
특정 견적의 RequiredItem 확인 상태.

## User / Role
시스템 사용자와 권한. 초기 역할은 STAFF / ADMIN.

## AuditLog
중요한 관리 데이터 변경 이력. MVP 핵심 기능 이후 도입을 검토한다.
