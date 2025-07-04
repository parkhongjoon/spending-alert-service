# 💳 신용카드 소비 분석 및 한도 초과 알림 서비스

---

## 1. 프로젝트 개요

- 사용자의 신용카드 사용 데이터를 등록하고 관리할 수 있는 백엔드 서비스입니다.
- 월별·주간·카테고리별 소비 패턴을 분석하여 리포트를 제공합니다.
- 사용자가 설정한 한도/카테고리별 예산 대비 지출 상황을 모니터링합니다.
- 한도 80% 이상 사용 시 스케줄러로 예상 초과 알림을 발송합니다.
- 실제 한도 초과 시 거래 등록과 동시에 실시간으로 알림을 발송합니다.
- 알림은 Mailgun(이메일) 또는 SMS(Mock) 으로 전송됩니다.
- 모든 데이터는 REST API + Swagger UI 로 테스트할 수 있습니다.

---

## 2. 핵심 기능

| 번호 | 기능 | 설명 |
|------|------|------|
| 1 | 회원가입/로그인 | Spring Security + JWT 토큰 인증 |
| 2 | 거래 내역 등록/조회 | 거래일자, 사용처, 금액, 카테고리 저장 / 월별 TOP5 지출 제공 |
| 3 | 소비 패턴 분석 | 주간 일자별 추이, 월간 주별 평균, 동일 집단 비교 |
| 4 | 카테고리별 소비 목표 | 사용자가 카테고리별 목표 금액 설정 |
| 5 | 사용자별 한도 설정 | User.limit_amount 컬럼으로 관리 |
| 6 | 한도 초과 알림 | 80% 이상 사용 시 배치 알림, 초과 시 실시간 알림 |
| 7 | Notification 기록 관리 | 발송 이력 저장 (성공/실패) |
| 8 | Swagger 기반 REST API | 모든 API 문서화 및 테스트 |

---

## 3. 서비스 동작 시나리오

1) 회원가입 / JWT 인증  
2) 거래 내역 등록 및 조회  
3) 스케줄러로 예상 초과 알림 처리  
4) 거래 등록 시 실시간 초과 여부 즉시 확인  
5) 초과 시 실시간 알림 발송  
6) Notification 기록 저장 (발송 성공/실패)  
7) 리포트를 통해 주간/월간 소비 트렌드 확인

## 4. 주요 설계 포인트

### 4-1 지출 TOP5
- 월간 단위에서 카테고리별 지출 상위 5개 항목 제공

### 4-2 주간/월간 소비 트렌드
- 주간: 일자별 금액 추이
- 월간: 주별 평균 소비
- 동일 연령/소득 집단과 비교

### 4-3 패턴 변화(급등) 알림
- 하루 단위로 처리
- 전일 대비 급등 여부를 스케줄러/배치로 집계
- 급등 시 자동 알림 발송

### 4-4 알림 처리 흐름
- 예상 초과: 스케줄러/배치 처리 (한도 80% 이상 사용 예상 시)
- 실제 초과: 거래 등록 시 즉시 비교 → 실시간 알림 발송
- 실시간 알림은 Mailgun(이메일)/SMS(Mock) 외부 API 호출로 비동기 처리

---

## 5. 알림 처리 구조 요약

| 구분 | 내용 | 처리 방식 |
|------|------|-----------|
| 예상 초과 알림 | 한도 80% 이상 사용 예상 | Scheduler/Batch |
| 지출 급등 알림 | 하루 단위로 전일 대비 급등 여부 확인 | 배치 처리 |
| 실시간 초과 알림 | 거래 등록 시 한도 초과 즉시 알림 | 비동기 이벤트 처리 |
| 발송 채널 | Mailgun(이메일) / SMS(Mock) | 외부 API 호출 |

---

## 6. ERD

| 테이블 | 주요 컬럼 | 설명 |
|--------|------------|------|
| User | id, email, password, limit_amount, role, created_at, updated_at | 사용자 정보 및 한도 설정 |
| Transaction | id, user_id, transaction_date, merchant, amount, category, created_at | 거래 내역 데이터 |
| Budget | id, user_id, category, amount, period_type, created_at | 카테고리별 소비 목표 |
| Notification | id, user_id, message, sent_at, channel, status | 알림 발송 이력 |
| PatternReport | id, user_id, period_start, period_end, compare_group, total_spent, category_summary | 소비 패턴 리포트 |
| AlertRule | id, user_id, condition_type, threshold_value, channel | 알림 조건 규칙 |

![ERD](./docs/erd.png)

---

## 7. Trouble Shooting

- Mailgun 연동: 외부 SMTP 발송 실패 시 재시도 로직 적용
- 스케줄러/배치 처리 시점: 새벽 시간대 정기 실행, 지출 급등/한도 초과 여부 집계
- 실시간 알림 안정성: Mailgun/SMS 외부 API 비동기 처리로 실시간성 보장
- JWT 인증 문제: 만료 및 재발급 시도 처리

---

## 8. Tech Stack

- **Backend:** Java 17+, Spring Boot 3.x, Spring Security, JWT
- **DB:** MySQL/MariaDB, H2(Test)
- **ORM:** Spring Data JPA, Hibernate
- **Batch/Scheduler:** Spring Scheduler/Batch
- **Notification:** Mailgun(이메일), SMS(Mock)
- **문서화:** Swagger UI
- **Test:** JUnit5, Testcontainers
- **배포:** Docker, AWS EC2+RDS, GitHub Actions

---

## 9. 진행 순서

1. 자유 주제 선정  
2. 프로젝트 셋업  
3. README.md 작성 및 리뷰 요청  
4. 기능 단위 PR → 코드 리뷰 → Slack Thread 공유  
5. 스케줄러/배치/실시간 알림까지 단계별 구현  
6. Docker & AWS 배포 (필요시 구현)

