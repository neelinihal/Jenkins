pipeline {
  agent any

  environment {
    REPO_URL     = 'https://github.com/neelinihal/Jenkins.git'
    GIT_BRANCH   = 'main'
    MODULE_DIR   = '.'                          // project root
    IMAGE_NAME   = 'neelinihal/jservice'
    IMAGE_TAG    = "build-${env.BUILD_NUMBER}"  // tag per build
    DOCKER_CREDS = 'dockerhub-creds'
    KUBE_NS      = 'default'
    DEPLOY_NAME  = 'jservice'
    CONTAINER    = 'jservice'
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
          bat 'mvn clean package -Dmaven.test.failure.ignore=false'
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
        // Preferred: apply manifest stored in repo
        //bat "kubectl apply -f %WORKSPACE%\\k8s\\jservice.yaml -n ${KUBE_NS} --timeout=200s --validate=false"
        bat "kubectl --kubeconfig=C:\Users\neeli\.kube\config apply -f %WORKSPACE%\\k8s\\jservice.yaml -n ${KUBE_NS} --time=200s"
        bat "kubectl rollout status deployment/${DEPLOY_NAME} -n ${KUBE_NS} --timeout=200s "
      }
    }
  }

  post {
    success { echo "✅ Pushed ${IMAGE_NAME}:${IMAGE_TAG} and deployed to ${KUBE_NS}." }
    failure { echo "❌ Pipeline failed. Check stage logs." }
  }
}
