pipeline {
    agent any
    
    environment {
        GIT_BRANCH = "${env.BRANCH_NAME}"
        DOCKER_REGISTRY = "your-registry-url"
        KUBE_NAMESPACE = "${env.BRANCH_NAME == 'main' ? 'production' : 'development'}"
    }
    
    stages {
        stage('변경 감지') {
            steps {
                script {
                    // 변경된 모듈 감지
                    def changedModules = sh(
                        script: "git diff --name-only ${GIT_COMMIT}~1 ${GIT_COMMIT} | grep -E '^(payment-api|backoffice-api|backoffice-manage)/' | cut -d'/' -f1 | sort | uniq",
                        returnStdout: true
                    ).trim().split("\n")
                    
                    // 공통 모듈 변경 감지
                    def commonChanged = sh(
                        script: "git diff --name-only ${GIT_COMMIT}~1 ${GIT_COMMIT} | grep -E '^(common|payment-core|payment-infra)/'",
                        returnStatus: true
                    ) == 0
                    
                    env.CHANGED_MODULES = changedModules.join(',')
                    env.COMMON_CHANGED = commonChanged.toString()
                }
            }
        }
        
        stage('모듈별 빌드 및 배포') {
            steps {
                script {
                    def modulesToBuild = []
                    
                    // 공통 모듈이 변경되었으면 모든 API 모듈 빌드
                    if (env.COMMON_CHANGED == 'true') {
                        modulesToBuild = ['payment-api', 'backoffice-api', 'backoffice-manage']
                    } else {
                        modulesToBuild = env.CHANGED_MODULES.split(',')
                    }
                    
                    // 각 모듈 빌드 및 배포
                    for (module in modulesToBuild) {
                        if (module?.trim()) {
                            stage("${module} 빌드") {
                                sh "gradle clean ${module}:build -x test"
                            }
                            
                            stage("${module} 도커 이미지 빌드") {
                                sh "docker build -t ${DOCKER_REGISTRY}/${module}:${GIT_COMMIT} --build-arg MODULE=${module} ."
                                sh "docker push ${DOCKER_REGISTRY}/${module}:${GIT_COMMIT}"
                                
                                // 브랜치별 태그 추가
                                if (env.GIT_BRANCH == 'main') {
                                    sh "docker tag ${DOCKER_REGISTRY}/${module}:${GIT_COMMIT} ${DOCKER_REGISTRY}/${module}:production"
                                    sh "docker push ${DOCKER_REGISTRY}/${module}:production"
                                } else if (env.GIT_BRANCH == 'test') {
                                    sh "docker tag ${DOCKER_REGISTRY}/${module}:${GIT_COMMIT} ${DOCKER_REGISTRY}/${module}:test"
                                    sh "docker push ${DOCKER_REGISTRY}/${module}:test"
                                }
                            }
                            
                            stage("${module} 쿠버네티스 배포") {
                                // 해당 모듈의 k8s 배포 파일 업데이트
                                sh "sed -i 's|image: ${DOCKER_REGISTRY}/${module}:.*|image: ${DOCKER_REGISTRY}/${module}:${GIT_COMMIT}|' k8s/${module}-deployment.yaml"
                                
                                // 쿠버네티스에 배포
                                sh "kubectl apply -f k8s/${module}-deployment.yaml -n ${KUBE_NAMESPACE}"
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo "빌드 및 배포 성공: ${env.CHANGED_MODULES}"
        }
        failure {
            echo "빌드 및 배포 실패"
        }
    }
} 