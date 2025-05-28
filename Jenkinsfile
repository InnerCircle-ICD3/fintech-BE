pipeline {
    agent any
    
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'develop', description: 'GitHub에서 트리거된 브랜치 이름')
        string(name: 'COMMIT_SHA', defaultValue: '', description: 'GitHub에서 트리거된 커밋 해시')
        choice(name: 'BUILD_MODE', choices: ['all', 'only_changed'], description: '모든 모듈을 빌드하려면 all, 변경된 모듈만 빌드하려면 only_changed를 선택하세요.')
        choice(name: 'MODULE', choices: ['all', 'payment-api'], description: 'only_changed를 선택한 경우 무시됩니다. all을 선택하면 모든 모듈이 빌드됩니다.')
    }
    
    environment {
        GIT_BRANCH = "${env.BRANCH_NAME}"
        DOCKER_REGISTRY = "nullplusnull"
        TIMESTAMP = sh(script: 'date +%Y%m%d-%H%M%S', returnStdout: true).trim()
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('환경 설정') {
            steps {
                script {
                    // 브랜치별 프로파일 설정
                    def springProfile = 'local'  // 기본값
                    def kubernetesNamespace = 'default'
                    
                    if (env.GIT_BRANCH == 'test' || env.GIT_BRANCH.startsWith('feature/')) {
                        springProfile = 'local'
                        kubernetesNamespace = 'test'
                        echo "테스트 환경 설정: profile=${springProfile}, namespace=${kubernetesNamespace}"
                    } else if (env.GIT_BRANCH == 'develop') {
                        springProfile = 'dev'
                        kubernetesNamespace = 'default'
                        echo "개발 환경 설정: profile=${springProfile}, namespace=${kubernetesNamespace}"
                    } else if (env.GIT_BRANCH == 'main' || env.GIT_BRANCH == 'master') {
                        springProfile = 'prod'
                        kubernetesNamespace = 'production'
                        echo "운영 환경 설정: profile=${springProfile}, namespace=${kubernetesNamespace}"
                    } else {
                        springProfile = 'dev'  // 기타 브랜치는 개발 환경으로
                        kubernetesNamespace = 'default'
                        echo "기타 브랜치 - 개발 환경으로 설정: profile=${springProfile}, namespace=${kubernetesNamespace}"
                    }
                    
                    env.SPRING_PROFILE = springProfile
                    env.K8S_NAMESPACE = kubernetesNamespace
                    
                    echo "설정된 Spring Profile: ${env.SPRING_PROFILE}"
                    echo "설정된 Kubernetes Namespace: ${env.K8S_NAMESPACE}"
                }
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
                            env.MODULES_TO_BUILD = 'payment-api'
                        } else {
                            env.MODULES_TO_BUILD = params.MODULE
                        }
                    } else {
                        // 변경된 모듈 감지
                        def changedModules = sh(
                            script: "git diff --name-only ${GIT_COMMIT}~1 ${GIT_COMMIT} | grep -E '^(payment-api)' | cut -d'/' -f1 | sort | uniq",
                            returnStdout: true
                        ).trim()
                        
                        // 공통 모듈 변경 감지
                        def commonChanged = sh(
                            script: "git diff --name-only ${GIT_COMMIT}~1 ${GIT_COMMIT} | grep -E '^(payment-core)'",
                            returnStatus: true
                        ) == 0
                        
                        if (commonChanged || changedModules == '') {
                            env.MODULES_TO_BUILD = 'payment-api'
                            echo "공통 모듈 변경 또는 변경 감지 안됨 - 모든 모듈 빌드"
                        } else {
                            env.MODULES_TO_BUILD = changedModules.replaceAll('\n', ',')
                            echo "변경된 모듈만 빌드: ${env.MODULES_TO_BUILD}"
                        }
                    }
                }
            }
        }
        
        stage('Gradle 빌드') {
            steps {
                script {
                    def moduleList = env.MODULES_TO_BUILD.split(',')
                    
                    sh 'chmod +x ./gradlew'
                    
                    for (module in moduleList) {
                        if (module?.trim()) {
                            echo "${module} 모듈 빌드 중..."
                            sh "./gradlew :${module}:build -x test"
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
                                // Docker 이미지 빌드
                                sh """
                                    docker build -t ${DOCKER_REGISTRY}/${module}:${TIMESTAMP} \\
                                    --build-arg MODULE=${module} \\
                                    --build-arg JAR_FILE=\$(find ${module}/build/libs/ -name '*.jar' | head -1) \\
                                    .
                                """
                                sh "docker push ${DOCKER_REGISTRY}/${module}:${TIMESTAMP}"
                                
                                // 쿠버네티스 배포
                                stage("${module} 배포") {
                                    // 네임스페이스 생성 (존재하지 않는 경우)
                                    sh "kubectl create namespace ${env.K8S_NAMESPACE} || true"
                                    
                                    // 디플로이먼트 존재 여부 확인
                                    def deploymentExists = sh(
                                        script: "kubectl get deployment ${module} -n ${env.K8S_NAMESPACE} 2>/dev/null || echo 'NOT_FOUND'",
                                        returnStdout: true
                                    ).trim()

                                    def modulePort = 8081 // 기본 포트
                                    if (module == 'payment-api') {
                                        modulePort = 8081
                                    } else if (module == 'backoffice-api') {
                                        modulePort = 8080
                                    } else if (module == 'backoffice-manage') {
                                        modulePort = 8082
                                    }
                                    
                                    if (deploymentExists.contains('NOT_FOUND')) {
                                        // 디플로이먼트가 없으면 생성
                                        sh "kubectl create deployment ${module} --image=${DOCKER_REGISTRY}/${module}:${TIMESTAMP} -n ${env.K8S_NAMESPACE}"
                                        
                                        // 환경 변수 설정 (프로파일 포함)
                                        sh """
                                            kubectl set env deployment/${module} \\
                                            SERVER_PORT=${modulePort} \\
                                            SPRING_PROFILES_ACTIVE=${env.SPRING_PROFILE} \\
                                            -n ${env.K8S_NAMESPACE}
                                        """
                                        
                                        sh "kubectl expose deployment ${module} --port=${modulePort} --target-port=${modulePort} --type=ClusterIP -n ${env.K8S_NAMESPACE} || true"
                                    } else {
                                        // 있으면 이미지만 업데이트
                                        sh "kubectl set image deployment/${module} ${module}=${DOCKER_REGISTRY}/${module}:${TIMESTAMP} -n ${env.K8S_NAMESPACE}"
                                        
                                        // 환경 변수 설정 (프로파일 포함)
                                        sh """
                                            kubectl set env deployment/${module} \\
                                            SERVER_PORT=${modulePort} \\
                                            SPRING_PROFILES_ACTIVE=${env.SPRING_PROFILE} \\
                                            -n ${env.K8S_NAMESPACE} --overwrite
                                        """
                                    }
                                    
                                    echo "배포 완료: ${module} (Profile: ${env.SPRING_PROFILE}, Namespace: ${env.K8S_NAMESPACE})"
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
            echo "빌드 및 배포 성공: ${env.MODULES_TO_BUILD} (Profile: ${env.SPRING_PROFILE})"
        }
        failure {
            echo "빌드 및 배포 실패"
        }
    }
} 