# Estágio 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS build
# Define o diretório de trabalho inicial
WORKDIR /app

# Copia todo o conteúdo do monorepo para dentro do container
COPY . .

# MUITO IMPORTANTE: Move o diretório de trabalho para onde o pom.xml realmente está antes de rodar o Maven
WORKDIR /app/backend/backend
# Estágio 1: Build da aplicação
RUN mvn clean install -DskipTests

# Estágio 2: Imagem de execução leve
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

EXPOSE 8080

# Copia o .jar gerado apontando para o caminho correto do estágio anterior
COPY --from=build /app/backend/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
