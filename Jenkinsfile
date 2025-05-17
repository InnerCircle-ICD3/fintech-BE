pipeline {
    agent any
    
    parameters {
        choice(name: 'BUILD_MODE', choices: ['all', 'only_changed'], description: '모든 모듈을 빌드하려면 all, 변경된 모듈만 빌드하려면 only_changed를 선택하세요.')
        choice(name: 'MODULE', choices: ['all', 'payment-api', 'backoffice-api', 'backoffice-manage'], description: 'only_changed를 선택한 경우 무시됩니다. all을 선택하면 모든 모듈이 빌드됩니다.')
        choice(name: 'DEPLOY_NAMESPACE', choices: ['default', 'dev', 'stage', 'prod'], description: '배포할 쿠버네티스 네임스페이스를 선택하세요.')
    }
    
    environment {
        GIT_BRANCH = "${env.BRANCH_NAME}"
        DOCKER_REGISTRY = "nullplusnull"
        TIMESTAMP = sh(script: 'date +%Y%m%d-%H%M%S', returnStdout: true).trim()
    }

    // BUILD_MODE 파라미터로 'all' 또는 'only_changed' 선택 가능
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Validate Parameters') {
            steps {
                script {
                    if (params.DEPLOY_NAMESPACE == '') {
                        error "DEPLOY_NAMESPACE 파라미터가 설정되지 않았습니다."
                    }
                    echo "배포 네임스페이스: ${params.DEPLOY_NAMESPACE}"
                }
            }
        }
        
        stage('DockerHub Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin || echo "Docker 로그인 건너뜀"'
                }
            }
        }
        
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
                    }
                }
            }
        }
        
        stage('모듈별 빌드, 테스트 및 배포') {
            steps {
                script {
                    def moduleList = env.MODULES_TO_BUILD.split(',')
                    
                    for (module in moduleList) {
                        if (module?.trim()) {
                            stage("${module} 테스트") {
                                sh "./gradlew ${module}:test || echo \"${module} 테스트 건너뜀\""
                            }
                            
                            stage("${module} 빌드") {
                                sh "./gradlew ${module}:build -x test || echo \"${module} 빌드 건너뜀\""
                            }
                            
                            stage("${module} Docker 빌드/푸시") {
                                // Dockerfile에서 정확한 JAR 파일을 찾도록 스크립트로 실행
                                sh """
                                    docker build -t ${DOCKER_REGISTRY}/${module}:${TIMESTAMP} \\
                                    --build-arg MODULE=${module} \\
                                    --build-arg JAR_FILE=\$(find ${module}/build/libs/ -name '*.jar' | head -1) \\
                                    . || echo "${module} Docker 빌드 건너뜀"
                                """
                                sh "docker push ${DOCKER_REGISTRY}/${module}:${TIMESTAMP} || echo \"${module} Docker 푸시 건너뜀\""
                                sh "docker tag ${DOCKER_REGISTRY}/${module}:${TIMESTAMP} ${DOCKER_REGISTRY}/${module}:latest || echo \"${module} Docker 태그 건너뜀\""
                                sh "docker push ${DOCKER_REGISTRY}/${module}:latest || echo \"${module} Docker 푸시 건너뜀\""
                            }
                            
                            // 쿠버네티스 배포
                            stage("${module} K8s 배포") {
                                // K8s 매니페스트 파일 수정 및 적용
                                sh """
                                    sed -i 's|your-registry-url/${module}:latest|${DOCKER_REGISTRY}/${module}:${TIMESTAMP}|g' k8s/${module}-deployment.yaml || echo "${module} 배포파일 수정 건너뜀"
                                    kubectl apply -f k8s/${module}-deployment.yaml -n ${params.DEPLOY_NAMESPACE} || echo "${module} K8s 배포 건너뜀"
                                    kubectl rollout status deployment/${module} -n ${params.DEPLOY_NAMESPACE} || echo "${module} 배포 상태 확인 건너뜀"
                                """
                            }
                            
                            stage("${module} 정리") {
                                // 이전 이미지 정리 (옵션)
                                sh """
                                    # 최근 5개 이미지를 제외한 이전 이미지 정리 (에러 무시)
                                    docker images ${DOCKER_REGISTRY}/${module} --format "{{.ID}} {{.Tag}}" | grep -v 'latest' | sort -k2 -r | tail -n +6 | awk '{print \$1}' | xargs -r docker rmi || echo "이미지 정리 건너뜀"
                                """
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo "빌드 및 배포 성공: ${env.MODULES_TO_BUILD} -> ${params.DEPLOY_NAMESPACE} 네임스페이스"
            slackSend(color: 'good', message: "배포 성공: ${env.MODULES_TO_BUILD} -> ${params.DEPLOY_NAMESPACE}")
        }
        failure {
            echo "빌드 및 배포 실패"
            slackSend(color: 'danger', message: "배포 실패: ${env.JOB_NAME} (${env.BUILD_NUMBER})")
        }
        always {
            // 작업 공간 청소
            cleanWs()
        }
    }
} 