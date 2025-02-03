def ecrLoginHelper="docker-credential-ecr-login" // ECR credential helper 이름

pipeline {
    agent any // 어느 젠킨스 서버에서도 실행이 가능
    environment {
        REGION = "ap-northeast-2"
        ECR_URL = "054037133599.dkr.ecr.ap-northeast-2.amazonaws.com"
        SERVICE_DIRS = "config-service,gateway-service,user-service,calendar-service,chat-service,etc-service"
        K8S_REPO_URL = "https://github.com/FlixMad/orai_kubenetes.git"
        K8S_REPO_CRED = "github-k8s-repo-token" // GitHub 자격증명 ID
    }
    stages {
        stage('Pull Codes from Github'){
            steps {
                checkout scm
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true)
                        .trim()
                        .split('\n')
                    echo "Changed files: ${changedFiles}"

                    def changedServices = ["config-service","gateway-service","user-service","calendar-service","chat-service","etc-service"]
                    def serviceDirs = env.SERVICE_DIRS.split(",")
                    serviceDirs.each { service ->
                        if (changedFiles.any { it.startsWith(service + "/") }) {
                            changedServices.add(service)
                        }
                    }

                    env.CHANGED_SERVICES = changedServices.join(",")
                    if (env.CHANGED_SERVICES == "") {
                        echo "No changes detected in service directories. Skipping build and deployment."
                        currentBuild.result = 'SUCCESS'
                    }
                }
            }
        }

        stage('Build Changed Services') {
            when {
                expression { env.CHANGED_SERVICES != "" }
            }
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES.split(",")
                    changedServices.each { service ->
                        sh """
                            echo "Building ${service}..."
                            cd ${service}

                            # Gradle Wrapper 재생성
                            gradle wrapper --gradle-version 7.6  # 원하는 Gradle 버전으로 설정하세요.

                            chmod +x ./gradlew
                            ./gradlew clean build -x test
                            ls -al ./build/libs
                            cd ..
                        """
                    }
                }
            }
        }

        stage('Build Docker Image & Push to AWS ECR') {
            when {
                expression { env.CHANGED_SERVICES != "" }
            }
            steps {
                script {
                    withAWS(region: "${REGION}", credentials: "aws-key") {
                        def changedServices = env.CHANGED_SERVICES.split(",")
                        changedServices.each { service ->
                            def newTag = "0.0.1"
                            sh """
                                mkdir -p ~/.docker
                                curl -O https://amazon-ecr-credential-helper-releases.s3.us-east-2.amazonaws.com/0.4.0/linux-amd64/${ecrLoginHelper}
                                chmod +x ${ecrLoginHelper}
                                mv ${ecrLoginHelper} /usr/local/bin/

                                echo '{"credHelpers": {"${ECR_URL}": "ecr-login"}}' > ~/.docker/config.json

                                docker build -t ${service}:${newTag} ${service}
                                docker tag ${service}:${newTag} ${ECR_URL}/${service}:${newTag}
                                docker push ${ECR_URL}/${service}:${newTag}
                            """
                        }
                    }
                }
            }
        }

        stage('Update k8s Repo') {
            when {
                expression { env.CHANGED_SERVICES != "" }
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${K8S_REPO_CRED}", usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh '''
                            cd ..
                            git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/FlixMad/orai_kubenetes.git
                        '''

                        def changedServices = env.CHANGED_SERVICES.split(",")
                        changedServices.each { service ->
                            def newTag = "0.0.1"
                            sh """
                                cd /var/jenkins_home/workspace/orai-kubenetes
                                sed -i 's#^image: .*#image: ${ECR_URL}/${service}:${newTag}#' ./umbrella-chart/charts/${service}/values.yaml
                            """
                        }

                        sh """
                            cd /var/jenkins_home/workspace/orai-kubenetes
                            git config user.name "maru Lee"
                            git config user.email "jason423@naver.com"
                            git remote -v
                            git add .
                            git commit -m "Update images for changed services ${env.BUILD_ID}"
                            git push origin main

                            echo "push complete."
                            cd ..
                            rm -rf orai-kubenetes
                        """
                    }
                }
            }
        }
    }
}













