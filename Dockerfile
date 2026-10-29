# syntax=docker/dockerfile:1

# ---------- Stage 1: build the jar ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

# Pass --build-arg MVN_ARGS="" to run the unit tests during the build
ARG MVN_ARGS="-DskipTests"

COPY pom.xml .
COPY src ./src
# The cache mount keeps downloaded dependencies between builds (needs BuildKit, the default in current Docker)
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package ${MVN_ARGS}

# ---------- Stage 2: small runtime image ----------
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Run as an unprivileged user
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /build/target/*.jar /app/app.jar
USER app

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -Dfile.encoding=UTF-8" \
    SPRING_PROFILES_ACTIVE="" \
    TZ=UTC

EXPOSE 9095
ENTRYPOINT ["java", "-jar", "/app/app.jar"]