# Development Workflow

## 기본 흐름
```text
Issue
 ↓
Branch
 ↓
Implementation + Test
 ↓
Pull Request
 ↓
Review
 ↓
Squash Merge
```

## 브랜치
- `feature/<issue-number>-<short-name>`
- `fix/<issue-number>-<short-name>`
- `docs/<issue-number>-<short-name>`
- `refactor/<issue-number>-<short-name>`

## 커밋
Conventional Commit을 기본으로 한다.

예:
```text
feat: add product domain
fix: prevent expired discount application
test: add quote calculator edge cases
docs: update pricing policy design
refactor: separate discount evaluation logic
chore: configure postgres development environment
```

## Pull Request
PR에는 무엇을 구현했는지, 왜 필요한지, 주요 설계 결정, 테스트 방법, 관련 Issue를 기록한다. 한 PR에 너무 많은 기능을 묶지 않는다.

## Merge
기본적으로 Squash Merge를 사용한다.

## 테스트 우선순위
- 유효 가격정책 선택
- 할인 적용조건
- 할인 중복/충돌
- 정책 유효기간
- 견적 확정 snapshot

## 문서 동기화
도메인 변경은 `docs/DOMAIN.md`, 범위 변경은 `docs/PROJECT_PLAN.md`, 실행 방법 변경은 `README.md`에 반영한다.
