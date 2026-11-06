# Estágio 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .
WORKDIR /app/backend/backend
RUN mvn clean install -DskipTests

# Estágio 2: Imagem de execução leve
FROM eclipse-temurin:25-jre
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/backend/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]