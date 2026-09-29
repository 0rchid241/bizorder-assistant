# BizOrder Assistant

> B2B 통신 상품의 **가격·할인 정책 기반 견적 생성**과 **가입 접수 전 준비 업무**를 지원하는 직원용 웹 시스템

## 프로젝트 소개

BizOrder Assistant는 B2B 통신 상품을 상담하는 직원이 고객의 요구조건을 바탕으로 상품을 구성하고,
현재 유효한 가격·할인 정책에 따라 견적을 계산한 뒤 공식 가입 접수 전에 필요한 항목을 확인할 수 있도록 돕는 포트폴리오 프로젝트입니다.

특정 기업의 실제 내부 시스템을 복제하거나 대체하는 것이 목적이 아닙니다.
통신 B2B 업무 도메인을 분석하고, 현실의 비즈니스 규칙을 소프트웨어로 모델링하는 과정을 보여주는 것을 목표로 합니다.

## 핵심 문제

통신상품 견적은 단순한 가격 합산이 아닙니다.

- 약정 기간에 따라 가격이 달라질 수 있습니다.
- 상품 조합에 따라 적용 가능한 할인이 달라질 수 있습니다.
- 일부 할인은 중복 적용이 불가능할 수 있습니다.
- 프로모션은 유효기간이 존재할 수 있습니다.
- 정책이 변경되어도 과거 확정 견적은 그대로 보존되어야 합니다.

BizOrder Assistant는 이러한 규칙을 코드에 무분별하게 하드코딩하지 않고,
**상품·가격·할인 정책을 도메인으로 모델링한 견적 엔진**을 중심으로 구현합니다.

## 핵심 흐름

```text
관리자
 └─ 상품 / 가격 / 할인 정책 관리

직원
 └─ 견적 조건 입력
       ↓
    상품 선택
       ↓
    정책 기반 견적 계산
       ↓
    접수 준비 체크리스트
       ↓
    견적 확정
```

## MVP

### 상품 관리
- 상품 등록 / 조회 / 수정
- 상품 활성·비활성 관리
- 상품 카테고리 관리

### 가격 정책
- 상품별 월 이용료
- 약정 기간별 가격 정책
- 정책 유효기간 관리

### 할인 정책
- 조건 기반 할인
- 적용 기간
- 중복 가능 여부
- 정책 우선순위

### 견적 엔진
- 견적 시점에 유효한 가격 정책 조회
- 적용 가능한 할인 계산
- 할인 충돌 검증
- 최종 예상 요금 계산

### 견적 확정 및 이력 보존
- 견적 생성 / 조회
- 확정 시 가격 및 정책 snapshot 보존
- 이후 정책이 변경되어도 과거 견적 재현

### 가입 접수 준비 체크리스트
- 상품별 필수 확인사항 제공
- 누락 항목 표시
- 접수 준비 상태 확인

## 범위에서 제외하는 것

초기 버전에서는 다음 기능을 구현하지 않습니다.

- 실제 SK브로드밴드/SWING 시스템 연동
- 실제 가입 신청 및 개통 처리
- 실제 고객 개인정보 저장
- 주민등록번호·신분증·계좌정보 저장
- 실제 통신사 내부 요금 및 비공개 정책 복제
- 고객 CRM
- AI 추천
- 통계 대시보드

시연에 사용하는 상품 및 요금 데이터는 모두 프로젝트용 가상 데이터입니다.

## 설계 원칙

### 개인정보 최소화
공식 고객관리 시스템을 대체하지 않습니다.
업무지원에 불필요한 개인정보는 저장하지 않고, 견적 조건과 상품 구성 정보를 중심으로 다룹니다.

### 정책과 코드 분리
상품 가격이나 할인 금액을 서비스 로직에 직접 하드코딩하지 않고 데이터 기반 정책으로 관리하는 구조를 지향합니다.

### 과거 견적 재현
정책 변경이 과거 확정 견적에 영향을 주지 않도록 견적 확정 시점의 가격·할인 결과를 보존합니다.

### 테스트 가능한 비즈니스 로직
견적 계산과 할인 규칙은 UI/API와 최대한 분리하여 단위 테스트가 가능한 구조로 구현합니다.

