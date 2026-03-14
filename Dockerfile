# Estágio 1: Build (Maven com Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia arquivos e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia código e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução (JRE 21)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]