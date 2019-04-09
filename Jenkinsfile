@Library('dynatrace@master') _

pipeline {
  agent {
    label 'maven'
  }  
  environment {
    SERVICE_NAME = "customer-service"
    VERSION = readFile('version').trim()
    ARTEFACT_ID = "orders-registry/" + "${env.SERVICE_NAME}"
    TAG = "${env.DOCKER_REGISTRY_URL}:5000/${env.ARTEFACT_ID}"
    TAG_DEV = "${env.TAG}:${env.VERSION}-${env.BUILD_NUMBER}"
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
          sh "docker build -t ${env.TAG_DEV} . --no-cache"
        }
      }
    }
    stage('Docker push to registry'){
      steps {
        container('docker') {
          sh "docker push ${env.TAG_DEV}"
        }
      }
    }
  }
}
