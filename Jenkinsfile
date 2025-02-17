// 필요한 변수를 선언할 수 있다. (내가 직접 선언하는 변수, 젠킨스 환경변수를 끌고 올 수 있음)
def ecrLoginHelper="docker-credential-ecr-login" // ECR credential helper 이름

// 젠킨스의 선언형 파이프라인 정의부 시작 (그루비 언어)
pipeline {
    agent any // 어느 젠킨스 서버에서도 실행이 가능
    environment {
        REGION = "ap-northeast-2"
        ECR_URL = "054037133599.dkr.ecr.ap-northeast-2.amazonaws.com"
        SERVICE_DIRS = "config-service,gateway-service,user-service,calendar-service,chat-service,etc-service"
        K8S_REPO_URL = "https://github.com/FlixMad/orai_kubenetes.git"
        K8S_REPO_CRED = "github-k8s-repo-token" // 여기다 토큰 넣는건가봐!
    }
    stages {
        stage('Pull Codes from Github'){ // 스테이지 제목 (맘대로 써도 됨.)
            steps{
                checkout scm // 젠킨스와 연결된 소스 컨트롤 매니저(git 등)에서 코드를 가져오는 명령어
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
            // 이 스테이지는 모든 서비스를 항상 빌드하도록 설정됨.
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES.split(",")
                    changedServices.each { service ->
                        sh """
                        echo "Building ${service}..."
                        cd ${service}
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
            steps {
                script {
                    withAWS(region: "${REGION}", credentials: "aws-key") {
                        def changedServices = env.CHANGED_SERVICES.split(",")
                        changedServices.each { service ->
                            // 여기서 원하는 버전을 정하거나, 커밋 태그를 붙여보자.
                            def newTag = "0.2.3"
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
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${K8S_REPO_CRED}", usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        // 기존에 클론된 레포지토리가 있다면 pull, 없으면 clone
                        sh '''
                            cd ..
                            ls -a
                            git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/FlixMad/orai_kubenetes.git
                        '''

                        def changedServices = env.CHANGED_SERVICES.split(",")
                        changedServices.each { service ->
                            def newTag = "0.2.3" // 이미지 빌드할 때 사용한 태그를 동일하게 사용.

                            // umbrella-chart/charts/<service>/values.yaml 파일 내의 image 태그 교체.
                            sh """
                                cd /var/jenkins_home/workspace/orai_kubenetes
                                ls -a
                                echo "Updating ${service} image tag in k8s repo...."
                                sed -i 's#^image: .*#image: ${ECR_URL}/${service}:${newTag}#' ./umbrella-chart/charts/${service}/values.yaml
                            """
                        }

                        // 변경사항 commit & push
                        sh """
                            cd /var/jenkins_home/workspace/orai_kubenetes
                            git config user.name "maru Lee"
                            git config user.email "jason423@naver.com"
                            git remote -v
                            git add .
                            git commit -m "Update images for all services ${env.BUILD_ID}"
                            git push origin main

                            echo "push complete."
                            cd ..
                            rm -rf orai_kubenetes
                            ls -a
                        """

                    }
                }
            }
        }
    }
}
