FROM maven:latest

WORKDIR /app

COPY . .

COPY pom.xml .
RUN mvn clean install -DskipTests
EXPOSE 8080
CMD ["mvn", "spring-boot:run"]
