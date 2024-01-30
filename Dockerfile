FROM openjdk:18
ADD target/users-app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
