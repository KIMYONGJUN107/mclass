pipeline {
    agent any

    tools {
        // Jenkins Global Tool Configuration에 등록된 이름
        maven 'maven 3.9.11'
        jdk   'JAVA_HOME'
    }

    environment {
        DOCKER_IMAGE       = "demo-app"
        CONTAINER_NAME     = "springboot-container"
        JAR_FILE_NAME      = "app.jar"
        PORT               = "8081"

        REMOTE_USER        = "ec2-user"
        REMOTE_HOST        = "3.36.75.248"
        REMOTE_DIR         = "/home/ec2-user/deploy"
        SSH_CREDENTIALS_ID = "0cd8d549-5d40-4b2d-b75e-aca40bc78520"
    }

    stages {
        stage('Git Checkout') {
            steps {
                echo "Checking out from Git..."
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                echo "Building with Maven..."
                sh 'chmod +x ./mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Copy Artifacts to Server') {
            steps {
                echo "Copying artifacts to remote server..."
                sshagent(credentials: [SSH_CREDENTIALS_ID]) {
                    script {
                        def builtJars = findFiles(glob: 'target/*.jar')
                        if (builtJars.length == 0) {
                            error "No JAR file found in target/"
                        }
                        def builtJar = builtJars[0].path
                        echo "Found built JAR: ${builtJar}"

                        // 원격 서버 디렉토리 생성
                        sh "ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} 'mkdir -p ${REMOTE_DIR}'"

                        // JAR & Dockerfile 전송
                        sh "scp -o StrictHostKeyChecking=no ${builtJar} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/${JAR_FILE_NAME}"
                        sh "scp -o StrictHostKeyChecking=no Dockerfile ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/"
                    }
                }
            }
        }

        stage('Remote Docker Build & Deploy') {
            steps {
                echo "Building and deploying Docker container on remote server..."
                sshagent(credentials: [SSH_CREDENTIALS_ID]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} '
                            cd ${REMOTE_DIR} || exit 1

                            echo "Stopping and removing old container..."
                            docker rm -f ${CONTAINER_NAME} || true

                            echo "Building new Docker image..."
                            docker build -t ${DOCKER_IMAGE} .

                            echo "Running new container..."
                            docker run -d --name ${CONTAINER_NAME} -p ${PORT}:${PORT} ${DOCKER_IMAGE}
                        '
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up workspace..."
            cleanWs()
        }
    }
}
