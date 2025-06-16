# Backoffice Manage Service

## 📋 개요
Backoffice Manage Service는 **가맹점 관리 서비스**로, 가맹점 회원가입, 로그인, 정보 관리, SDK 키 관리 등의 기능을 제공합니다. 가맹점이 PassionPay 플랫폼에 온보딩하고 서비스를 이용할 수 있도록 지원합니다.

## 🎯 주요 기능
- **가맹점 회원가입/로그인**: JWT 기반 인증 시스템
- **가맹점 정보 관리**: 기본 정보, 연락처 정보 관리
- **SDK 키 관리**: 개발용 SDK 키 발급 및 관리
- **비밀번호 관리**: 비밀번호 변경 및 보안 관리
- **계정 상태 관리**: 활성화/비활성화 처리

## 🏗️ 기술 스택
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL** (가맹점 데이터 저장)
- **Redis** (세션 및 JWT 관리)
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
      password: fintechpass

server:
  port: 8082

jwt:
  secret: dYVzdC1zZWNyZXQta2V5LXlvdS2jYW4tY2hhbmdlPXRoaXM=
  expiration: 180000  # 3분 (개발용 짧은 시간)

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
java -jar build/libs/backoffice-manage-0.0.1-SNAPSHOT.jar
```

### 서비스 상태 확인
```bash
curl http://localhost:8082/actuator/health
```

## 📚 API 가이드

### 🔐 인증 시스템

#### 1️⃣ 가맹점 회원가입
```bash
curl -X POST http://localhost:8082/merchants/register \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "merchant01",
    "loginPw": "passw0rd!",
    "name": "테스트 가맹점",
    "businessNumber": "123-45-67890",
    "contactName": "홍길동",
    "contactEmail": "merchant@test.com",
    "contactPhone": "010-1234-5678"
  }'
```

**성공 응답:**
```json
{
  "merchantId": 1,
  "loginId": "merchant01",
  "name": "테스트 가맹점",
  "businessNumber": "123-45-67890",
  "contactName": "홍길동",
  "contactEmail": "merchant@test.com",
  "contactPhone": "010-1234-5678",
  "status": "ACTIVE",
  "createdAt": "2024-12-15T14:30:00"
}
```

#### 2️⃣ 가맹점 로그인
```bash
curl -X POST http://localhost:8082/merchants/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "merchant01",
    "loginPw": "passw0rd!"
  }'
```

**성공 응답:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 180000,
  "tokenType": "Bearer"
}
```

#### 3️⃣ 토큰 재발급
```bash
curl -X POST http://localhost:8082/merchants/reissue \
  -H "Refresh-Token: Bearer YOUR_REFRESH_TOKEN"
```

#### 4️⃣ 로그아웃
```bash
curl -X POST http://localhost:8082/merchants/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 🏢 가맹점 정보 관리

#### 1️⃣ 내 정보 조회
```bash
curl -X GET http://localhost:8082/merchants/info \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**응답 예시:**
```json
{
  "name": "테스트 가맹점",
  "businessNumber": "123-45-67890",
  "contactName": "홍길동",
  "contactEmail": "merchant@test.com",
  "contactPhone": "010-1234-5678",
  "status": "ACTIVE"
}
```

#### 2️⃣ 정보 수정
```bash
curl -X PUT http://localhost:8082/merchants/modify \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 가맹점명",
    "businessNumber": "123-45-67890",
    "contactName": "김철수",
    "contactEmail": "updated@test.com",
    "contactPhone": "010-9876-5432"
  }'
```

#### 3️⃣ 비밀번호 변경
```bash
curl -X PUT http://localhost:8082/merchants/update-password \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "passw0rd!",
    "newPassword": "newPassw0rd!"
  }'
```

#### 4️⃣ 계정 비활성화
```bash
curl -X DELETE http://localhost:8082/merchants/delete \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 🔑 SDK 키 관리

#### SDK 키 조회
개발자가 API 연동 시 사용할 SDK 키를 조회합니다.

```bash
curl -X GET http://localhost:8082/sdk-key \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**응답 예시:**
```json
{
  "sdkKey": "sk_test_1234567890abcdef",
  "status": "ACTIVE",
  "createdAt": "2024-12-15T14:30:00"
}
```

## ⚠️ 에러 처리

### 주요 에러 코드

#### 400 Bad Request - 입력 검증 오류
```json
{
  "error": "VALIDATION_ERROR",
  "message": "비밀번호는 특수문자, 영어 소문자, 숫자를 각각 최소 하나씩 포함해야 하며 8자 이상 20자 이하여야 합니다.",
  "code": "MERCHANT_001"
}
```

#### 401 Unauthorized - 인증 실패
```json
{
  "error": "INVALID_CREDENTIALS",
  "message": "아이디 또는 비밀번호가 올바르지 않습니다",
  "code": "AUTH_001"
}
```

#### 403 Forbidden - 비활성화 계정
```json
{
  "error": "ACCOUNT_INACTIVE",
  "message": "비활성화된 계정입니다",
  "code": "AUTH_002"
}
```

#### 409 Conflict - 중복 데이터
```json
{
  "error": "DUPLICATE_LOGIN_ID",
  "message": "이미 사용 중인 로그인 ID입니다",
  "code": "MERCHANT_002"
}
```

#### JWT 토큰 관련 에러
```json
{
  "error": "TOKEN_EXPIRED",
  "message": "토큰이 만료되었습니다",
  "code": "JWT_001"
}
```

## 🧪 테스트 시나리오

