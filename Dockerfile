FROM maven:latest

WORKDIR /app

COPY . .

COPY pom.xml .
RUN mvn clean install -DskipTests
<<<<<<< HEAD
EXPOSE 8080
=======

EXPOSE 8080

>>>>>>> dd7dd1b (Update Dockerfile)
CMD ["mvn", "spring-boot:run"]
