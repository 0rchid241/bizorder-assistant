# Master Roadmap — BizOrder Assistant

> 목표: **정책 데이터 → 결정론적 가격 선택 → 견적 계산 → 계산 근거 → 할인 충돌 처리 → Snapshot 보존**을 브라우저에서 끝까지 시연 가능한 포트폴리오 시스템으로 완성한다.

이 문서는 BizOrder Assistant의 **진행률 기준(Source of Truth)** 이다.  
프로젝트 목적과 범위는 `PROJECT_PLAN.md`, 도메인은 `DOMAIN.md`, 개발 절차는 `DEVELOPMENT_WORKFLOW.md`를 따른다.

## 진행률 계산 규칙

- 전체 진행률은 아래 완료 항목의 가중치 합계 100%로 계산한다.
- `[x]`는 기본 브랜치에서 구현·테스트·문서 근거가 확인된 경우만 표시한다.
- 열린 Issue, 작업 브랜치, 미병합 PR은 완료로 계산하지 않는다.
- 이미 완료된 기반 작업은 프로젝트 재정의 후에도 그대로 인정한다.
- 실제 고객 개인정보와 실제 회사의 비공개 상품·요금 데이터는 사용하지 않는다.

---

## M0. 문제 정의·개발 규칙 — 8%

- [x] **2%** 정책 기반 견적 엔진이라는 핵심 문제와 MVP 범위 정의
- [x] **2%** Product / PricingPolicy / DiscountRule / Quote / Snapshot 핵심 도메인 정의
- [x] **2%** Issue → Branch → PR → Squash Merge 개발 워크플로 구성
- [x] **2%** 초기 Backlog와 진행률 관리 기준 구성

**완료 기준:** 저장소 문서만으로 프로젝트가 특정 회사 시스템 복제가 아니라 정책 기반 견적 문제를 다루는 포트폴리오임을 설명할 수 있다.

---

## M1. Backend·Database 기반 — 8%

관련 Issue: **#1, #2**

- [x] **4%** Spring Boot 기본 구조, `/api/health`, 기본 보안정책, 테스트
- [x] **4%** PostgreSQL Docker Compose, datasource, secret 분리, 실제 DB 연결 검증

**완료 기준:** 새 개발환경에서 Backend와 PostgreSQL을 재현하고 기본 테스트를 통과할 수 있다.

---

## M2. Product & Pricing Policy — 15%

관련 Issue: **#3, #4, #5**

- [x] **5%** Product 도메인 및 등록/조회/수정 API, 활성 상태와 입력 검증
- [ ] **5%** PricingPolicy 도메인, 약정기간·월 이용료·유효기간 모델링
- [ ] **3%** 견적일·약정조건에 맞는 유효 가격정책 선택 로직
- [ ] **2%** Product / PricingPolicy 핵심 Service·API 테스트

**완료 기준:** 한 Product에 여러 과거·현재·미래 가격정책을 저장하고 특정 날짜와 약정조건에서 사용할 정책을 결정론적으로 선택할 수 있다.

---

## M3. Quote Engine v1 — 18%

관련 Issue: **#6, #7**

- [ ] **4%** QuoteRequest / QuoteResult / QuoteItem 입력·출력 계약
- [ ] **5%** Product·수량·약정·견적일 기반 기본 금액 계산
- [ ] **3%** 비활성 상품·가격정책 미존재·잘못된 수량/조건 실패 정책
- [ ] **2%** 계산 결과에 선택된 PricingPolicy와 근거 정보 포함
- [ ] **4%** QuoteCalculator 단위 테스트와 날짜/금액 경계값 검증

**완료 기준:** 할인 없이도 실제 입력 → 정책 선택 → 기본 견적 결과와 계산 근거를 API로 반환할 수 있다.

---

## M4. Early Frontend Vertical Slice — 10%

> 기존 계획보다 Frontend를 앞당긴다. 엔진이 동작하는 모습을 일찍 눈으로 확인하기 위한 마일스톤이다.

- [ ] **3%** React + TypeScript 기본 구조와 Backend 연결
- [ ] **3%** Product / PricingPolicy 최소 관리 화면
- [ ] **3%** 견적 입력 → 계산 결과 → 적용 가격정책 표시 화면
- [ ] **1%** 브라우저 기준 Product → Policy → Quote 흐름 검증

**완료 기준:** 브라우저에서 상품·약정·견적일을 입력하고 Backend가 선택한 가격정책과 기본 견적 결과를 직접 확인할 수 있다.

---

## M5. Discount Engine — 15%

관련 Issue: **#8, #9**

