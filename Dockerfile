FROM openjdk:14
COPY target/*.jar .
COPY target/MANIFEST .

# used to set the problem pattern
ARG APP_VERSION=1
ENV APP_VERSION=$APP_VERSION
# optional value
ENV LAUNCH_DARKLY_SDK_KEY=$LAUNCH_DARKLY_SDK_KEY
 
EXPOSE 8080

CMD ["sh", "-c", "cat MANIFEST && /usr/bin/java -Xmx400m -Xms400m -jar *.jar"]