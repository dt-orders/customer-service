@Library('dynatrace@master') _

pipeline {
  agent {
    label 'maven'
  }
  environment {
    APP_NAME = "customer"
    VERSION = readFile('version').trim()
    DOCKER_REPO = "${env.DOCKER_REGISTRY_URL}/${env.APP_NAME}"
    DEV_TAG = "${env.VERSION}-${env.BUILD_NUMBER}"
    TAG = "${env.VERSION}"
  }
  stages {
    stage('Maven build') {
      steps {
        checkout scm
        container('maven') {
          sh 'unset MAVEN_CONFIG && env && ./mvnw clean package -Dmaven.test.skip=true'
        }
      }
    }
    stage('Docker build') {
      steps {
        container('docker') {
          sh "docker build -t ${env.DOCKER_REPO} ."
          sh "docker tag ${env.DOCKER_REPO} ${env.DOCKER_REPO}:${env.TAG}"
          sh "docker tag ${env.DOCKER_REPO} ${env.DOCKER_REPO}:${env.DEV_TAG}"
        }
      }
    }
    stage('Docker push to registry'){
      steps {
        container('docker') {
          withCredentials([usernamePassword(credentialsId: 'acm-demo-app-cicd-user', passwordVariable: 'TOKEN', usernameVariable: 'USER')]) {
            sh "docker login --username=${USER} --password=${TOKEN} https://${env.DOCKER_REGISTRY_URL}"
            sh "docker push ${env.DOCKER_REPO}:${env.TAG}"
            sh "docker push ${env.DOCKER_REPO}:${env.DEV_TAG}"
          }
        }
      }
    }
  }
}
