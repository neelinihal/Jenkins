pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  environment {
    IMAGE_NAME   = 'neelinihal/jservice'
    IMAGE_TAG    = "build-${BUILD_NUMBER}"
    DOCKER_CREDS = 'dockerhub-creds'
    KUBE_CREDS   = 'kubeconfig-local'
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build App') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
      }
    }

    stage('Push to Docker Hub') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: "${DOCKER_CREDS}",
          usernameVariable: 'USER',
          passwordVariable: 'PASS'
        )]) {
          sh '''
            echo $PASS | docker login -u $USER --password-stdin
            docker push $IMAGE_NAME:$IMAGE_TAG
          '''
        }
      }
    }

    stage('Deploy to LOCAL k3s') {
      steps {
        withCredentials([file(credentialsId: "${KUBE_CREDS}", variable: 'KUBECONFIG')]) {
          sh '''
            sed -i "s|IMAGE_TAG|$IMAGE_TAG|g" k8s/jservice.yaml
            kubectl apply -f k8s/
            kubectl rollout status deployment/jservice
          '''
        }
      }
    }
  }
}

