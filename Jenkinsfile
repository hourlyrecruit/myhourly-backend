 pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-2'
        AWS_ACCOUNT_ID = '683091268235'
        ECR_REPOSITORY = 'myhourly'
        ECR_REGISTRY = '683091268235.dkr.ecr.ap-south-2.amazonaws.com'
        IMAGE_NAME = '683091268235.dkr.ecr.ap-south-2.amazonaws.com/myhourly'
        CONTAINER_NAME = 'myhourly-backend'
        SECRET_NAME = 'Myhourly/application'
        HOST_PORT = '8081'
        CONTAINER_PORT = '8080'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build \
                      -t ${IMAGE_NAME}:${BUILD_NUMBER} \
                      -t ${IMAGE_NAME}:latest \
                      .
                '''
            }
        }

        stage('Login to ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region ${AWS_REGION} |
                    docker login --username AWS --password-stdin ${ECR_REGISTRY}
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh '''
                    docker push ${IMAGE_NAME}:${BUILD_NUMBER}
                    docker push ${IMAGE_NAME}:latest
                '''
            }
        }

        stage('Get Secrets from AWS') {
            steps {
                sh '''
                    set -e

                    ENV_FILE="/tmp/myhourly-${BUILD_NUMBER}.env"

                    aws secretsmanager get-secret-value \
                      --secret-id "${SECRET_NAME}" \
                      --region "${AWS_REGION}" \
                      --query SecretString \
                      --output text |
                    python3 -c 'import json,sys; d=json.load(sys.stdin); print("\\n".join(f"{k}={v}" for k,v in d.items()))' \
                      > "$ENV_FILE"

                    chmod 600 "$ENV_FILE"

                    echo "Secrets retrieved successfully."
                '''
            }
        }

        stage('Deploy Backend') {
            steps {
                sh '''
                    set -e

                    ENV_FILE="/tmp/myhourly-${BUILD_NUMBER}.env"

                    docker rm -f ${CONTAINER_NAME} 2>/dev/null || true

                    docker run -d \
                      --name ${CONTAINER_NAME} \
                      --restart unless-stopped \
                      --env-file "$ENV_FILE" \
                      -p ${HOST_PORT}:${CONTAINER_PORT} \
                      ${IMAGE_NAME}:${BUILD_NUMBER}

                    rm -f "$ENV_FILE"

                    echo "Backend container deployed successfully."
                '''
            }
        }

        stage('Verify Container') {
            steps {
                sh '''
                    sleep 10
                    docker ps --filter "name=${CONTAINER_NAME}"
                '''
            }
        }
    }

    post {
        always {
            sh '''
                rm -f /tmp/myhourly-*.env 2>/dev/null || true
            '''
        }

        success {
            echo 'Myhourly deployment completed successfully.'
        }

        failure {
            echo 'Myhourly deployment failed. Check the Jenkins console output.'
        }
    }
}
