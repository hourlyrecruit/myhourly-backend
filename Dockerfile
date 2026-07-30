# ---------- Build Stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre

# Set system timezone
ENV TZ=Asia/Kolkata

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Start Spring Boot with JVM timezone set to IST
CMD ["java", "-Duser.timezone=Asia/Kolkata", "-jar", "app.jar"]