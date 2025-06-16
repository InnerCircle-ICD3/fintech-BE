# App User Manage Service

## 📋 개요
App User Manage Service는 **앱 사용자 관리 서비스**로, 일반 사용자의 회원가입, 로그인, 정보 관리, 카드 관리, 거래 내역 조회 등의 기능을 제공합니다. PassionPay 앱을 사용하는 일반 소비자들을 위한 서비스입니다.

## 🎯 주요 기능
- **사용자 회원가입/로그인**: 이메일 기반 JWT 인증
- **사용자 정보 관리**: 개인정보 수정, 비밀번호 변경
- **카드 관리**: 카드 등록, 조회, 삭제
- **거래 내역 조회**: 개인 결제 내역 확인
- **계정 관리**: 로그아웃, 회원 탈퇴 (Soft Delete)

## 🏗️ 기술 스택
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL** (사용자 및 카드 데이터)
- **Redis** (세션 및 JWT 블랙리스트 관리)
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
  port: 8083

jwt:
  secret: dYVzdC1zZWNyZXQta2V5LXlvdS2jYW4tY2hhbmdlPXRoaXM=
  expiration: 3600000  # 1시간

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### 빌드 및 실행
```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
# 또는
java -jar build/libs/appuser-manage-0.0.1-SNAPSHOT.jar
```

### 서비스 상태 확인
```bash
curl http://localhost:8083/actuator/health
```

## 📚 API 가이드

### 👤 사용자 인증 관리

#### 1️⃣ 회원가입
```bash
curl -X POST http://localhost:8083/app-users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "userPass1!",
    "name": "홍길동",
    "phone": "010-1234-5678"
  }'
```

**성공 응답:**
```json
{
  "userId": 1,
  "email": "user@example.com",
  "name": "홍길동",
  "status": "ACTIVE"
}
```

#### 2️⃣ 로그인
```bash
curl -X POST http://localhost:8083/app-users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "userPass1!"
  }'
```

**성공 응답:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000,
  "tokenType": "Bearer"
}
```

#### 3️⃣ 토큰 재발급
```bash
curl -X POST http://localhost:8083/app-users/reissue \
  -H "Refresh-Token: Bearer YOUR_REFRESH_TOKEN"
```

#### 4️⃣ 로그아웃
```bash
curl -X POST http://localhost:8083/app-users/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "로그아웃 완료"
}
```

### 👥 사용자 정보 관리

#### 1️⃣ 내 정보 조회
```bash
curl -X GET http://localhost:8083/app-users/info \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**응답 예시:**
```json
{
  "email": "user@example.com",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "status": "ACTIVE"
}
```

#### 2️⃣ 정보 수정
```bash
curl -X PUT http://localhost:8083/app-users/modify \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "김철수",
    "phone": "010-9876-5432"
  }'
```

#### 3️⃣ 비밀번호 변경
```bash
curl -X PUT http://localhost:8083/app-users/update-password \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "userPass1!",
    "newPassword": "newUserPass2@"
  }'
```

#### 4️⃣ 회원 탈퇴 (Soft Delete)
```bash
curl -X DELETE http://localhost:8083/app-users/delete \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**응답:**
```json
{
  "success": true,
  "message": "회원 탈퇴가 완료되었습니다."
}
```

### 💳 카드 관리

#### 1️⃣ 카드 등록
```bash
curl -X POST http://localhost:8083/api/cards/register \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "1234-5678-9012-3456",
    "expiryDate": "12/26",
    "cardHolderName": "홍길동",
    "cardType": "CREDIT"
  }'
