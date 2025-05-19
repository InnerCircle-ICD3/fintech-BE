pipeline {
    agent any
    
<<<<<<< HEAD
    parameters {
        choice(name: 'BUILD_MODE', choices: ['all', 'only_changed'], description: '모든 모듈을 빌드하려면 all, 변경된 모듈만 빌드하려면 only_changed를 선택하세요.')
        choice(name: 'MODULE', choices: ['all', 'payment-api', 'backoffice-api', 'backoffice-manage'], description: 'only_changed를 선택한 경우 무시됩니다. all을 선택하면 모든 모듈이 빌드됩니다.')
    }
    
    environment {
        GIT_BRANCH = "${env.BRANCH_NAME}"
        DOCKER_REGISTRY = "nullplusnull"
        TIMESTAMP = sh(script: 'date +%Y%m%d-%H%M%S', returnStdout: true).trim()
    }

    // BUILD_MODE 파라미터로 'all' 또는 'only_changed' 선택 가능
=======
    // 파이프라인 파라미터 정의
    parameters {
        string(name: 'DOCKER_REGISTRY', defaultValue: 'nullplusnull', description: 'Docker 레지스트리 이름')
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch Name')
        string(name: 'COMMIT_SHA', defaultValue: '', description: 'Commit SHA')
    }
    
    environment {
        IMAGE_NAME_PAYMENT_API = 'payment-api'
        IMAGE_NAME_BACKOFFICE_API = 'backoffice-api'
    }
>>>>>>> e1da41e6726a7be5453200f6d8b368709067303a
    
    stages {
        stage('Checkout') {
            steps {
<<<<<<< HEAD
=======
                // 기본 체크아웃 사용
>>>>>>> e1da41e6726a7be5453200f6d8b368709067303a
                checkout scm
            }
        }
        
<<<<<<< HEAD
        stage('DockerHub Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
=======
        stage('Setup Environment') {
            steps {
                script {
                    // BRANCH_NAME이 비어있으면 Git에서 현재 브랜치 가져오기
                    if (params.BRANCH_NAME == 'main' && env.BRANCH_NAME != null) {
                        env.CURRENT_BRANCH = env.BRANCH_NAME.replaceAll('origin/', '')
                    } else {
                        env.CURRENT_BRANCH = params.BRANCH_NAME
                    }
                    
                    echo "현재 브랜치: ${env.CURRENT_BRANCH}"
                    
                    // 브랜치에 따른 환경 설정
                    if (env.CURRENT_BRANCH == 'main') {
                        env.K8S_NAMESPACE = 'fintech-be'
                        env.SPRING_PROFILE = 'prod'
                        env.IMAGE_TAG = 'prod'
                        env.DOMAIN_SUFFIX = 'passionpay.com'
                    } else if (env.CURRENT_BRANCH == 'develop') {
                        env.K8S_NAMESPACE = 'fintech-be'
                        env.SPRING_PROFILE = 'dev'
                        env.IMAGE_TAG = 'dev'
                        env.DOMAIN_SUFFIX = 'test.passionpay.com'
                    } else if (env.CURRENT_BRANCH == 'test') {
                        env.K8S_NAMESPACE = 'fintech-be'
                        env.SPRING_PROFILE = 'test'
                        env.IMAGE_TAG = 'test'
                        env.DOMAIN_SUFFIX = 'test.passionpay.com'
                    } else {
                        env.K8S_NAMESPACE = 'fintech-be'
                        env.SPRING_PROFILE = 'dev'
                        env.IMAGE_TAG = 'feature'
                        env.DOMAIN_SUFFIX = 'test.passionpay.com'
                    }
                    
                    // Docker 레지스트리 설정
                    env.DOCKER_REGISTRY = params.DOCKER_REGISTRY
                    echo "Docker 레지스트리: ${env.DOCKER_REGISTRY}"
>>>>>>> e1da41e6726a7be5453200f6d8b368709067303a
                }
            }
        }
        
<<<<<<< HEAD
        stage('모듈 결정') {
            steps {
                script {
                    if (params.BUILD_MODE == 'all') {
                        if (params.MODULE == 'all') {
                            env.MODULES_TO_BUILD = 'payment-api,backoffice-api,backoffice-manage'
                        } else {
                            env.MODULES_TO_BUILD = params.MODULE
                        }
                    } else {
                        // 변경된 모듈 감지
                        def changedModules = sh(
                            script: "git diff --name-only ${GIT_COMMIT}~1 ${GIT_COMMIT} | grep -E '^(payment-api|backoffice-api|backoffice-manage)' | cut -d'/' -f1 | sort | uniq",
                            returnStdout: true
                        ).trim()
                        
                        // 공통 모듈 변경 감지
                        def commonChanged = sh(
                            script: "git diff --name-only ${GIT_COMMIT}~1 ${GIT_COMMIT} | grep -E '^(common|payment-core|payment-infra)'",
                            returnStatus: true
                        ) == 0
                        
                        if (commonChanged || changedModules == '') {
                            env.MODULES_TO_BUILD = 'payment-api,backoffice-api,backoffice-manage'
                            echo "공통 모듈 변경 또는 변경 감지 안됨 - 모든 모듈 빌드"
                        } else {
                            env.MODULES_TO_BUILD = changedModules.replaceAll('\n', ',')
                            echo "변경된 모듈만 빌드: ${env.MODULES_TO_BUILD}"
                        }
=======
        stage('Debug Info') {
            steps {
                sh 'java -version || echo "Java not found"'
                sh 'echo "PATH: $PATH"'
                sh 'echo "Current directory: $(pwd)"'
                sh 'ls -la'
            }
        }
        
        stage('Build with Gradle') {
            options {
                timeout(time: 15, unit: 'MINUTES')
            }
            steps {
                script {
                    def isWindows = isUnix() ? false : true
                    
                    if (isWindows) {
                        bat 'gradlew.bat clean build -x test --no-daemon'
                    } else {
                        sh 'chmod +x gradlew'
                        // 시스템 기본 Java 사용 및 디버그 정보 출력
                        sh './gradlew --version'
                        sh './gradlew clean --info || echo "Clean failed but continuing"'
                        sh './gradlew compileJava --info || echo "Compile failed but continuing"'
                        sh './gradlew build -x test --info --stacktrace --no-daemon --console=plain'
>>>>>>> e1da41e6726a7be5453200f6d8b368709067303a
                    }
                }
            }
        }
        
<<<<<<< HEAD
        stage('모듈별 빌드 및 배포') {
            steps {
                script {
                    def moduleList = env.MODULES_TO_BUILD.split(',')
                    
                    for (module in moduleList) {
                        if (module?.trim()) {
                            stage("${module} 빌드 및 배포") {
                                // Dockerfile에서 정확한 JAR 파일을 찾도록 스크립트로 실행
                                sh """
                                    docker build -t ${DOCKER_REGISTRY}/${module}:${TIMESTAMP} \\
                                    --build-arg MODULE=${module} \\
                                    --build-arg JAR_FILE=\$(find ${module}/build/libs/ -name '*.jar' | head -1) \\
                                    .
                                """
                                sh "docker push ${DOCKER_REGISTRY}/${module}:${TIMESTAMP}"
                                
                                // 쿠버네티스 배포
                                stage("${module} 배포") {
                                    // 디플로이먼트 존재 여부 확인
                                    def deploymentExists = sh(
                                        script: "kubectl get deployment ${module} -n default 2>/dev/null || echo 'NOT_FOUND'",
                                        returnStdout: true
                                    ).trim()
                                    
                                    if (deploymentExists.contains('NOT_FOUND')) {
                                        // 디플로이먼트가 없으면 생성
                                        sh "kubectl create deployment ${module} --image=${DOCKER_REGISTRY}/${module}:${TIMESTAMP} -n default"
                                        sh "kubectl expose deployment ${module} --port=8080 --target-port=8080 --type=ClusterIP -n default || true"
                                    } else {
                                        // 있으면 이미지만 업데이트
                                        sh "kubectl set image deployment/${module} ${module}=${DOCKER_REGISTRY}/${module}:${TIMESTAMP} -n default"
                                    }
                                }
=======
        stage('Build and Push Docker Images') {
            parallel {
                stage('Payment API') {
                    steps {
                        script {
                            sh "docker build -t ${env.DOCKER_REGISTRY}/${IMAGE_NAME_PAYMENT_API}:${BUILD_NUMBER} -f payment-api/Dockerfile --build-arg PROFILE=${env.SPRING_PROFILE} ."
                            sh "docker tag ${env.DOCKER_REGISTRY}/${IMAGE_NAME_PAYMENT_API}:${BUILD_NUMBER} ${env.DOCKER_REGISTRY}/${IMAGE_NAME_PAYMENT_API}:${env.IMAGE_TAG}"
                            
                            withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                                sh '''
                                echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                                docker push ${DOCKER_REGISTRY}/${IMAGE_NAME_PAYMENT_API}:${BUILD_NUMBER}
                                docker push ${DOCKER_REGISTRY}/${IMAGE_NAME_PAYMENT_API}:${IMAGE_TAG}
                                '''
>>>>>>> e1da41e6726a7be5453200f6d8b368709067303a
                            }
                        }
                    }
                }
<<<<<<< HEAD
=======
                
                stage('Backoffice API') {
                    steps {
                        script {
                            sh "docker build -t ${env.DOCKER_REGISTRY}/${IMAGE_NAME_BACKOFFICE_API}:${BUILD_NUMBER} -f backoffice-api/Dockerfile --build-arg PROFILE=${env.SPRING_PROFILE} ."
                            sh "docker tag ${env.DOCKER_REGISTRY}/${IMAGE_NAME_BACKOFFICE_API}:${BUILD_NUMBER} ${env.DOCKER_REGISTRY}/${IMAGE_NAME_BACKOFFICE_API}:${env.IMAGE_TAG}"
                            
                            withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                                sh '''
                                echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                                docker push ${DOCKER_REGISTRY}/${IMAGE_NAME_BACKOFFICE_API}:${BUILD_NUMBER}
                                docker push ${DOCKER_REGISTRY}/${IMAGE_NAME_BACKOFFICE_API}:${IMAGE_TAG}
                                '''
                            }
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubernetes-config', variable: 'KUBECONFIG')]) {
                    sh '''
                    # 네임스페이스가 존재하지 않으면 생성
                    kubectl create namespace ${K8S_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                    
                    # Payment API 배포
                    sed -i "s|\\${DOCKER_REGISTRY}|${DOCKER_REGISTRY}|g" k8s/payment-api/deployment.yaml
                    sed -i "s|\\${VERSION}|${BUILD_NUMBER}|g" k8s/payment-api/deployment.yaml
                    sed -i "s|value: \"prod\"|value: \"${SPRING_PROFILE}\"|g" k8s/payment-api/deployment.yaml
                    sed -i "s|readinessProbe:|readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080|g" k8s/payment-api/deployment.yaml
                    kubectl apply -f k8s/payment-api/deployment.yaml -n ${K8S_NAMESPACE}
                    
                    # 도메인 이름 변경
                    sed -i "s|api.passionpay.com|api.${DOMAIN_SUFFIX}|g" k8s/payment-api/service.yaml
                    kubectl apply -f k8s/payment-api/service.yaml -n ${K8S_NAMESPACE}
                    kubectl rollout status deployment/payment-api -n ${K8S_NAMESPACE}
                    
                    # Backoffice API 배포
                    sed -i "s|\\${DOCKER_REGISTRY}|${DOCKER_REGISTRY}|g" k8s/backoffice-api/deployment.yaml
                    sed -i "s|\\${VERSION}|${BUILD_NUMBER}|g" k8s/backoffice-api/deployment.yaml
                    sed -i "s|value: \"prod\"|value: \"${SPRING_PROFILE}\"|g" k8s/backoffice-api/deployment.yaml
                    sed -i "s|readinessProbe:|readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080|g" k8s/backoffice-api/deployment.yaml
                    kubectl apply -f k8s/backoffice-api/deployment.yaml -n ${K8S_NAMESPACE}
                    
                    # 도메인 이름 변경
                    sed -i "s|admin-api.passionpay.com|admin-api.${DOMAIN_SUFFIX}|g" k8s/backoffice-api/service.yaml
                    kubectl apply -f k8s/backoffice-api/service.yaml -n ${K8S_NAMESPACE}
                    kubectl rollout status deployment/backoffice-api -n ${K8S_NAMESPACE}
                    '''
                }
>>>>>>> e1da41e6726a7be5453200f6d8b368709067303a
            }
        }
    }
    
    post {
        success {
<<<<<<< HEAD
            echo "빌드 및 배포 성공: ${env.MODULES_TO_BUILD}"
        }
        failure {
            echo "빌드 및 배포 실패"
=======
            echo '배포가 성공적으로 완료되었습니다!'
        }
        failure {
            echo '배포 중 오류가 발생했습니다.'
            echo '에러 코드: ${currentBuild.result}'
        }
        always {
            // 불필요한 Docker 이미지 정리
            sh 'docker system prune -f || echo "Docker cleanup failed but it is OK"'
            echo '파이프라인 종료!'
>>>>>>> e1da41e6726a7be5453200f6d8b368709067303a
        }
    }
} 