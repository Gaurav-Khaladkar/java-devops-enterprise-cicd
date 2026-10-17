# ==============================================================================
# Multi-Stage Production Dockerfile for Enterprise Java DevOps Application
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build & Package with Maven
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-17-noble AS builder

WORKDIR /build

# Copy Maven descriptor and download dependencies (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Compile, test, and package production WAR
RUN mvn clean package -DskipTests=false

# ------------------------------------------------------------------------------
# Stage 2: Hardened Runtime Container with Apache Tomcat
# ------------------------------------------------------------------------------
FROM tomcat:9.0-jdk17-temurin-jammy AS runner

LABEL maintainer="Enterprise DevOps Engineering Team" \
      version="1.0.0" \
      description="Production Enterprise Java WAR Application"

# Remove default insecure Tomcat web applications
RUN rm -rf /usr/local/tomcat/webapps/* \
    && rm -rf /usr/local/tomcat/webapps.dist

# Copy compiled artifact from build stage
COPY --from=builder /build/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Create unprivileged application user for zero-trust container security
RUN groupadd -g 1001 devopsgroup && \
    useradd -u 1001 -g devopsgroup -s /bin/sh -m devopsuser && \
    chown -R devopsuser:devopsgroup /usr/local/tomcat

# Set container environment flags
ENV JAVA_OPTS="-Djava.awt.headless=true -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom" \
    CATALINA_OPTS="-Xms256m -Xmx512m" \
    PORT=8080

USER 1001

EXPOSE 8080

# Kubernetes & Docker Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

ENTRYPOINT ["catalina.sh", "run"]
