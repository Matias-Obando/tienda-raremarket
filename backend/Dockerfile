FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only backend to keep context small
COPY backend /workspace/backend

WORKDIR /workspace/backend
RUN mvn -B -f pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/backend/target/backend-0.0.1-SNAPSHOT.jar /app/app.jar
ENV PORT=8081
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]