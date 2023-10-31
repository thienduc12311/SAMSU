FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8081
COPY target/*.jar samsu.jar
CMD ["java", "-jar", "/samsu.jar"]
