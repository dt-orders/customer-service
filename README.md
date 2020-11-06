# Overview

This repo has the code for the customer service for demostrations.  See the [overview](https://github.com/dt-orders/overview) repo for an overiew for that whole application.

# Developer Notes

## Pre-requisites

The following programs to be installed
* Java 14
* Maven
* Docker

## Build and Run Locally

1. run these commands
  ```
  ./mvnw clean package -Dmaven.test.skip=true
  export APP_VERSION=1 && java -jar target/*.jar
  ```
  
2. access application at ```http://localhost:8080```

## Build Docker Images and push images to a repository

Use the provided Unix shell scipt that will build the docker image and publish it. There are three versions that will be built.

    Just call: `./buildpush.sh <REPOSITORY>`

    For example: `./buildpush.sh dtdemos`

## Run Docker Image Locally

1. Here is an example of running version 2
  ```
  docker run -p 8080:8080 dtdemos/dt-orders-customer-service:2
  ```

2. access application at ```http://localhost:8080```
