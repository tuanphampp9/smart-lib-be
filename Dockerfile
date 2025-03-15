# Sử dụng image của Gradle để build
FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test --no-daemon

# Sau đó copy file jar đã build vào container
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]