```

**성공 응답:**
```json
{
  "cardToken": "card_abc123def456",
  "maskedCardNumber": "1234-****-****-3456",
  "cardHolderName": "홍길동",
  "cardType": "CREDIT",
  "status": "ACTIVE",
  "createdAt": "2024-12-15T14:30:00"
}
```

#### 2️⃣ 내 카드 목록 조회
```bash
curl -X GET http://localhost:8083/api/cards \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**응답 예시:**
```json
[
  {
    "cardToken": "card_abc123def456",
    "maskedCardNumber": "1234-****-****-3456",
    "cardHolderName": "홍길동",
    "cardType": "CREDIT",
    "status": "ACTIVE",
    "createdAt": "2024-12-15T14:30:00"
  },
  {
    "cardToken": "card_xyz789ghi012",
    "maskedCardNumber": "9876-****-****-5432",
    "cardHolderName": "홍길동",
    "cardType": "DEBIT",
    "status": "ACTIVE",
    "createdAt": "2024-12-10T10:15:00"
  }
]
```

#### 3️⃣ 카드 삭제
```bash
curl -X DELETE http://localhost:8083/api/cards/card_abc123def456 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 📊 거래 내역 조회

#### 내 거래 내역 조회
```bash
curl -X GET http://localhost:8083/api/info/transactions \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**응답 예시:**
```json
[
  {
    "transactionId": "txn_001",
    "paymentToken": "payment_abc123def456",
    "amount": 15000,
    "merchantName": "스타벅스",
    "status": "COMPLETED",
    "transactionDate": "2024-12-15T14:30:00",
    "cardInfo": {
      "maskedCardNumber": "1234-****-****-3456",
      "cardType": "CREDIT"
    }
  }
]
```

## ⚠️ 에러 처리

### 주요 에러 코드

#### 400 Bad Request - 입력 검증 오류
```json
{
  "error": "VALIDATION_ERROR",
  "message": "이메일 형식이 올바르지 않습니다",
  "code": "USER_001"
}
```

#### 401 Unauthorized - 인증 실패
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "이메일 또는 비밀번호가 올바르지 않습니다",
  "code": "AUTH_001"
}
```

#### 403 Forbidden - 계정 비활성화
```json
{
  "error": "ACCOUNT_INACTIVE",
  "message": "비활성화된 계정입니다",
  "code": "AUTH_002"
}
```

#### 409 Conflict - 중복 이메일
```json
{
  "error": "DUPLICATE_EMAIL",
  "message": "이미 사용 중인 이메일입니다",
  "code": "USER_002"
}
```

#### 카드 관련 에러
```json
{
  "error": "INVALID_CARD",
  "message": "유효하지 않은 카드 정보입니다",
  "code": "CARD_001"
}
```

## 🧪 테스트 시나리오

### 사용자 온보딩 플로우 테스트
```bash
#!/bin/bash

# 1. 회원가입
curl -X POST http://localhost:8083/app-users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "testPass1!",
    "name": "테스트 사용자",
    "phone": "010-1111-2222"
  }'

# 2. 로그인하여 토큰 획득
ACCESS_TOKEN=$(curl -s -X POST http://localhost:8083/app-users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "testPass1!"
  }' | jq -r '.accessToken')

echo "Access Token: $ACCESS_TOKEN"

# 3. 내 정보 조회
curl -X GET http://localhost:8083/app-users/info \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# 4. 카드 등록
curl -X POST http://localhost:8083/api/cards/register \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "1234-5678-9012-3456",
    "expiryDate": "12/26",
    "cardHolderName": "테스트 사용자",
    "cardType": "CREDIT"
  }'

# 5. 카드 목록 조회
curl -X GET http://localhost:8083/api/cards \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 결제 연동 테스트
```bash
# 결제 가능한 카드 조회 후 결제 시스템 연동
CARD_TOKEN=$(curl -s -X GET http://localhost:8083/api/cards \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq -r '.[0].cardToken')

echo "Card Token: $CARD_TOKEN"

# 이 카드 토큰을 Payment Service에서 사용
curl -X PATCH http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "paymentToken": "payment_from_merchant",
    "cardToken": "'$CARD_TOKEN'"
  }'
```

## 🔧 개발자 도구

### Swagger UI
- **URL**: http://localhost:8083/swagger-ui.html
- **API 문서**: http://localhost:8083/api-docs

**⚠️ Swagger에서 인증 방법:**
1. `/app-users/login`으로 로그인하여 AccessToken 획득
2. `Authorize` 버튼 클릭
3. `Bearer YOUR_ACCESS_TOKEN` 형식으로 입력

