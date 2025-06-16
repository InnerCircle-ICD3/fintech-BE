# PassionPay - 핀테크 플랫폼

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.7-blue?style=flat-square&logo=gradle)](https://gradle.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7+-red?style=flat-square&logo=redis)](https://redis.io/)

## 🚀 프로젝트 개요

PassionPay는 **마이크로서비스 아키텍처** 기반의 종합 핀테크 플랫폼입니다. 결제 처리, 가맹점 관리, 사용자 관리 등 핀테크 생태계의 핵심 기능들을 독립적이고 확장 가능한 서비스로 구성했습니다.

## 🏗️ 시스템 아키텍처

```
passionpay/
├── 🏢 backoffice-api/        # 관리자 대시보드 API (포트: 8080)
├── 🛠️ backoffice-manage/     # 가맹점 관리 서비스 (포트: 8082)  
├── 💳 payment/               # 결제 처리 서비스 (포트: 8081)
├── 🏪 payment-method/        # 결제 수단 관리 라이브러리
├── 👥 appuser-manage/        # 앱 사용자 관리 서비스 (포트: 8083)
└── 🔧 common/                # 공통 라이브러리 (JWT, 유틸리티)
```

### 📊 서비스별 책임 분리

| 서비스 | 포트 | 주요 기능 | 데이터베이스 |
|--------|------|-----------|-------------|
| **backoffice-api** | 8080 | 관리자 대시보드, API 키 관리, 결제 내역 조회 | PostgreSQL, Redis |
| **backoffice-manage** | 8082 | 가맹점 회원가입/로그인, 가맹점 정보 관리, SDK 키 관리 | PostgreSQL, Redis |
| **payment** | 8081 | 결제 요청/실행/취소, QR 코드 거래, 결제 상태 관리 | PostgreSQL, Redis |
| **appuser-manage** | 8083 | 앱 사용자 회원가입/로그인, 사용자 정보 관리, 카드 관리 | PostgreSQL, Redis |
| **payment-method** | - | 결제 수단 엔티티 및 리포지토리 (공유 라이브러리) | PostgreSQL |
| **common** | - | JWT 처리, 공통 예외, 유틸리티 (공유 라이브러리) | - |

## 🛠️ 기술 스택

### 🔧 Backend
- **언어**: Java 21
- **프레임워크**: Spring Boot 3.4.5
- **보안**: Spring Security + JWT
- **빌드 도구**: Gradle 8.7
- **API 문서**: SpringDoc OpenAPI 3

### 🗄️ 데이터베이스 & 캐시
- **RDBMS**: PostgreSQL 14+
- **캐시**: Redis 7+
- **ORM**: Spring Data JPA + Hibernate

### 🐳 인프라 & 배포
- **컨테이너**: Docker
- **오케스트레이션**: Kubernetes
- **CI/CD**: Jenkins
- **모니터링**: Spring Boot Actuator

## 🚀 빠른 시작

### 필수 요구사항
- ☑️ Java 21
- ☑️ PostgreSQL 14+
- ☑️ Redis 7+
- ☑️ Docker (선택사항)

### 1️⃣ 데이터베이스 설정
```sql
-- PostgreSQL 데이터베이스 설정
CREATE DATABASE testdb;
CREATE USER testuser WITH PASSWORD 'testpass';
GRANT ALL PRIVILEGES ON DATABASE testdb TO testuser;
```

### 2️⃣ Redis 설정
```bash
# Docker로 Redis 실행
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 또는 로컬 Redis 설치 후 실행
redis-server
```

### 3️⃣ 전체 시스템 빌드
```bash
# 프로젝트 루트에서
./gradlew clean build
```

### 4️⃣ 개별 서비스 실행
```bash
# 1. 가맹점 관리 서비스 (포트: 8082)
./gradlew :backoffice-manage:bootRun

# 2. 결제 서비스 (포트: 8081)
./gradlew :payment:bootRun

# 3. 백오피스 API (포트: 8080)
./gradlew :backoffice-api:bootRun

# 4. 앱 사용자 관리 (포트: 8083)
./gradlew :appuser-manage:bootRun
```

### 5️⃣ 서비스 상태 확인
```bash
# 각 서비스 헬스체크
curl http://localhost:8080/actuator/health  # backoffice-api
curl http://localhost:8082/actuator/health  # backoffice-manage
curl http://localhost:8081/actuator/health  # payment
curl http://localhost:8083/actuator/health  # appuser-manage
```

## 📚 모듈별 상세 문서

| 모듈 | 문서 링크 | 설명 |
|------|-----------|------|
| 🏢 **Backoffice API** | [문서 보기](./backoffice-api/README.md) | 관리자 대시보드 API 사용법 |
| 🛠️ **Backoffice Manage** | [문서 보기](./backoffice-manage/README.md) | 가맹점 관리 서비스 가이드 |
| 💳 **Payment** | [문서 보기](./payment/README.md) | 결제 처리 서비스 API |
| 👥 **App User Manage** | [문서 보기](./appuser-manage/README.md) | 앱 사용자 관리 API |
| 🏪 **Payment Method** | [문서 보기](./payment-method/README.md) | 결제 수단 라이브러리 |
| 🔧 **Common** | [문서 보기](./common/README.md) | 공통 라이브러리 사용법 |

## 🐳 Docker 실행

### 개별 서비스 실행
```bash
# 백오피스 API
docker build --build-arg MODULE=backoffice-api -t passionpay-backoffice-api .
docker run -p 8080:8080 passionpay-backoffice-api

# 결제 서비스
docker build --build-arg MODULE=payment -t passionpay-payment .
docker run -p 8081:8080 passionpay-payment
```

### Docker Compose (개발용)
```yaml
# docker-compose.yml
version: '3.8'
services:
  postgres:
    image: postgres:14-alpine
    environment:
      POSTGRES_DB: testdb
      POSTGRES_USER: testuser
      POSTGRES_PASSWORD: testpass
    ports:
      - "5432:5432"
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

## ☸️ Kubernetes 배포

### 개발/테스트 환경 배포
```bash
# K8s 리소스 배포
kubectl apply -f k8s/

# 서비스 상태 확인
kubectl get pods,svc
```

### 프로덕션 배포
```bash
# Jenkins 파이프라인을 통한 자동 배포
# Jenkinsfile 참조
```

## 🧪 전체 시스템 테스트

### 1️⃣ 가맹점 등록 및 로그인
```bash
# 가맹점 회원가입
curl -X POST http://localhost:8082/merchants/register \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "merchant01",
    "loginPw": "passw0rd!",
    "name": "테스트 가맹점",
    "businessNumber": "123-45-67890",
    "contactEmail": "merchant@test.com"
  }'

