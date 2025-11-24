pipeline {
  agent any

  environment {
    REPO_URL     = 'https://github.com/neelinihal/Jenkins.git'
    GIT_BRANCH   = 'main'
    MODULE_DIR   = '.'
    IMAGE_NAME   = 'neelinihal/jservice'
    IMAGE_TAG    = "build-${env.BUILD_NUMBER}"
    DOCKER_CREDS = 'dockerhub-creds'
    KUBE_NS      = 'default'
    DEPLOY_NAME  = 'jservice'
    CONTAINER    = 'jservice'
    GCP_CREDS    = 'gcp-sa-json'   // Jenkins credential ID
    CLUSTER_NAME = 'my-cluster'
    CLUSTER_ZONE = 'us-central1'
    PROJECT_ID   = 'steel-earth-478506'
  }

  stages {
    stage('Authenticate GCP') {
      steps {
        withCredentials([file(credentialsId: "${GCP_CREDS}", variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
          bat "gcloud auth activate-service-account --key-file=%GOOGLE_APPLICATION_CREDENTIALS%"
          bat "gcloud config set project ${PROJECT_ID}"
          bat "gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${CLUSTER_ZONE} --project ${PROJECT_ID}"
        }
      }
    }

    stage('Deploy to GKE') {
      steps {
        bat "kubectl apply -f %WORKSPACE%\\k8s\\jservice.yaml -n ${KUBE_NS} --validate=false"
        bat "kubectl rollout status deployment/${DEPLOY_NAME} -n ${KUBE_NS} --timeout=200s"
      }
    }
  }
}
