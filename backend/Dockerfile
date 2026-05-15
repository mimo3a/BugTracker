# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY backend/pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY backend/src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring

COPY --from=build /app/target/*.jar /app/app.jar

USER spring:spring
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]

