FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar"]
