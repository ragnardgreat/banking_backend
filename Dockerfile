# Build stage
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

USER root
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
