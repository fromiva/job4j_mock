FROM maven:3-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN ["mvn", "dependency:resolve"]

COPY . .
RUN ["mvn", "package", "-Dmaven.test.skip=true"]

FROM eclipse-temurin:17-jre-alpine AS prod

WORKDIR /app
COPY --from=build /app/target/desc-1.0.0.jar desc.jar

CMD ["java", "-jar", "desc.jar"]
