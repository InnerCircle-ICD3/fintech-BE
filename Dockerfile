# 1단계: 빌드용 이미지
FROM gradle:8.5-jdk21-alpine as builder
WORKDIR /app

# 전체 소스 복사
COPY . .

# 모듈 지정하여 빌드
ARG MODULE
RUN gradle clean ${MODULE}:test ${MODULE}:build --no-daemon

# 2단계: 실행용 이미지
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG MODULE
# 명확한 파일명 패턴 사용
COPY --from=builder /app/${MODULE}/build/libs/${MODULE}-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

