# Payment Service

## 📋 개요
Payment Service는 PassionPay 플랫폼의 **핵심 결제 처리 서비스**입니다. QR 코드 기반 결제, 카드 결제, 결제 취소 등 모든 결제 관련 기능을 담당합니다.

## 🎯 주요 기능
- **결제 준비**: 거래 생성 및 QR 토큰 발급
- **결제 실행**: 카드 토큰을 통한 실제 결제 처리
- **결제 상태 조회**: 실시간 거래 상태 확인
- **결제 취소**: 거래 취소 및 환불 처리
- **중복 방지**: Idempotent 처리로 중복 결제 방지

## 🏗️ 기술 스택
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Data JPA**
- **PostgreSQL** (결제 데이터 저장)
- **Redis** (세션 및 캐시 관리)
- **SpringDoc OpenAPI** (API 문서)

## 🚀 시작하기

### 환경 설정
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/testdb
    username: testuser
    password: testpass
  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8081

logging:
  level:
    com.fastcampus.payment: DEBUG
```

### 빌드 및 실행
```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
# 또는
java -jar build/libs/payment-0.0.1-SNAPSHOT.jar
```

### 서비스 상태 확인
```bash
curl http://localhost:8081/actuator/health
```

## 📚 API 가이드

### 🔑 인증
Payment Service는 JWT 토큰 기반 인증을 사용합니다.
```bash
curl -X POST http://localhost:8081/api/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 💳 결제 플로우

#### 1️⃣ 결제 준비 (Payment Ready)
거래를 생성하고 QR 코드용 토큰을 발급합니다.

```bash
curl -X POST http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "amount": 15000,
    "merchantOrderId": "ORDER-20241215-001"
  }'
```

**응답 예시:**
```json
{
  "paymentToken": "payment_abc123def456",
  "status": "READY",
  "amount": 15000,
  "merchantId": 1,
  "merchantOrderId": "ORDER-20241215-001",
  "qrCode": "https://qr.passionpay.com/payment_abc123def456",
  "expiresAt": "2024-12-15T15:30:00"
}
```

#### 2️⃣ 결제 상태 조회 (Payment Progress)
QR 코드 스캔 후 결제 상태를 확인합니다.

```bash
curl -X GET http://localhost:8081/api/payments/payment_abc123def456
```

**응답 예시:**
```json
{
  "paymentToken": "payment_abc123def456",
  "status": "READY",
  "amount": 15000,
  "merchantId": 1,
  "merchantOrderId": "ORDER-20241215-001",
  "createdAt": "2024-12-15T14:30:00",
  "expiresAt": "2024-12-15T15:30:00"
}
```

#### 3️⃣ 결제 실행 (Payment Execution)
카드 정보로 실제 결제를 처리합니다.

```bash
curl -X PATCH http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "paymentToken": "payment_abc123def456",
    "cardToken": "card_xyz789ghi012"
  }'
```

**성공 응답:**
```json
{
  "paymentToken": "payment_abc123def456",
  "status": "COMPLETED",
  "amount": 15000,
  "merchantId": 1,
  "merchantOrderId": "ORDER-20241215-001",
  "cardToken": "card_xyz789ghi012",
  "approvalResult": true,
  "createdAt": "2024-12-15T14:32:00"
}
```

**실패 응답:**
```json
{
  "paymentToken": "payment_abc123def456",
  "status": "FAILED",
  "amount": 15000,
  "merchantId": 1,
  "merchantOrderId": "ORDER-20241215-001",
  "cardToken": "card_xyz789ghi012",
  "approvalResult": false,
  "errorMessage": "잔액 부족",
  "createdAt": "2024-12-15T14:32:00"
}
```

#### 4️⃣ 결제 취소 (Payment Cancel)
완료된 결제를 취소합니다.

```bash
curl -X DELETE http://localhost:8081/api/payments/payment_abc123def456
```

**응답 예시:**
```json
{
  "paymentToken": "payment_abc123def456",
  "status": "CANCELLED",
  "amount": 15000,
  "merchantId": 1,
  "merchantOrderId": "ORDER-20241215-001",
  "cancelledAt": "2024-12-15T14:35:00"
}
```

## 📊 결제 상태 코드

| 상태 | 설명 | 다음 가능한 상태 |
|------|------|------------------|
| `READY` | 결제 준비 완료 | `COMPLETED`, `FAILED`, `CANCELLED` |
| `COMPLETED` | 결제 완료 | `CANCELLED` |
| `FAILED` | 결제 실패 | `READY` (재시도 가능) |
| `CANCELLED` | 결제 취소 | - (최종 상태) |

