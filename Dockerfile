FROM openjdk:11.0.8-jre-slim

COPY target/*.jar .
COPY version .
CMD java -Xmx600m -Xms600m -jar *.jar 
EXPOSE 8080

