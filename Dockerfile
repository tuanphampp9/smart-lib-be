# Sử dụng image OpenJDK
FROM openjdk:17-jdk-slim

# Tạo thư mục app trong container
WORKDIR /app

# Copy file jar vào container
COPY build/libs/smart-lib-be-0.0.1-SNAPSHOT.jar app.jar

# Expose port (nên match với port của Spring Boot)
EXPOSE 8080

# Command để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
