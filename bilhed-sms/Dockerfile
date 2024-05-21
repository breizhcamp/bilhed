FROM eclipse-temurin:21-jre-alpine
RUN mkdir /opt/app
COPY target/*.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar"]
