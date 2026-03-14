# Estágio 1: Build (Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven para cachear dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e gera o executável
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução (JRE mais leve)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que vimos no seu application.yaml
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]