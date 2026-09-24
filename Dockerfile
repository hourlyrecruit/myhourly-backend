# ---------- Build Stage ----------
# Multi-stage: Maven + JDK 21 compile here, and only the fat JAR crosses into
# the runtime image, so Maven and the JDK never ship to production.
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the POM on its own first so the dependency layer is cached and only
# re-resolved when pom.xml changes. Without this, every code change re-downloads
# the whole dependency tree on the Droplet.
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

# Now the sources (src/main/resources holds the Flyway migrations and fonts).
COPY src ./src

# Tests run in the GitHub Actions CI stage before the image is built; skipping
# them here keeps the Droplet build fast and avoids needing a database inside the
# build container.
RUN mvn -B -ntp -DskipTests package


# ---------- Runtime Stage ----------
# eclipse-temurin:21-jre is based on ubuntu:24.04.
FROM eclipse-temurin:21-jre

# Payroll and attendance calculations are anchored to IST.
ENV TZ=Asia/Kolkata

# Non-root runtime user. ubuntu:24.04 has useradd/groupadd but not adduser/addgroup.
RUN groupadd --system spring \
 && useradd --system --gid spring --create-home spring

WORKDIR /app

# Explicit name (not *.jar) so a second artifact in target/ can never be picked up.
COPY --from=build --chown=spring:spring /app/target/my_hourly-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

# The base image already sets ENTRYPOINT ["/__cacert_entrypoint.sh"], which keeps
# the JDK truststore in sync and execs the command below - so the JVM stays PID 1
# and receives SIGTERM on `docker stop`. Extra JVM flags (heap, GC) can be supplied
# at runtime through JAVA_TOOL_OPTIONS, which the JVM reads natively; no shell needed.
CMD ["java", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:+ExitOnOutOfMemoryError", \
     "-Duser.timezone=Asia/Kolkata", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", "app.jar"]