## 기술 스택

### Backend
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- JUnit 5

### Database
- PostgreSQL

### Frontend
- React
- TypeScript

### Infrastructure
- Docker / Docker Compose
- GitHub Actions

### Documentation
- OpenAPI / Swagger
- ERD
- Architecture Diagram

> 기술 스택은 개발 과정에서 실제 필요에 따라 조정될 수 있습니다.

## 예정 프로젝트 구조

```text
bizorder-assistant/
├─ backend/
├─ frontend/
├─ docs/
│  ├─ PROJECT_PLAN.md
│  ├─ ROADMAP.md
│  ├─ DOMAIN.md
│  └─ DEVELOPMENT_WORKFLOW.md
├─ .github/
│  ├─ ISSUE_TEMPLATE/
│  └─ pull_request_template.md
├─ docker-compose.yml
└─ README.md
```

## Backend 로컬 실행 (Issue #1)

JDK **21**이 필요하며 `java -version`으로 확인합니다. Spring Boot **4.1.1**, Gradle Wrapper **9.7.1**을 사용합니다.
Gradle을 별도로 설치할 필요는 없으며 최초 실행 시 Gradle 및 의존성 다운로드를 위한 인터넷 연결이 필요합니다.

Windows PowerShell:

```powershell
cd backend
.\gradlew.bat bootRun
```

macOS / Linux:

```bash
cd backend
./gradlew bootRun
```

기본 포트는 `8080`입니다. 서버 실행 후 별도 터미널에서 확인합니다.

```bash
curl http://localhost:8080/api/health
```

PowerShell에서는 `Invoke-RestMethod http://localhost:8080/api/health`를 사용할 수 있습니다.
정상 응답은 HTTP `200 OK`, JSON `{"status":"UP"}`입니다. 서버는 `Ctrl+C`로 종료합니다.

테스트 실행 (`backend/`에서):

```powershell
.\gradlew.bat test
```

macOS / Linux에서는 `./gradlew test`를 실행합니다. ApplicationContext 로딩과 실제 HTTP health 응답을 검증합니다.
실행 가능한 Jar는 `./gradlew bootJar` (Windows: `.\gradlew.bat bootJar`)로 생성합니다.

현재는 **DB 없이 실행하는 초기 기반**입니다. JPA와 PostgreSQL 드라이버는 의존성만 포함하며,
`application.yml`에서 datasource 자동 설정을 임시 제외합니다. Issue #2에서 DB 연결을 구성할 때 이 제외를 제거합니다.
기본 사용자 자동 생성도 제외하며, 임시 보안 정책은 **GET `/api/health`만 허용하고 나머지 요청을 차단**합니다.
실제 인증·권한 정책은 후속 Issue에서 구현합니다. 이 health 응답은 프로세스의 HTTP 응답 확인용이며 DB 연결 상태를 검사하지 않습니다.

## 개발 방식

```text
Issue
  ↓
feature / fix branch
  ↓
구현 + 테스트
  ↓
Pull Request
  ↓
검토
  ↓
Squash Merge
```

작은 기능이라도 가능한 한 Issue와 PR 단위로 개발 이유와 변경 내용을 남깁니다.

## 개발 로드맵

상세 마일스톤, 완료 조건, 가중치 기반 진행률은 [마스터 로드맵](docs/ROADMAP.md)을 단일 기준으로 사용합니다.

현재 단계는 **M0 기획·도메인·개발 규칙 완료 → M1 Backend·Database 개발 기반 시작**입니다.

## 문서

- [프로젝트 기획서](docs/PROJECT_PLAN.md)
- [마스터 로드맵](docs/ROADMAP.md)
- [도메인 용어집](docs/DOMAIN.md)
- [개발 워크플로](docs/DEVELOPMENT_WORKFLOW.md)

## 현재 상태

**Planning → Initial Development**

현재는 핵심 도메인과 MVP 범위를 확정하고 백엔드 초기 개발을 시작하는 단계입니다.

---

This repository is a personal portfolio project and is not an official product of SK Broadband or any affiliated company.
