FROM openjdk:14
COPY target/*.jar .

# used to set the problem pattern
ARG APP_VERSION=1
ENV APP_VERSION=$APP_VERSION

CMD /usr/bin/java -Xmx400m -Xms400m -jar *.jar 
EXPOSE 8080