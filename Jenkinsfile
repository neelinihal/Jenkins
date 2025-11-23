pipeline {
  agent any

  environment {
    REPO_URL     = 'https://github.com/neelinihal/Jenkins.git'
    GIT_BRANCH   = 'main'                       // change if you use a different branch
    MODULE_DIR   = '.'                          // update if your code is in a subfolder
    IMAGE_NAME   = 'neelinihal/jservice'
    IMAGE_TAG    = "build-${env.BUILD_NUMBER}"  // you can switch to commit SHA later
    DOCKER_CREDS = 'dockerhub-creds'
    KUBE_NS      = 'default'
    DEPLOY_NAME  = 'jservice'                   // your Kubernetes Deployment name
    CONTAINER    = 'jservice'                   // container name inside the Deployment
  }

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout([$class: 'GitSCM',
          branches: [[name: "*/${GIT_BRANCH}"]],
          userRemoteConfigs: [[url: REPO_URL]]
        ])
      }
    }

    stage('Build (Maven)') {
      steps {
        dir(MODULE_DIR) {
          bat 'mvn -version'
          bat 'mvn -Dmaven.test.failure.ignore=false clean package'
        }
      }
    }

    stage('Docker build') {
      steps {
        dir(MODULE_DIR) {
          bat "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
        }
      }
    }

    stage('Docker login & push') {
      steps {
        withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDS}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          bat 'docker logout || ver > nul'
          bat 'docker login -u %DOCKER_USER% -p %DOCKER_PASS%'
        }
        bat "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
      }
    }

    stage('Deploy to GKE') {
      steps {
        // Option A: rolling update of an existing Deployment
        bat "kubectl set image deployment/${DEPLOY_NAME} ${CONTAINER}=${IMAGE_NAME}:${IMAGE_TAG} -n ${KUBE_NS} || ver > nul"
        bat "kubectl rollout status deployment/${DEPLOY_NAME} -n ${KUBE_NS}"

        // Option B: apply manifests (preferred if you store YAML in repo)
        // bat 'kubectl apply -f k8s/jservice.yaml -n %KUBE_NS%'
      }
    }
  }

  post {
    success { echo "Pushed ${IMAGE_NAME}:${IMAGE_TAG} and deployed to ${KUBE_NS}." }
    failure { echo "Pipeline failed. Check stage logs." }
  }
}
