pipeline {
    agent any

    tools {
        maven 'Maven_3.8.5'
        jdk 'jdk17'
    }

    environment {
        APP_NAME   = "spring-demo-deployment"
        K8S_NS     = "default"
        REGISTRY   = "docker.io/rajadocker2109"
        IMAGE_NAME = "spring-k8-demo"
        IMAGE_TAG  = "latest"
        FULL_IMAGE = "${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
    }

    stages {
        stage('1. Checkout Source') {
            steps {
                git branch: 'main',
                    credentialsId: 'git-creds',
                    url: 'https://github.com/rajaraok9/cicd-k8-demo-git.git'
            }
        }

        stage('2. Build & Unit Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('3. Build & Push Docker Image (Jib)') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                      mvn jib:build \
                        -Djib.to.image=${FULL_IMAGE} \
                        -Djib.to.auth.username=${DOCKER_USER} \
                        -Djib.to.auth.password=${DOCKER_PASS} \
                        -Djib.from.auth.username=${DOCKER_USER} \
                        -Djib.from.auth.password=${DOCKER_PASS}
                    """
                }
            }
        }

        stage('4. Deploy to Kubernetes (KIND)') {
            steps {
                // --- THIS IS THE CRITICAL ADDITION ---
                // Ensure 'k8s-config' matches the ID you gave the Secret File in Jenkins Credentials
                withKubeConfig([credentialsId: 'k8s-config']) {
                    sh """
                      echo "Deploying ${FULL_IMAGE} to Kubernetes..."

                      # Apply manifests
                      kubectl apply -f k8s/deployment.yaml
                      kubectl apply -f k8s/service.yaml

                      # Check status (using --insecure-skip-tls-verify if using host IP)
                      kubectl rollout status deployment/${APP_NAME} -n ${K8S_NS}
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ CI/CD Pipeline completed successfully!"
        }
        failure {
            echo "❌ CI/CD Pipeline failed. Check logs above."
        }
    }
}