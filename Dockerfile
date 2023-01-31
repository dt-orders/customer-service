FROM openjdk:11
COPY target/MANIFEST .
COPY target/*.jar .

# used to set the problem pattern
ARG APP_VERSION=1
ENV APP_VERSION=$APP_VERSION

# used in the list problem pattern
ARG SLEEP_TIME=1000
ENV SLEEP_TIME=$SLEEP_TIME

# https://docs.rookout.com/docs/java-container-tutorial
# rookout to sync code commit and java debugger SDK
COPY .git /.git
RUN curl -L "https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.rookout&a=rook&v=LATEST" -o ${pwd}/rook.jar

EXPOSE 8080

CMD ["sh", "-c", "/usr/local/openjdk-11/bin/java -Xmx400m -Xms400m -javaagent:rook.jar -jar dt-orders-customer-service-2.2.6.RELEASE.jar"]
