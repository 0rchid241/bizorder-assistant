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

## Backend 로컬 개발환경 (Issue #2)

JDK **21**, 실행 중인 Docker Engine(Windows/macOS는 Docker Desktop), Docker Compose v2가 필요합니다.
Spring Boot **4.1.1**, Gradle Wrapper **9.7.1**을 사용하며 Gradle을 별도 설치할 필요는 없습니다.
최초 실행에는 Gradle 의존성과 PostgreSQL 이미지 다운로드를 위한 인터넷 연결이 필요합니다.
Backend는 호스트에서 실행하고 PostgreSQL만 Docker에서 실행합니다.

### 1. 환경변수 설정

저장소 루트에서 `.env.example`을 `.env`로 복사합니다. 기존 `.env`가 있으면 덮어쓰지 않습니다.

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

macOS / Linux:

```bash
cp .env.example .env
```

`.env`의 빈 `POSTGRES_PASSWORD`에 직접 정한 로컬 개발 전용 비밀번호를 입력합니다.
예시에는 실제 비밀번호가 없고, 비밀번호가 비어 있으면 Compose가 실행을 거부합니다.
아래 로딩 명령을 두 OS에서 동일하게 사용하려면 따옴표·공백 없는 `KEY=value` 형식을 유지하고
비밀번호는 영문/숫자 조합으로 설정합니다. `.env`는 Git에서 제외되며 커밋하지 않습니다.

| 변수 | 용도 |
| --- | --- |
| `POSTGRES_DB` | 초기 생성 DB 이름 |
| `POSTGRES_USER` | 초기 생성 사용자 및 Backend 접속 사용자 |
| `POSTGRES_PASSWORD` | Docker와 Backend가 사용하는 로컬 비밀번호 |
| `POSTGRES_PORT` | 호스트 포트 (기본 예시: 5432) |
| `DB_URL` | 호스트 Backend용 JDBC URL |

DB 이름이나 호스트 포트를 변경하면 `DB_URL`도 함께 수정합니다.
Compose는 루트 `.env`를 읽지만, 호스트에서 실행하는 Spring Boot는 자동으로 읽지 않습니다.
**Backend/테스트를 실행할 각 터미널에서** 저장소 루트를 기준으로 다음 명령을 실행합니다.

Windows PowerShell:

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([A-Z_]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
    }
}
```

macOS / Linux (직접 작성한 로컬 `.env`만 로드):

```bash
set -a
. ./.env
set +a
```

이 변수는 현재 터미널과 그 자식 프로세스에 적용됩니다. `.env` 변경 후에는 다시 로드합니다.
Compose에서도 이미 설정된 셸 환경변수가 `.env`보다 우선하므로 값이 서로 다르지 않도록 합니다.

### 2. PostgreSQL Docker 실행

저장소 루트에서 두 OS 공통:

```bash
docker compose config --quiet
docker compose up -d
docker compose ps
```

`config --quiet`는 비밀번호가 포함된 렌더링 결과를 출력하지 않고 구성을 검사합니다.
`postgres`가 `healthy` 상태가 될 때까지 기다립니다. 필요하면 `docker compose logs postgres`로 확인합니다.
대기까지 자동으로 하려면 `docker compose up -d --wait --wait-timeout 90`을 사용할 수 있습니다.

공식 `postgres:18` 이미지로 major 버전을 고정합니다.
[공식 이미지의 PostgreSQL 18 볼륨 경로](https://hub.docker.com/_/postgres)에 맞춰
named volume `postgres-data`를 `/var/lib/postgresql`에 연결합니다.
호스트 포트는 로컬 개발용으로 `127.0.0.1`에만 노출합니다.
`pg_isready` healthcheck는 서버가 접속을 받을 준비가 되었는지 검사하며,
실제 Backend 자격증명과 JDBC 연결은 아래 테스트에서 별도로 확인합니다.

### 3. Backend 실행

1단계에서 환경변수를 로드한 터미널에서 실행합니다.

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

기본 HTTP 포트는 `8080`입니다. `application.yml`은 `DB_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`를 읽습니다.
Issue #1의 datasource 자동 설정 제외를 제거했으므로 **실행과 통합 테스트 모두 PostgreSQL이 필요**합니다.
Hibernate `ddl-auto: none`으로 테이블을 자동 생성·변경하지 않으며, 이번 단계에는 도메인 테이블이 없습니다.
기본 사용자 생성 제외와 GET `/api/health`만 허용하는 임시 보안 정책은 Issue #12까지 유지합니다.

### 4. DB 연결 확인

별도 터미널에서 1단계 환경변수를 로드한 후 `backend/`에서 실행합니다.

Windows PowerShell:

```powershell
.\gradlew.bat test --tests '*BizOrderAssistantApplicationTests.databaseConnectionIsValid' --rerun-tasks
```

macOS / Linux:

```bash
./gradlew test --tests '*BizOrderAssistantApplicationTests.databaseConnectionIsValid' --rerun-tasks
```

Spring이 생성한 실제 `DataSource`로 PostgreSQL에 연결해 DB 제품명과 `SELECT 1` 결과를 검증합니다.
H2나 DB mock은 사용하지 않습니다. 실패하면 Docker 상태, 호스트 포트, 환경변수 및 기존 볼륨의 자격증명을 확인합니다.

### 5. Health API 확인

3단계 서버가 실행 중인 상태에서 별도 터미널로 확인합니다.

Windows PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

macOS / Linux:

```bash
curl -i http://localhost:8080/api/health
```

정상 응답은 HTTP `200 OK`, JSON `{"status":"UP"}`입니다.
이 API는 HTTP 응답 확인용이며 DB 상태를 검사하지 않습니다. DB 연결은 4단계 테스트로 확인합니다.

### 6. 전체 테스트 실행

환경변수를 로드한 터미널의 `backend/`에서 실행합니다. PostgreSQL은 계속 실행 중이어야 하며
테스트는 별도 임의 HTTP 포트로 서버를 시작하므로 `bootRun`과 병행할 수 있습니다.

Windows PowerShell:

```powershell
.\gradlew.bat test --rerun-tasks
```

macOS / Linux:

```bash
./gradlew test --rerun-tasks
```

Context 로딩, health HTTP 200 및 `status: UP`, 다른 경로 HTTP 403, 실제 DB 연결의 4개 테스트를 실행합니다.
`--rerun-tasks`는 이전 성공 결과를 재사용하지 않고 현재 DB를 다시 검증합니다.
결과는 `backend/build/reports/tests/test/index.html`에서 확인합니다.
Jar 생성은 `./gradlew bootJar` (Windows: `.\gradlew.bat bootJar`)를 사용합니다.

### 7. 종료 및 데이터 유지

Backend 터미널에서 `Ctrl+C`로 종료한 뒤 저장소 루트에서 실행합니다.

```bash
docker compose down
```

named volume은 유지되므로 재시작하거나 `down` 후 `up -d`해도 DB 데이터가 남습니다.
`docker compose down -v`는 데이터를 삭제하므로 일반 종료에 사용하지 않습니다.
`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`는 **빈 볼륨을 최초 초기화할 때만** 적용됩니다.
기존 볼륨이 있는 상태에서 `.env` 값만 변경해도 DB 사용자/비밀번호는 바뀌지 않습니다.
기존 값을 유지하거나 DB에서 명시적으로 변경해야 하며, 연결 오류 해결을 위해 볼륨을 임의로 삭제하지 않습니다.

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
