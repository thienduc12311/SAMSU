FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8081
COPY target/*.jar samsu.jar
COPY firebase-service-account.json /firebase-service-account.json
CMD ["java", "-jar", "/samsu.jar"]
