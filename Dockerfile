FROM maven:latest

WORKDIR /app

COPY . .

COPY pom.xml .
RUN mvn clean install -DskipTests

CMD ["mvn", "spring-boot:run"]