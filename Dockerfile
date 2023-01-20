FROM openjdk:11
COPY target/*.jar .
COPY target/MANIFEST .

# rookout java SDK - also added to the CMD line below
COPY app/rook.jar .

# used to set the problem pattern
ARG APP_VERSION=1
ENV APP_VERSION=$APP_VERSION

# used in the list problem pattern
ARG SLEEP_TIME=1000
ENV SLEEP_TIME=$SLEEP_TIME

EXPOSE 8080

CMD ["sh", "-c", "/usr/local/openjdk-11/bin/java -Xmx400m -Xms400m -javaagent:rook.jar -jar *.jar"]
