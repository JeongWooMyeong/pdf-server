pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "jeongwoomyeong/pdf-server"
        DOCKER_TAG   = "${env.BUILD_NUMBER}"
        KUBECONFIG   = "C:\\Users\\jwm90\\.kube\\config"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/JeongWooMyeong/pdf-server',
                    credentialsId: 'github-token'

                bat 'git clean -fdx'
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew.bat clean build'
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-crendentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    bat 'docker login -u %DOCKER_USER% -p %DOCKER_PASS%'

                    bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."

                    bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withEnv(["KUBECONFIG=${env.KUBECONFIG}"]) {
                    bat """
                        kubectl apply -f C:\\Project\\erp_server\\k8s\\pdf-deployment.yaml
                        kubectl apply -f C:\\Project\\erp_server\\k8s\\pdf-service.yaml

                        kubectl set image deployment/pdf-server-deployment pdf-server=${DOCKER_IMAGE}:${DOCKER_TAG}

                        kubectl rollout status deployment/pdf-server-deployment
                    """
                }
            }
        }
    }
}
