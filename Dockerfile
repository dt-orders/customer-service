FROM openjdk:10.0.2-jre-slim
COPY target/*.jar .
COPY version .
CMD /usr/bin/java -Xmx400m -Xms400m -jar *.jar 
EXPOSE 8080