# 가맹점 로그인
curl -X POST http://localhost:8082/merchants/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginId": "merchant01",
    "loginPw": "passw0rd!"
  }'
```

### 2️⃣ 결제 플로우 테스트
```bash
# 1. 결제 준비
curl -X POST http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "amount": 10000,
    "merchantOrderId": "ORDER-001"
  }'

# 2. 결제 실행
curl -X PATCH http://localhost:8081/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "paymentToken": "payment_token_here",
    "cardToken": "card_token_here"
  }'
```

### 3️⃣ 백오피스에서 결제 내역 조회
```bash
curl -X GET "http://localhost:8080/merchants/payment-histories?merchantId=1&startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📊 모니터링 및 로그

### Actuator 엔드포인트
```bash
# 헬스체크
curl http://localhost:8080/actuator/health

# 메트릭 정보
curl http://localhost:8080/actuator/metrics

# 애플리케이션 정보
curl http://localhost:8080/actuator/info
```

### API 문서
- **Backoffice API**: http://localhost:8080/swagger-ui.html
- **Backoffice Manage**: http://localhost:8082/swagger-ui.html
- **Payment API**: http://localhost:8081/swagger-ui.html
- **App User Manage**: http://localhost:8083/swagger-ui.html

### 로그 모니터링
```bash
# 전체 서비스 로그 확인
tail -f logs/*.log

# 특정 서비스 로그
tail -f logs/backoffice-api.log
tail -f logs/payment.log
```

## 🔧 개발 환경 설정

### IDE 설정
1. **IntelliJ IDEA** 권장
2. **Lombok Plugin** 설치 필수
3. **Java 21** SDK 설정

### 코드 스타일
- **Google Java Style Guide** 준수
- **SpotBugs**, **Checkstyle** 적용

### 테스트 실행
```bash
# 전체 모듈 테스트
./gradlew test

# 특정 모듈 테스트
./gradlew :payment:test
./gradlew :backoffice-api:test

# 통합 테스트
./gradlew integrationTest
```

## 🤝 기여하기

1. **Fork** the Project
2. Create your **Feature Branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit** your Changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the Branch (`git push origin feature/AmazingFeature`)
5. Open a **Pull Request**

### 개발 가이드라인
- 각 모듈은 독립적으로 개발
- API 변경 시 문서 업데이트 필수
- 테스트 커버리지 80% 이상 유지
- 코드 리뷰 필수

## 📞 지원 및 문의

- **이슈 제기**: [GitHub Issues](../../issues)
- **보안 관련**: security@passionpay.com
- **기술 문의**: tech@passionpay.com

## 📄 라이선스

이 프로젝트는 **MIT 라이선스** 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 🔄 버전 히스토리

- **v0.0.1-SNAPSHOT** (현재)
  - 초기 마이크로서비스 아키텍처 구성
  - 결제, 가맹점 관리, 사용자 관리 기본 기능 구현
  - JWT 인증 시스템 구축
  - Kubernetes 배포 환경 구성
