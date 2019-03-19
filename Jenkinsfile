@Library('dynatrace@master') _

pipeline {
  agent {
    label 'maven'
  }
  environment {
    APP_NAME = "customer-service"
    VERSION = readFile('version').trim()
    DOCKER_REPO = "${env.DOCKER_REGISTRY_URL}/${env.APP_NAME}"
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
        }
      }
    }
    stage('Docker push to registry'){
      steps {
        container('docker') {
          withDockerRegistry([ credentialsId: "registry-creds", url: "" ]) {
            sh "docker push ${env.DOCKER_REPO}:${env.TAG}"
          }
        }
      }
    }
  }
}
