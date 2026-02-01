pipeline {
    agent any

    tools {
        maven 'Maven_3.8.5'
    }

    environment {
        APP_NAME   = "spring-k8-demo"
        IMAGE_NAME = "spring-k8-demo:latest"
        K8S_NS     = "default"
    }

    stages {

        /* ============================
           1. CI – Checkout Source Code
           ============================ */
        stage('1. CI - Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'git-creds',
                    url: 'https://github.com/rajaraok9/cicd-k8-demo-git.git'
            }
        }

        /* ============================
           2. CI – Build & Test
           ============================ */
        stage('2. CI - Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        /* ============================
           3. CI – Build Docker Image
           (Local Docker daemon via Jib)
           ============================ */
        stage('3. CI - Build Docker Image') {
             environment {
                 DOCKER_HOST = 'unix:///var/run/docker.sock'
             }
             steps {
                 sh '''
                   echo "Docker info:"
                   docker version

                   mvn jib:dockerBuild \
                     -Djib.to.image=spring-k8-demo
                 '''
             }
         }

        /* ============================
           4. CD – Deploy to KIND Kubernetes
           ============================ */
        stage('4. CD - Deploy to Kubernetes (KIND)') {
            steps {
                sh """
                  echo "Using Kubernetes context:"
                  kubectl config current-context

                  echo "Applying Deployment..."
                  kubectl apply -f k8s/deployment.yaml

                  echo "Applying Service..."
                  kubectl apply -f k8s/service.yaml

                  echo "Waiting for rollout..."
                  kubectl rollout status deployment/${APP_NAME} -n ${K8S_NS}
                """
            }
        }
    }

    post {
        success {
            echo "CI/CD Pipeline completed successfully 🚀"
        }
        failure {
            echo "Pipeline failed ❌ – check logs above"
        }
    }
}
