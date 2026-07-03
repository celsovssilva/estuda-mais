# Estágio 1: Build da aplicação (Usando Maven com Java 21)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia os arquivos de dentro da estrutura correta das pastas
COPY ./backend/backend /app

# Executa o build onde está o pom.xml
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação (Usando Java 21)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]