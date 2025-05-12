# 1단계: 빌드용 이미지
FROM gradle:8.5-jdk21-alpine as builder
WORKDIR /app

# 의존성 캐싱을 위해 빌드 스크립트만 먼저 복사
COPY settings.gradle build.gradle ./
# 소스 복사
COPY . .

# 모듈 지정하여 빌드
ARG MODULE=payment-api
RUN gradle clean ${MODULE}:build -x test --no-daemon

# 2단계: 실행용 이미지
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG MODULE=payment-api
COPY --from=builder /app/${MODULE}/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

