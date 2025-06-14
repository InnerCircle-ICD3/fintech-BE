FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :backoffice-api:build -x test

FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/backoffice-api/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 