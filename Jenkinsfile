pipeline {
    agent any
    tools {
        maven 'Maven_3.8.5'
    }
    environment {
        APP_NAME   = "spring-demo-k8"
        REGISTRY   = "docker.io/rajadocker2109"
        IMAGE_NAME = "${REGISTRY}/${APP_NAME}"
        IMAGE_TAG  = "latest"
         KUBECONFIG = "/root/.kube/config"
    }
    stages {
        stage('1. CI - Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'git-creds',
                    url: 'https://github.com/rajaraok9/cicd-k8-demo-git.git'
            }
        }
        stage('2. CI - Build & Test') {
            steps {
                // Combined for speed in a foundation session
                sh 'mvn clean compile test'
            }
        }
        stage('3. CI - Build Image (Jib)') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                      # Use dockerBuild so the image is available for the next 'docker run' stage
                      mvn jib:build \
                        -Djib.to.image=${IMAGE_NAME} \
                        -Djib.to.tags=${IMAGE_TAG} \
                        -Djib.to.auth.username=${DOCKER_USER} \
                        -Djib.to.auth.password=${DOCKER_PASS}
                    """
                }
            }
        }
        stage('4. CD - Deploy to Kubernetes') {
                    steps {
                        // Replacing 'docker run' with 'kubectl apply'
                        // envsubst injects the ${IMAGE_TAG} into your deployment.yaml
                        sh "envsubst < k8s/deployment.yaml | kubectl apply -f -  --validate=false"
                        sh "kubectl apply -f k8s/service.yaml"

                        echo "Success! The Conductor (K8s) is now managing the Java Band."
                    }
                }
    }
}