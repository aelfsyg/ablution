FROM openjdk:18

ADD target/backend.jar /srv/backend.jar

EXPOSE 6003/tcp

CMD ["java", "-jar", "/srv/backend.jar"]
