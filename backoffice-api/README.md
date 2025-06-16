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

## 보안
- JWT 기반 인증
- Spring Security를 통한 엔드포인트 보호
- HTTPS 적용 (프로덕션 환경)
- 비밀번호 암호화 (BCrypt)

## 모니터링
- Actuator 엔드포인트:
  - /actuator/health: 서비스 상태 확인
  - /actuator/info: 서비스 정보 확인
- Swagger UI: /swagger-ui.html
- API 문서: /api-docs

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

## 기여
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스
이 프로젝트는 MIT 라이선스 하에 배포됩니다. 