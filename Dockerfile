FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 hrms
COPY --from=build /workspace/target/java-hrms-1.0.0.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
