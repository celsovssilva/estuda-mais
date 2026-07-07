
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app


COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]