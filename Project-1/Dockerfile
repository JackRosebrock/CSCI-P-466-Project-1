# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests=false test package

# ---- Run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create folder for SQLite db (important)
RUN mkdir -p /app/data

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["java","-jar","app.jar"]