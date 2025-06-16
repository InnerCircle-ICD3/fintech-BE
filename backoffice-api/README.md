# Backoffice API

## Overview
Backoffice API는 가맹점 관리자 대시보드를 위한 RESTful API 서비스입니다. 이 모듈은 가맹점 관리, API 키 관리, 결제 내역 조회 등의 기능을 제공합니다.

## 주요 기능
- 가맹점 관리 (조회, 생성, 수정, 삭제)
- API 키 관리 (생성, 재발급, 조회, 비활성화)
- 결제 내역 관리 (조회, 상세 조회)
- 결제 수단 관리

## 기술 스택
- Java 21
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- JWT Authentication
- Gradle
- Swagger/OpenAPI

## 시작하기

### 필수 요구사항
- Java 21
- PostgreSQL
- Redis
- Gradle

### 로컬 개발 환경 설정
1. PostgreSQL 데이터베이스 설정
   ```sql
   CREATE DATABASE testdb;
   CREATE USER testuser WITH PASSWORD 'testpass';
   GRANT ALL PRIVILEGES ON DATABASE testdb TO testuser;
   ```

2. application.yml 설정
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/testdb
       username: testuser
       password: testpass
       driver-class-name: org.postgresql.Driver
     jpa:
       hibernate:
         ddl-auto: update
       show-sql: true
       properties:
         hibernate:
           format_sql: true
           dialect: org.hibernate.dialect.PostgreSQLDialect
     data:
       redis:
         host: localhost
         port: 6379
         password: fintechpass

   server:
     port: 8080

   jwt:
     secret: your_jwt_secret_key
     expiration: 86400000  # 24시간
   ```

### 빌드 및 실행
```bash
# 프로젝트 루트 디렉토리에서
./gradlew :backoffice-api:build

# 실행
java -jar backoffice-api/build/libs/backoffice-api.jar
```

## 🔐 인증 가이드

### JWT 토큰 발급
백오피스 API를 사용하려면 먼저 JWT 토큰이 필요합니다.

#### 관리자 로그인 (토큰 발급)
```bash
curl -X POST http://localhost:8080/merchants/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com", 
    "password": "password123"
  }'
```

**성공 응답:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400
}
```

### 인증이 필요한 API 호출
모든 API 호출 시 Authorization 헤더에 Bearer 토큰을 포함해야 합니다:
```bash
curl -X GET http://localhost:8080/merchants/api-keys/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## API 엔드포인트

### API 키 관리
- POST /merchants/api-keys/{merchantId} - API 키 신규 생성
- POST /merchants/api-keys/{merchantId}/reissue - API 키 재발급
- GET /merchants/api-keys/{merchantId} - 가맹점의 API 키 목록 조회
- DELETE /merchants/api-keys/{key} - API 키 비활성화

### 결제 내역 관리
- GET /merchants/payment-histories - 결제 내역 조회
  - Query Parameters:
    - merchantId: 가맹점 ID
    - status: 결제 상태 (선택)
    - startDate: 시작일 (yyyy-MM-dd)
    - endDate: 종료일 (yyyy-MM-dd)
    - page: 페이지 번호
    - size: 페이지 크기
- GET /merchants/payment-histories/{paymentToken} - 결제 상세 조회

## 🧪 API 테스트 가이드

### cURL을 사용한 API 테스트

#### 1. API 키 생성
```bash
curl -X POST http://localhost:8080/merchants/api-keys/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**성공 응답:**
```json
{
  "keyId": 1,
  "merchantId": 1,
  "keyValue": "ak_test_1234567890abcdef",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00",
  "expiresAt": "2025-01-15T10:30:00"
}
```

