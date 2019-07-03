# Overview

This repo has code for a Java spring-boot micro service.

Demo app credits go to: https://github.com/ewolff/microservice-kubernetes

The application is built and run using Jenkins.  See ```Jenkinsfile``` in this repo.

This application will be packaged in a Docker image.  See ```Dockerfile``` in this repo

# Developer Notes

## Pre-requisites

The following programs to be installed
* Java 1.8
* Maven
* IDE such as VS Code

## Build and Run Locally

1. run these commands
  ```
  ./mvnw clean package -Dmaven.test.skip=true
  java -jar target/*.jar
  ```
2. access application at ```http://localhost:8080```

# Utilities

## 1. quicktest

unix shell script that loops and calls the app URL.  Just call:

```./quicktest.sh <customer base url>```

For example:

```./quicktest.sh http://localhost:8080```

## 2. quickbuild

unix shell script that builds and pushes docker image named: keptn-orders-customer-service:tag.  Just call:

```./quickbuild.sh <registry> <tag>```

For example:

```./quickbuild.sh robjahn 1```