# Backoffice API

## Overview
Backoffice API는 관리자 대시보드를 위한 RESTful API 서비스입니다. 이 모듈은 관리자 인증, 사용자 관리, 결제 관리 등의 기능을 제공합니다.

## 주요 기능
- 관리자 인증 및 권한 관리
- 사용자 관리 (조회, 생성, 수정, 삭제)
- 결제 내역 관리
- 결제 수단 관리
- 거래 내역 관리

## 기술 스택
- Java 21
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Gradle

## 시작하기

### 필수 요구사항
- Java 21
- PostgreSQL
- Gradle

### 로컬 개발 환경 설정
1. PostgreSQL 데이터베이스 설정
   ```sql
   CREATE DATABASE backoffice_db;
   CREATE USER backoffice_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE backoffice_db TO backoffice_user;
   ```

2. application.yml 설정
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/backoffice_db
       username: backoffice_user
       password: your_password
   ```

3. JWT 설정
   ```yaml
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

### 인증
- POST /api/v1/auth/login - 관리자 로그인
- POST /api/v1/auth/refresh - 토큰 갱신

### 사용자 관리
- GET /api/v1/users - 사용자 목록 조회
- GET /api/v1/users/{id} - 사용자 상세 조회
- POST /api/v1/users - 사용자 생성
- PUT /api/v1/users/{id} - 사용자 정보 수정
- DELETE /api/v1/users/{id} - 사용자 삭제

### 결제 관리
- GET /api/v1/payments - 결제 내역 조회
- GET /api/v1/payments/{id} - 결제 상세 조회
- POST /api/v1/payments - 결제 생성
- PUT /api/v1/payments/{id} - 결제 정보 수정

### 결제 수단 관리
- GET /api/v1/payment-methods - 결제 수단 목록 조회
- GET /api/v1/payment-methods/{id} - 결제 수단 상세 조회
- POST /api/v1/payment-methods - 결제 수단 생성
- PUT /api/v1/payment-methods/{id} - 결제 수단 수정
- DELETE /api/v1/payment-methods/{id} - 결제 수단 삭제

## 배포

### CI/CD 파이프라인
이 프로젝트는 GitHub Actions를 사용하여 CI/CD 파이프라인이 구성되어 있습니다.

1. GitHub Secrets 설정
   - SERVER_HOST: 서버 IP 또는 도메인
   - SERVER_USER: 서버 접속 계정
   - SERVER_SSH_KEY: 서버 SSH 키
   - SERVER_DEPLOY_PATH: 배포 경로

2. 자동 배포 프로세스
   - main 브랜치에 push 시 자동으로 빌드 및 배포
   - 서버에 JAR 파일 전송
   - systemd 서비스 재시작

### 서버 설정
1. Java 21 설치
   ```bash
   sudo apt update
   sudo apt install openjdk-21-jdk
   ```

2. 서비스 설정
   ```bash
   sudo systemctl enable backoffice-api
   sudo systemctl start backoffice-api
   ```

3. 로그 확인
   ```bash
   sudo journalctl -u backoffice-api -f
   ```

## 보안
- JWT 기반 인증
- Spring Security를 통한 엔드포인트 보호
- HTTPS 적용 (프로덕션 환경)
- 비밀번호 암호화 (BCrypt)

## 모니터링
- 애플리케이션 로그: `/opt/backoffice-api/backoffice-api.log`
- 시스템 로그: `journalctl -u backoffice-api`

## 문제 해결
1. 서비스 상태 확인
   ```bash
   sudo systemctl status backoffice-api
   ```

2. 로그 확인
   ```bash
   sudo journalctl -u backoffice-api -f
   ```

3. 데이터베이스 연결 확인
   ```bash
   psql -U backoffice_user -d backoffice_db
   ```

## 기여
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스
이 프로젝트는 MIT 라이선스 하에 배포됩니다. 