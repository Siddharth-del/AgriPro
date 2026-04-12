# ─── Stage 1: Build Spring Boot ───────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ─── Stage 2: Final image (Java + Python) ─────────────────────
FROM eclipse-temurin:21-jre

# Install Python + OpenCV system deps
RUN apt-get update && apt-get install -y \
    python3 python3-pip \
    libgl1 libglib2.0-0 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Set up Python ML service
WORKDIR /ml
COPY ML-Training/ .
RUN pip3 install --no-cache-dir --break-system-packages -r requirements.txt

# Copy Spring Boot jar
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

COPY start.sh .
RUN chmod +x start.sh

EXPOSE 8080
ENTRYPOINT ["./start.sh"]