- [ ] **4%** DiscountRule 도메인: 정액/정률, 조건, 유효기간, 우선순위, 중복 가능 여부
- [ ] **4%** 적용 가능한 할인 판별 및 할인 금액 계산
- [ ] **3%** 중복 불가 할인 충돌과 우선순위 해결
- [ ] **2%** 적용/제외 할인과 이유를 Quote 결과에 포함
- [ ] **2%** 만료·조건불충족·음수금액 등 핵심 테스트

**완료 기준:** 동일 입력에 대해 어떤 할인이 적용·제외됐는지 설명 가능한 최종 견적을 계산한다.

---

## M6. Quote 확정 & Snapshot — 12%

관련 Issue: **#10**

- [ ] **3%** 견적 DRAFT 저장·조회
- [ ] **4%** CONFIRMED 전환 시 상품·가격정책·할인·금액 Snapshot 보존
- [ ] **3%** 이후 Product/PricingPolicy/DiscountRule 변경에도 확정 결과 불변
- [ ] **2%** 확정 견적 조회 화면과 불변성 통합 테스트

**완료 기준:** “현재 정책”과 “당시 확정 결과”가 명확히 분리되며 과거 견적을 재현할 수 있다.

---

## M7. 인증·품질·CI·실행환경 — 8%

관련 Issue: **#12, #14**

- [ ] **3%** 최소 STAFF / ADMIN 인증·인가와 권한 테스트
- [ ] **2%** GitHub Actions Backend/Frontend test·build
- [ ] **2%** Docker 기반 재현 가능한 실행환경 정리
- [ ] **1%** 예외처리·Validation·로그·민감정보 최종 점검

**완료 기준:** 권한이 필요한 관리 기능이 구분되고 PR과 새 환경에서 핵심 빌드/테스트를 재현할 수 있다.

---

## M8. 포트폴리오 데모 완성 — 6%

- [ ] **1%** 가상 Product / PricingPolicy / DiscountRule Demo 데이터
- [ ] **1%** 계산 근거를 포함한 UI 정리
- [ ] **1%** OpenAPI / ERD / Architecture 최신화
- [ ] **1%** README에 문제 → 설계 → 핵심 로직 → 테스트 전략 정리
- [ ] **1%** 배포 또는 한 번에 재현 가능한 Demo 실행 방식
- [ ] **1%** 최종 시나리오 회귀 테스트와 포트폴리오 설명 자료

**완료 기준:** 저장소와 데모만으로 “정책을 어떻게 모델링했고 왜 이 견적이 나왔으며 왜 과거 결과가 보존되는지”를 설명하고 시연할 수 있다.

---

## Stretch — 진행률 외

핵심 엔진 완성 후 여유가 있을 때만 진행한다.

- 가입 접수 RequiredItem / QuoteChecklist
- 설치비·일회성 비용 모델
- AuditLog
- 복잡한 검색·필터
- PDF 견적서

기존 Issue #11 Checklist는 이 범주로 이동한다.

---

## 핵심 End-to-End 데모

```text
ADMIN
  └─ Product 생성
  └─ 12/24/36개월 PricingPolicy 등록
  └─ DiscountRule 등록
             ↓
STAFF
  └─ 견적일 / 상품 / 수량 / 약정 / 조건 입력
             ↓
      유효 가격정책 선택
             ↓
        할인 규칙 평가
             ↓
      충돌 및 우선순위 해결
             ↓
   최종 금액 + 계산 근거 표시
             ↓
          견적 확정
             ↓
        Snapshot 보존
             ↓
관리자가 정책 변경
             ↓
새 견적은 새 정책 / 과거 확정 견적은 기존 결과 유지
```

## 현재 기준 진행률

기본 브랜치 완료 근거 기준: **21%**

- M0: **8 / 8**
- M1: **8 / 8**
- M2: **5 / 15**
- M3~M8: **0**
- 완료: Issue #1~#3
- 다음 핵심 작업: Issue #4 PricingPolicy → Issue #5 유효 가격정책 선택

> 프로젝트 방향은 재정의했지만 완료된 Spring/PostgreSQL/Product 기반은 새 구조에서도 그대로 필요하므로 진행률 21%를 유지한다.

## 운영 원칙

- 핵심 엔진에 직접 기여하지 않는 기능은 쉽게 추가하지 않는다.
- Backend만 장기간 진행하지 않고 Quote Engine v1 직후 Frontend를 연결한다.
- 정책/계산 로직은 UI에서 분리해 단위 테스트 가능하게 유지한다.
- 실제 회사 업무를 추측해 기능을 추가하지 않는다.
- 새 기능은 “정책 기반 견적과 계산 근거를 더 잘 보여주는가?”를 기준으로 판단한다.