## ⚠️ 에러 처리

### 주요 에러 코드

#### 400 Bad Request
```json
{
  "error": "INVALID_REQUEST",
  "message": "결제 금액은 0보다 커야 합니다",
  "code": "PAYMENT_001"
}
```

#### 404 Not Found
```json
{
  "error": "PAYMENT_NOT_FOUND",
  "message": "결제 정보를 찾을 수 없습니다",
  "code": "PAYMENT_002"
}
```

#### 409 Conflict
```json
{
  "error": "PAYMENT_ALREADY_PROCESSED",
  "message": "이미 처리된 결제입니다",
  "code": "PAYMENT_003"
}
```

#### 408 Request Timeout
```json
{
  "error": "PAYMENT_EXPIRED",
  "message": "결제 시간이 만료되었습니다",
  "code": "PAYMENT_004"
}
```

## 🧪 테스트 시나리오

### 정상 결제 플로우 테스트
```bash
#!/bin/bash

# 1. 결제 준비
PAYMENT_TOKEN=$(curl -s -X POST http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "amount": 10000,
    "merchantOrderId": "TEST-001"
  }' | jq -r '.paymentToken')

echo "Payment Token: $PAYMENT_TOKEN"

# 2. 결제 상태 조회
curl -X GET http://localhost:8081/api/payments/$PAYMENT_TOKEN

# 3. 결제 실행
curl -X PATCH http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "paymentToken": "'$PAYMENT_TOKEN'",
    "cardToken": "card_test_123456"
  }'
```

### 결제 취소 테스트
```bash
# 결제 취소
curl -X DELETE http://localhost:8081/api/payments/$PAYMENT_TOKEN
```

## 🔧 개발자 도구

### Swagger UI
- **URL**: http://localhost:8081/swagger-ui.html
- **API 문서**: http://localhost:8081/v3/api-docs

### 로그 설정
```yaml
logging:
  level:
    com.fastcampus.payment: DEBUG
    com.fastcampus.payment.service: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 주요 로그 확인
```bash
# 결제 관련 로그
grep "PaymentController\|PaymentService" logs/application.log

# 에러 로그
grep "ERROR" logs/application.log | tail -10

# 특정 Payment Token 추적
grep "payment_abc123def456" logs/application.log
```

## 🔒 보안 고려사항

### 1. 토큰 보안
- Payment Token은 1시간 후 자동 만료
- Card Token은 결제 완료 후 즉시 무효화
- 모든 토큰은 암호화된 형태로 저장

### 2. 중복 방지
- `@Idempotent` 어노테이션으로 중복 결제 방지
- 동일한 `merchantOrderId`로 중복 결제 요청 시 기존 결제 정보 반환

### 3. 결제 금액 검증
- 최소 금액: 100원
- 최대 금액: 1,000,000원
- 금액 변조 방지를 위한 서버 사이드 검증

## 📈 성능 및 모니터링

### 메트릭 확인
```bash
# 결제 성공률
curl http://localhost:8081/actuator/metrics/payment.success.rate

# 평균 결제 처리 시간
curl http://localhost:8081/actuator/metrics/payment.processing.time

# 현재 진행 중인 결제 수
curl http://localhost:8081/actuator/metrics/payment.active.count
```

### 헬스체크
```bash
curl http://localhost:8081/actuator/health
```

## 🔗 관련 서비스

### 의존성
- **Payment Method**: 결제 수단 및 카드 정보 관리
- **Common**: JWT 인증 및 공통 유틸리티

### 연동 서비스
- **Backoffice API**: 결제 내역 조회 시 데이터 제공
- **App User Manage**: 사용자 결제 정보 연동

## 🚨 문제 해결

### 자주 발생하는 문제

#### 1. 결제 토큰 만료
```bash
# 해결: 새로운 결제 요청 생성
curl -X POST http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{"merchantId": 1, "amount": 10000, "merchantOrderId": "NEW-ORDER"}'
```

#### 2. 카드 토큰 오류
```bash
# 카드 토큰 유효성 확인
curl -X GET http://localhost:8083/api/cards/card_xyz789ghi012/validate
```

#### 3. 데이터베이스 연결 오류
```bash
# 데이터베이스 상태 확인
curl http://localhost:8081/actuator/health/db
```

## 🧪 테스트 실행

```bash
# 단위 테스트
./gradlew test

# 통합 테스트
./gradlew integrationTest

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

## 📞 지원

문제 발생 시 다음 정보와 함께 문의해주세요:
- Payment Token
- 요청 시간
- 에러 메시지
- 로그 파일 (payment.log)

**기술 지원**: tech-payment@passionpay.com 