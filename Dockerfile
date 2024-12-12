FROM openjdk:17

COPY target/KidsCare.jar kidsCare.jar

ENTRYPOINT ["java", "-jar", "/kidsCare.jar"]