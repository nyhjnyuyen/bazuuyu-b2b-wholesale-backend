FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY wholesale-service/target/wholesale-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
