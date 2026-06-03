FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies --no-daemon

COPY lombok.config .
COPY src src

RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]