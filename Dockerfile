FROM openjdk:14
COPY target/*.jar .
COPY target/MANIFEST .

# used to set the problem pattern
ARG APP_VERSION=1
ENV APP_VERSION=$APP_VERSION

# used in the list problem pattern
ARG SLEEP_TIME=1000
ENV SLEEP_TIME=$SLEEP_TIME

# optional value
ENV LAUNCH_DARKLY_SDK_KEY=$LAUNCH_DARKLY_SDK_KEY
 
EXPOSE 8080

CMD ["sh", "-c", "cat MANIFEST && /usr/bin/java -Xmx400m -Xms400m -jar *.jar"]