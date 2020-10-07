# Overview

This repo has code for a Java spring-boot micro service.

Demo app credits go to: https://github.com/ewolff/microservice-kubernetes

This application will be packaged in a Docker image.  See ```Dockerfile``` in this repo

# Developer Notes

## Pre-requisites

The following programs to be installed
* Java 14
* Maven
* IDE such as VS Code

## Build and Run Locally

1. run these commands
  ```
  ./mvnw clean package -Dmaven.test.skip=true
  export APP_VERSION=1 && java -jar target/*.jar
  ```
  
2. access application at ```http://localhost:8080```

## Build Docker Image

Run unix shell script that builds and pushes docker images with multiple tags

```./buildpush.sh <registry>```

For example:

```./quickbuild.sh dtdemos```

## Run Docker Image Locally

1. run these commands
  ```
  docker run -p 8080:8080 dtdemos/keptn-orders-customer-service:2
  ```

2. access application at ```http://localhost:8080```
