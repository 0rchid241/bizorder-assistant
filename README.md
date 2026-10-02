# BizOrder Assistant

> **가격·약정·할인 정책을 데이터로 관리하고, 특정 시점의 견적을 결정론적으로 계산하며, 그 계산 근거와 확정 당시 결과를 보존하는 Policy-based Quote Engine**

## 프로젝트 한 줄 설명

BizOrder Assistant는 가격과 할인 규칙이 자주 바뀌는 B2B 상품 환경을 가정한 포트폴리오 프로젝트입니다.

핵심은 관리 화면을 많이 만드는 것이 아니라, **“이 조건에서 왜 이 가격이 나왔는가?”를 코드와 데이터로 설명할 수 있는 견적 엔진**을 구현하는 것입니다.

특정 회사의 실제 내부 시스템을 복제하거나 대체하는 프로젝트가 아닙니다. 실제 고객 개인정보, 비공개 요금표, 사내 시스템 연동 없이 가상 상품과 가상 정책으로만 동작합니다.

## 이 프로젝트가 해결하려는 문제

상품 가격이 단순한 고정값이라면 CRUD만으로도 충분합니다. 하지만 실제 업무에서는 다음과 같은 규칙이 동시에 존재할 수 있습니다.

- 같은 상품도 12개월 / 24개월 / 36개월 약정에 따라 가격이 다르다.
- 가격 정책은 적용 시작일과 종료일을 가진다.
- 동일 상품에 과거·현재·미래 정책이 함께 존재할 수 있다.
- 정액 할인과 정률 할인이 섞여 있다.
- 일부 할인은 동시에 적용할 수 없다.
- 여러 할인 중 우선순위가 높은 규칙을 선택해야 할 수 있다.
- 정책이 바뀌더라도 이미 확정된 과거 견적은 변하면 안 된다.

이 프로젝트는 이런 규칙을 `if/else`와 상수에 흩뿌리지 않고 **Product → PricingPolicy → DiscountRule → Quote → Snapshot**으로 모델링합니다.

## 완성했을 때 보여줄 장면

```text
[견적 입력]
견적일        2026-10-02
상품          Biz Internet 500M
수량          2
약정기간      36개월
추가 조건     인터넷+전화 결합 / 신규가입

              ↓

[정책 선택]
2026-10-02에 유효하고 36개월 약정에 맞는
PricingPolicy 하나를 결정론적으로 선택

              ↓

[할인 평가]
장기약정 할인      적용 가능
결합 할인          적용 가능
신규가입 할인      적용 가능

결합 할인 ↔ 신규가입 할인 충돌
→ 우선순위 규칙에 따라 결합 할인 선택

              ↓

[견적 결과]
기본 월 요금       64,000원
장기약정 할인      -3,200원
결합 할인          -5,000원
최종 월 요금       55,800원

왜 이 가격인가?
- 2026 하반기 가격 정책 적용
- 36개월 조건 충족
- 결합 조건 충족
- 신규가입 할인은 중복 불가 규칙으로 제외

              ↓

[견적 확정]
적용 상품 / 가격 / 할인 / 최종금액을 Snapshot으로 보존

이후 관리자가 정책을 바꿔도
2026-10-02 확정 견적 결과는 그대로 유지
```

## 핵심 가치

### 1. 정책을 코드와 분리
가격과 할인 금액을 서비스 코드에 하드코딩하지 않고 데이터로 관리합니다.

### 2. 결정론적 계산
같은 상품·시점·약정·조건이라면 같은 정책이 선택되고 같은 견적 결과가 나와야 합니다.

### 3. 계산 근거 설명
최종 금액만 반환하지 않고 어떤 가격정책과 할인규칙이 선택·제외되었는지 설명할 수 있는 구조를 지향합니다.

### 4. 과거 견적 불변성
확정된 견적은 이후 정책 변경과 분리하여 Snapshot으로 보존합니다.

### 5. 실제 DB 기반 검증
핵심 흐름은 PostgreSQL과 실제 HTTP 요청을 포함해 검증합니다.

## MVP 핵심 범위

- Product 관리
- PricingPolicy 관리
- 견적 시점 + 약정기간에 맞는 가격정책 선택
- Quote Engine v1
- 최소 React 견적 화면
- DiscountRule 및 할인 충돌 처리
- 계산 근거 표시
- Quote 확정 및 Snapshot
- 최소 인증/권한
- Docker / CI / 포트폴리오 데모

## 후순위 / Stretch

- 가입 접수 Checklist
- 설치비 등 부가 요금 모델
- 정책 변경 AuditLog
- 복잡한 검색·대시보드
- PDF 견적서

## 명시적으로 하지 않는 것

- 실제 통신사의 사내 시스템 또는 BSS/CRM 복제
- 실제 가입/개통 처리
- 실제 고객 개인정보 저장
- 실제 비공개 상품 코드·요금·할인정책 사용
- AI가 가격 또는 할인 적용 여부를 판단하는 기능
- 실제 결제·정산

## 기술 스택

### Backend
- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Bean Validation
- Flyway
- JUnit 5

### Database
- PostgreSQL 18

