FROM maven:3-eclipse-temurin-17-alpine

WORKDIR /app

COPY pom.xml .
RUN ["mvn", "dependency:resolve"]

COPY . .
RUN ["mvn", "package", "-Dmaven.test.skip=true"]

CMD ["mvn", "liquibase:update", "-Pdocker"]
CMD ["java", "-jar", "target/mock-1.0.0.jar"]
