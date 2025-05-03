FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN ./gradlew bootJar

EXPOSE 8081

CMD ["java", "-jar", "build/libs/books-0.0.1-SNAPSHOT.jar"]