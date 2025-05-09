# 1단계: 빌드용 이미지
FROM gradle:8.5-jdk21-alpine as builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 2단계: 실행용 이미지
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# payment-api 모듈에서 .jar 복사
COPY --from=builder /app/payment-api/build/libs/*SNAPSHOT.jar /app/app.jar


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

