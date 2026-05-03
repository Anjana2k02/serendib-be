# ---- Stage 1: Download dependencies (cached as long as pom.xml doesn't change) ----
FROM eclipse-temurin:17-jdk-alpine AS deps
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# ---- Stage 2: Build ----
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY --from=deps /root/.m2 /root/.m2
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests -q

# ---- Stage 3: Run (JRE only — smaller image) ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} --server.address=0.0.0.0 --server.port=${PORT:-8080}"]
