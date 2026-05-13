FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY auth-service/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
