# Multi-stage Dockerfile for Spring Boot TodoListService
# Stage 1: BUILD
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Create non-root user for build process
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --chown=appuser:appgroup gradle/ gradle/
COPY --chown=appuser:appgroup gradlew build.gradle settings.gradle ./

COPY --chown=appuser:appgroup config/ config/

# Download dependencies (this layer will be cached unless build files change)
RUN ./gradlew dependencies --no-daemon
COPY --chown=appuser:appgroup src/ src/
RUN ./gradlew bootJar --no-daemon && \
    java -Djarmode=layertools -jar build/libs/*.jar list > layers.txt && \
    java -Djarmode=layertools -jar build/libs/*.jar extract

# Stage 2: RUNTIME
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copy layers from build stage (optimized for Docker layer caching)
COPY --from=builder --chown=appuser:appgroup /app/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /app/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/application/ ./

RUN mkdir -p /app/data /app/logs && \
    chown -R appuser:appgroup /app

USER appuser:appgroup

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=docker"

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]

CMD ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

LABEL maintainer="amit" \
      version="0.0.1-SNAPSHOT" \
      description="TodoListService" \
      org.opencontainers.image.source="https://github.com/tangibleideas/TodoListService" \
      org.opencontainers.image.title="TodoListService" \
      org.opencontainers.image.description="Spring Boot Todo List Service" \
      org.opencontainers.image.version="0.0.1-SNAPSHOT"
