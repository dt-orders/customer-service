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
  ./mvnw clean package
  java -jar target/*.jar
  ```
2. access application at ```http://localhost:8080```
