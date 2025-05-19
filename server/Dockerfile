FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew test --no-daemon
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/books-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]