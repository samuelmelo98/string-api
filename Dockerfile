# ==================================================
# BUILD
# ==================================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src src

RUN mvn -B clean package -DskipTests

# ==================================================
# RUNTIME
# ==================================================
FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && apt-get install -y \
    libglib2.0-0 \
    libnspr4 \
    libnss3 \
    libdbus-1-3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libatspi2.0-0 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libxcb1 \
    libxkbcommon0 \
    libasound2 \
    libdrm2 \
    libxshmfence1 \
    libgtk-3-0 \
    wget \
    unzip \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Diretório dos browsers Playwright
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

# Instala Chromium do Playwright
RUN mkdir -p /ms-playwright

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75", \
    "-jar", \
    "app.jar"]