### Frontend
- React
- TypeScript

### Infrastructure
- Docker / Docker Compose
- GitHub Actions

### Documentation
- OpenAPI / Swagger
- ERD / Architecture 문서

## 프로젝트 구조

```text
bizorder-assistant/
├─ backend/
├─ frontend/                 # Quote Engine v1 이후 조기 연결 예정
├─ docs/
│  ├─ PROJECT_PLAN.md
│  ├─ ROADMAP.md
│  ├─ DOMAIN.md
│  └─ DEVELOPMENT_WORKFLOW.md
├─ .github/
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
Flyway가 `backend/src/main/resources/db/migration/V1__create_products.sql`로 Product 테이블을 생성합니다.
Hibernate는 `ddl-auto: validate`로 매핑을 검증하며 스키마를 자동 변경하지 않습니다.
이미 적용된 migration은 수정하지 않고 후속 변경 시 새 migration을 추가합니다.
기본 사용자 생성은 계속 제외합니다. health, Product의 GET/POST/PUT, API 문서 GET만 임시 공개하며
Product 요청에만 CSRF 검사를 제외합니다. Issue #12에서 STAFF / ADMIN 인증·권한 정책으로 교체합니다.

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

기존 Context/health/경로 접근제어/DB 연결 테스트와 Product 서비스 단위·실제 HTTP/PostgreSQL 통합 테스트를 실행합니다.
Product API 테스트는 테스트별 고유 코드로 데이터를 생성하고 해당 데이터만 삭제합니다.
HTTP 요청은 서버의 별도 트랜잭션이므로 테스트 스레드의 rollback에 의존하지 않습니다.
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

## Product API (Issue #3)

위 절차로 PostgreSQL과 Backend를 실행하면 아래 API를 사용할 수 있습니다.

| Method | 경로 | 정상 응답 |
| --- | --- | --- |
| POST | `/api/products` | 201, Product 응답 및 Location 헤더 |
| GET | `/api/products` | 200, id 오름차순 배열 (비활성 포함) |
| GET | `/api/products/{id}` | 200, Product 응답 |
| PUT | `/api/products/{id}` | 200, 변경된 Product 응답 |

목록은 현재 MVP 규모에서 전체 조회하며 검색/페이지네이션은 아직 추가하지 않습니다.
PUT은 수정 가능한 필드 전체를 교체하므로 name/category/active를 모두 보냅니다.
description은 생략/null로 지울 수 있습니다. code 변경과 DELETE는 제공하지 않습니다.
code 정규화·길이·카테고리·활성 정책은 [Product 도메인 정의](docs/DOMAIN.md#product)를 참고합니다.

등록 예시 (Swagger UI의 Try it out에서도 사용 가능):

```json
{
  "code": "DEMO_INTERNET_500M",
  "name": "가상 인터넷 500M",
  "category": "INTERNET",
  "description": "프로젝트 시연용 가상 상품"
}
```

수정/비활성화 예시:

```json
{
  "name": "가상 인터넷 500M (판매 중단)",
  "category": "INTERNET",
  "description": null,
  "active": false
}
```

입력 오류(알 수 없는 필드/category 포함)는 400 `INVALID_REQUEST`, 없는 상품은 404 `PRODUCT_NOT_FOUND`,
중복 코드는 409 `DUPLICATE_PRODUCT_CODE`이며 오류 응답은 `{ "code": "...", "message": "..." }`입니다.
상품은 기본 active=true로 생성되며 생성 요청에 active를 지정할 수 없습니다.

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

[springdoc 공식 문서](https://springdoc.org/)의 Spring Boot 4 호환 3.x 계열인 3.1.1을 사용합니다.
컨트롤러와 요청/응답 record에서 문서를 생성합니다.
Flyway는 새 DB와 기존 개발 볼륨에 같은 Product 스키마를 적용하기 위해 최소 도입했습니다.

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

상세 마일스톤과 가중치 기반 진행률은 [마스터 로드맵](docs/ROADMAP.md)을 단일 기준으로 사용합니다.

현재는 **M2 Product & Pricing Policy** 단계이며 Product 구현이 완료되었습니다. 다음 핵심 작업은 PricingPolicy와 유효 가격 선택입니다. 그 다음 Quote Engine v1을 만든 뒤, 할인 엔진보다 먼저 최소 Frontend를 연결해 실제 견적 흐름을 눈으로 확인할 수 있게 합니다.

## 문서

- [프로젝트 기획서](docs/PROJECT_PLAN.md)
- [마스터 로드맵](docs/ROADMAP.md)
- [도메인 용어집](docs/DOMAIN.md)
- [개발 워크플로](docs/DEVELOPMENT_WORKFLOW.md)

## 현재 상태

**Core backend foundation complete → Policy-based quote engine in progress**

현재 진행률은 Roadmap 기준 **21%**입니다. Spring Boot/PostgreSQL 개발환경과 Product 도메인은 완료되었고, 이제 가격정책과 견적 계산이라는 프로젝트의 핵심 문제를 구현하는 단계입니다.

---

This repository is a personal portfolio project. It does not reproduce or integrate with any specific company's private systems or non-public pricing data.