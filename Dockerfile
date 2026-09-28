# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Cache Maven dependencies
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn -B -ntp -DskipTests package


# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

ENV TZ=Asia/Kolkata

# Run application as non-root user
RUN groupadd --system spring \
    && useradd --system --gid spring --create-home spring

WORKDIR /app

# Copy generated JAR
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring

EXPOSE 8080

CMD ["java", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:+ExitOnOutOfMemoryError", \
     "-Duser.timezone=Asia/Kolkata", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", "app.jar"]