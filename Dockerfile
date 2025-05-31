# 1단계: 빌드용 이미지
FROM gradle:8.5-jdk21-alpine as builder
WORKDIR /app

# 전체 소스 복사
COPY . .

# 모듈 지정하여 빌드
# 이 값은 'docker build' 시 --build-arg MODULE=<모듈이름> 으로 반드시 제공되어야 합니다.
# 예: --build-arg MODULE=payment-api
ARG MODULE
RUN gradle clean ${MODULE}:build -x test --no-daemon

# 2단계: 실행용 이미지
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드 시 사용된 동일한 모듈 이름을 전달받습니다.
# 이 값은 'docker build' 시 --build-arg MODULE=<모듈이름> 으로 반드시 제공되어야 합니다.
ARG MODULE

# 모듈별 포트 설정을 위한 ARG 추가
ARG SERVER_PORT=9080

# 명확한 파일명 패턴 사용
COPY --from=builder /app/${MODULE}/build/libs/${MODULE}-0.0.1-SNAPSHOT.jar /app/app.jar

# 컨테이너 내부 애플리케이션 포트는 실행 시 SERVER_PORT 환경 변수로 제어됩니다.
# 예: payment-api (9081), backoffice-api (9080), backoffice-manage (9082)
# 모듈별로 다른 포트를 동적으로 설정
EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"] 