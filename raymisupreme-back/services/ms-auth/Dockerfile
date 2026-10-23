FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src
ENV MAVEN_OPTS="-Xmx512m -Xms128m -XX:MaxMetaspaceSize=256m"
RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests -q \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=30

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8189
ENTRYPOINT ["java", "-jar", "app.jar"]