### 가맹점 온보딩 플로우 테스트
```bash
#!/bin/bash

# 1. 가맹점 회원가입
curl -X POST http://localhost:8082/merchants/register \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "testmerchant",
    "loginPw": "testPass1!",
    "name": "테스트 가맹점",
    "businessNumber": "123-45-67890",
    "contactEmail": "test@merchant.com"
  }'

# 2. 로그인하여 토큰 획득
ACCESS_TOKEN=$(curl -s -X POST http://localhost:8082/merchants/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "testmerchant",
    "loginPw": "testPass1!"
  }' | jq -r '.accessToken')

echo "Access Token: $ACCESS_TOKEN"

# 3. 내 정보 조회
curl -X GET http://localhost:8082/merchants/info \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# 4. SDK 키 조회
curl -X GET http://localhost:8082/sdk-key \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 정보 수정 테스트
```bash
# 가맹점 정보 수정
curl -X PUT http://localhost:8082/merchants/modify \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 가맹점",
    "businessNumber": "123-45-67890",
    "contactName": "수정된 담당자",
    "contactEmail": "updated@merchant.com",
    "contactPhone": "010-9999-8888"
  }'
```

## 🔧 개발자 도구

### Swagger UI
- **URL**: http://localhost:8082/swagger-ui.html
- **API 문서**: http://localhost:8082/api-docs

**⚠️ Swagger에서 인증 방법:**
1. 먼저 `/merchants/login`으로 로그인하여 AccessToken 획득
2. `Authorize` 버튼 클릭
3. `Bearer YOUR_ACCESS_TOKEN` 형식으로 입력

### 로그 설정
```yaml
logging:
  level:
    com.fastcampus.backofficemanage: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 주요 로그 확인
```bash
# 인증 관련 로그
grep "MerchantController\|AuthService" logs/application.log

# JWT 관련 로그
grep "JWT\|JwtProvider" logs/application.log

# 에러 로그
grep "ERROR" logs/application.log | tail -10
```

## 🔒 보안 고려사항

### 1. 비밀번호 정책
- **길이**: 8자 이상 20자 이하
- **구성**: 영어 소문자, 숫자, 특수문자 각각 최소 1개씩 포함
- **암호화**: BCrypt를 사용한 단방향 암호화

### 2. JWT 토큰 관리
- **Access Token**: 3분 (개발용 짧은 시간)
- **Refresh Token**: 7일
- **로그아웃**: 토큰 블랙리스트 처리

### 3. 로그인 ID 정책
- **길이**: 최대 20자
- **중복**: 시스템 전체에서 유일해야 함

### 4. 비즈니스 등록번호
- **형식**: XXX-XX-XXXXX
- **중복**: 시스템 전체에서 유일해야 함

## 📈 성능 및 모니터링

### 메트릭 확인
```bash
# 가맹점 등록 수
curl http://localhost:8082/actuator/metrics/merchant.registration.count

# 로그인 성공률
curl http://localhost:8082/actuator/metrics/merchant.login.success.rate

# 현재 활성 세션 수
curl http://localhost:8082/actuator/metrics/merchant.active.sessions
```

### 헬스체크
```bash
curl http://localhost:8082/actuator/health
```

**상세 헬스체크:**
```bash
curl http://localhost:8082/actuator/health/db      # 데이터베이스 상태
curl http://localhost:8082/actuator/health/redis   # Redis 상태
```

## 🔗 관련 서비스

### 의존성
- **Common**: JWT 유틸리티, 공통 예외 처리
- **PostgreSQL**: 가맹점 정보 저장
- **Redis**: JWT 토큰 관리

### 연동 서비스
- **Backoffice API**: 가맹점 인증 정보 제공
- **Payment**: 가맹점 ID를 통한 결제 처리

## 🚨 문제 해결

### 자주 발생하는 문제

#### 1. 토큰 만료 에러
```bash
# 해결: Refresh Token으로 새 토큰 발급
curl -X POST http://localhost:8082/merchants/reissue \
  -H "Refresh-Token: Bearer YOUR_REFRESH_TOKEN"
```

#### 2. 비밀번호 정책 위반
```bash
# 올바른 비밀번호 형식 예시
{
  "currentPassword": "oldPass1!",
  "newPassword": "newPass2@"  # 소문자+숫자+특수문자 포함
}
```

#### 3. 중복 로그인 ID
```bash
# 해결: 다른 로그인 ID 사용
{
  "loginId": "unique_merchant_id_001",  # 시스템에서 유일한 ID
  "loginPw": "securePass1!"
}
```

#### 4. Redis 연결 오류
```bash
# Redis 상태 확인
docker ps | grep redis
curl http://localhost:8082/actuator/health/redis
```

## 📊 데이터베이스 스키마

### Merchant 테이블
```sql
CREATE TABLE merchant (
    merchant_id BIGSERIAL PRIMARY KEY,
    login_id VARCHAR(20) UNIQUE NOT NULL,
    login_pw VARCHAR(60) NOT NULL,  -- BCrypt 암호화
    name VARCHAR(50) NOT NULL,
    business_number VARCHAR(20) UNIQUE NOT NULL,
    contact_name VARCHAR(30),
    contact_email VARCHAR(50),
    contact_phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Keys 테이블
```sql
CREATE TABLE keys (
    key_id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT REFERENCES merchant(merchant_id),
    sdk_key VARCHAR(255) UNIQUE NOT NULL,
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

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

### 테스트용 가맹점 계정
```json
{
  "loginId": "test_merchant",
  "loginPw": "testPass1!",
  "name": "테스트 가맹점",
  "businessNumber": "999-99-99999"
}
```

## 📞 지원

문제 발생 시 다음 정보와 함께 문의해주세요:
- 가맹점 ID (merchantId)
- 로그인 ID
- 에러 발생 시간
- 에러 메시지
- 요청 내용

**기술 지원**: tech-merchant@passionpay.com 