### 로그 설정
```yaml
logging:
  level:
    com.fastcampus.appusermanage: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 주요 로그 확인
```bash
# 사용자 관련 로그
grep "UserController\|UserService" logs/application.log

# 카드 관련 로그
grep "CardController\|UserCardService" logs/application.log

# JWT 관련 로그
grep "JWT\|JwtProvider" logs/application.log
```

## 🔒 보안 고려사항

### 1. 이메일 및 비밀번호 정책
- **이메일**: 표준 이메일 형식, 최대 50자
- **비밀번호**: 8~20자, 소문자+숫자+특수문자 포함
- **암호화**: BCrypt 사용

### 2. 카드 정보 보안
- **카드 번호**: 마스킹 처리 (1234-****-****-3456)
- **토큰화**: 실제 카드 번호 대신 토큰 사용
- **암호화**: 민감 정보는 암호화 저장

### 3. JWT 토큰 관리
- **Access Token**: 1시간
- **Refresh Token**: 7일
- **블랙리스트**: 로그아웃 시 토큰 무효화

### 4. 개인정보 보호
- **Soft Delete**: 탈퇴 시 데이터 삭제가 아닌 상태 변경
- **로그 마스킹**: 민감 정보 로그 출력 방지

## 📈 성능 및 모니터링

### 메트릭 확인
```bash
# 사용자 등록 수
curl http://localhost:8083/actuator/metrics/user.registration.count

# 카드 등록 수
curl http://localhost:8083/actuator/metrics/card.registration.count

# 로그인 성공률
curl http://localhost:8083/actuator/metrics/user.login.success.rate
```

### 헬스체크
```bash
curl http://localhost:8083/actuator/health
```

## 🔗 관련 서비스

### 의존성
- **Payment Method**: 사용자, 카드 엔티티 공유
- **Common**: JWT 유틸리티, 공통 예외 처리

### 연동 서비스
- **Payment**: 카드 토큰을 통한 결제 처리
- **Backoffice API**: 사용자 결제 내역 제공

## 🚨 문제 해결

### 자주 발생하는 문제

#### 1. 토큰 만료
```bash
# Refresh Token으로 재발급
curl -X POST http://localhost:8083/app-users/reissue \
  -H "Refresh-Token: Bearer YOUR_REFRESH_TOKEN"
```

#### 2. 이메일 중복
```json
{
  "error": "DUPLICATE_EMAIL",
  "solution": "다른 이메일 주소를 사용하거나 기존 계정으로 로그인하세요"
}
```

#### 3. 카드 등록 실패
```bash
# 카드 정보 형식 확인
{
  "cardNumber": "1234-5678-9012-3456",  # 하이픈 포함 16자리
  "expiryDate": "MM/YY",                 # MM/YY 형식
  "cardHolderName": "영문 이름"           # 영문 대문자
}
```

## 📊 데이터베이스 스키마

### User 테이블
```sql
CREATE TABLE user (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,  -- BCrypt 암호화
    name VARCHAR(10) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Card Info 테이블
```sql
CREATE TABLE card_info (
    card_id BIGSERIAL PRIMARY KEY,
    card_token VARCHAR(255) UNIQUE NOT NULL,
    card_number_encrypted TEXT NOT NULL,  -- 암호화된 카드번호
    masked_card_number VARCHAR(20) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    expiry_date VARCHAR(5) NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🧪 테스트 실행

```bash
# 단위 테스트
./gradlew test

# 통합 테스트  
./gradlew integrationTest

# 테스트 커버리지
./gradlew jacocoTestReport
```

### 테스트용 사용자 계정
```json
{
  "email": "test.user@example.com",
  "password": "testPass1!",
  "name": "테스트 사용자",
  "phone": "010-0000-0000"
}
```

## 📞 지원

문제 발생 시 다음 정보와 함께 문의해주세요:
- 사용자 ID (userId)
- 이메일 주소
- 에러 발생 시간
- 에러 메시지
- 사용 중인 기능

**기술 지원**: tech-user@passionpay.com 