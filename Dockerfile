
# ==================================================
# BUILD - JAVA 25
# ==================================================
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn -B dependency:go-offline

COPY src ./src

RUN mvn -B clean package -DskipTests -Pprod


# ==================================================
# RUNTIME - JAVA 25
# ==================================================
FROM eclipse-temurin:25-jre-jammy

ARG APP_VERSION=dev
ENV APP_VERSION=${APP_VERSION}

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright
ENV SPRING_PROFILES_ACTIVE=prod

RUN mkdir -p /ms-playwright

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75", \
    "-jar", \
    "app.jar"]