#### 2. 결제 내역 조회
```bash
curl -X GET "http://localhost:8080/merchants/payment-histories?merchantId=1&startDate=2024-01-01&endDate=2024-01-31&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**성공 응답:**
```json
{
  "content": [
    {
      "paymentToken": "payment_1234567890",
      "merchantId": 1,
      "amount": 15000,
      "currency": "KRW",
      "status": "COMPLETED",
      "paymentMethod": "CREDIT_CARD",
      "createdAt": "2024-01-15T14:30:00",
      "updatedAt": "2024-01-15T14:31:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

#### 3. 결제 상세 조회
```bash
curl -X GET http://localhost:8080/merchants/payment-histories/payment_1234567890 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 🛠️ 개발 도구 활용

#### Swagger UI 사용
- **로컬**: http://localhost:8080/swagger-ui.html
- **API 문서**: http://localhost:8080/v3/api-docs

**⚠️ Swagger에서 인증하는 방법:**
1. `Authorize` 버튼 클릭
2. `Bearer YOUR_ACCESS_TOKEN` 형식으로 입력
3. `Authorize` 클릭 후 API 테스트

#### Postman 컬렉션 사용
1. **환경 변수 설정**
   - `BASE_URL`: http://localhost:8080
   - `ACCESS_TOKEN`: 로그인 후 받은 토큰

2. **사전 스크립트 (Pre-request Script)**
   ```javascript
   pm.request.headers.add({
     key: 'Authorization',
     value: 'Bearer ' + pm.environment.get('ACCESS_TOKEN')
   });
   ```

## 보안
- JWT 기반 인증
- Spring Security를 통한 엔드포인트 보호
- HTTPS 적용 (프로덕션 환경)
- 비밀번호 암호화 (BCrypt)

## ⚠️ 에러 처리 가이드

### 주요 에러 코드와 해결 방법

#### 401 Unauthorized
```json
{
  "error": "UNAUTHORIZED",
  "message": "Access token이 유효하지 않습니다",
  "code": "AUTH_001"
}
```
**해결책**: 토큰 재발급 또는 로그인 다시 시도

#### 403 Forbidden
```json
{
  "error": "FORBIDDEN", 
  "message": "해당 리소스에 접근 권한이 없습니다",
  "code": "AUTH_002"
}
```
**해결책**: 관리자 권한 확인

#### 404 Not Found
```json
{
  "error": "NOT_FOUND",
  "message": "요청한 리소스를 찾을 수 없습니다",
  "code": "RESOURCE_001"
}
```
**해결책**: 요청 URL 및 파라미터 확인

## 모니터링
- Actuator 엔드포인트:
  - /actuator/health: 서비스 상태 확인
  - /actuator/info: 서비스 정보 확인
  - /actuator/metrics: 메트릭 정보 확인
- Swagger UI: /swagger-ui.html
- API 문서: /api-docs

## 🔍 디버깅 및 로그

### 로그 레벨 설정
```yaml
logging:
  level:
    com.fastcampus.backoffice: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

### 주요 로그 확인 포인트
```bash
# JWT 인증 관련 로그
grep "JWT" logs/application.log

# API 호출 로그  
grep "ApiKeyController\|PaymentController" logs/application.log

# 에러 로그
grep "ERROR" logs/application.log | tail -10
```

## 문제 해결
1. 서비스 상태 확인
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. 로그 확인
   ```bash
   tail -f /opt/backoffice-api/backoffice-api.log
   ```

3. 데이터베이스 연결 확인
   ```bash
   psql -U testuser -d testdb
   ```

4. JWT 토큰 디버깅
   ```bash
   # 토큰 만료 확인
   curl -X GET http://localhost:8080/actuator/info \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
   ```

## 🧪 테스트 실행

### 단위 테스트
```bash
./gradlew test
```

### 통합 테스트
```bash
./gradlew integrationTest
```

### 테스트 커버리지 확인
```bash
./gradlew jacocoTestReport
# 리포트 위치: build/reports/jacoco/test/html/index.html
```

## 기여
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스
이 프로젝트는 MIT 라이선스 하에 배포됩니다. 