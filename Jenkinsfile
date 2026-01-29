pipeline {
    agent any
    tools {
        maven 'Maven_3.8.5'
    }
    environment {
        APP_NAME   = "spring-demo"
        REGISTRY   = "docker.io/rajadocker2109"
        IMAGE_NAME = "${REGISTRY}/${APP_NAME}"
        IMAGE_TAG  = "latest"
    }
    stages {
        stage('1. CI - Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'git-creds',
                    url: 'https://github.com/rajaraok9/cicd-demo-git.git'
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
                      mvn jib:dockerBuild \
                        -Djib.to.image=${IMAGE_NAME} \
                        -Djib.to.tags=${IMAGE_TAG} \
                        -Djib.to.auth.username=${DOCKER_USER} \
                        -Djib.to.auth.password=${DOCKER_PASS}
                    """
                }
            }
        }
        stage('4. CD - Deploy Locally') {
            steps {
                // Use double quotes "" so Groovy can interpolate the ${APP_NAME} variables
                sh "docker rm -f ${APP_NAME} || true"
                sh "docker run -d --name ${APP_NAME} -p 8081:8080 ${IMAGE_NAME}:${IMAGE_TAG}"
                echo "Success! Application is available at http://localhost:8081"
            }
        }
    }
}