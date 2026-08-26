FROM gradle:8.5-jdk21 AS build

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon

COPY . .

RUN ./gradlew bootJar -x test --no-daemon


FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 9999

ENTRYPOINT ["java", "-jar", "app.jar"]