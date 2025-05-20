pipeline {
    agent any
    
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'develop', description: 'GitHub에서 트리거된 브랜치 이름')
        string(name: 'COMMIT_SHA', defaultValue: '', description: 'GitHub에서 트리거된 커밋 해시')
        choice(name: 'BUILD_MODE', choices: ['all', 'only_changed'], description: '모든 모듈을 빌드하려면 all, 변경된 모듈만 빌드하려면 only_changed를 선택하세요.')
        choice(name: 'MODULE', choices: ['all', 'payment-api', 'backoffice-api', 'backoffice-manage'], description: 'only_changed를 선택한 경우 무시됩니다. all을 선택하면 모든 모듈이 빌드됩니다.')
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
        
        stage('DockerHub Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
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
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo "빌드 및 배포 성공: ${env.MODULES_TO_BUILD}"
        }
        failure {
            echo "빌드 및 배포 실패"
        }
    }
} 