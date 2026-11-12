# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY ../../backend/pom.xml .
COPY ../../backend/src ./src
RUN mvn clean package -Dmaven.test.skip

# Stage 2: Run
FROM openjdk:26-ea-21-jdk-oraclelinux9
WORKDIR /backend
COPY --from=build /build/target/artilleryonline-0.9.0.jar artilleryonline.jar
CMD ["java", "-jar", "artilleryonline.jar"]
