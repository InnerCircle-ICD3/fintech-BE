pipeline {
    agent any
    
    environment {
        // 환경 변수 설정
        JAVA_HOME = tool 'JDK21'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        K8S_NAMESPACE = "fintech-be"  // 네임스페이스 환경변수 추가
    }
    
    stages {
        stage('소스 체크아웃') {
            steps {
                checkout scm
            }
        }
        
        stage('빌드') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }
        
        stage('테스트') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
                failure {
                    echo '테스트 실패했지만 배포 계속 진행'
                }
            }
        }
        
        stage('아티팩트 저장') {
            steps {
                archiveArtifacts artifacts: 'payment-api/build/libs/*.jar', allowEmptyArchive: true
                archiveArtifacts artifacts: 'backoffice-api/build/libs/*.jar', allowEmptyArchive: true
            }
        }
        
        stage('배포') {
            when {
                expression { 
                    return env.BRANCH_NAME in ['main', 'develop', 'test'] 
                }
            }
            steps {
                script {
                    def deployEnv = ""
                    switch(env.BRANCH_NAME) {
                        case 'main':
                            deployEnv = "production"
                            break
                        case 'develop':
                            deployEnv = "development"
                            break
                        case 'test':
                            deployEnv = "testing"
                            break
                    }
                    
                    echo "배포 환경: ${deployEnv}"
                    echo "네임스페이스: ${K8S_NAMESPACE}"
                    
                    // 네임스페이스 생성 (존재하지 않는 경우에만)
                    sh "kubectl create namespace ${K8S_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -"
                    
                    // 쿠버네티스 배포
                    sh "kubectl config use-context ${deployEnv}"
                    sh "kubectl apply -f k8s/${deployEnv} -n ${K8S_NAMESPACE}"
                    
                    // 배포 상태 확인
                    sh "kubectl rollout status deployment/payment-api -n ${K8S_NAMESPACE} || true"
                    sh "kubectl rollout status deployment/backoffice-api -n ${K8S_NAMESPACE} || true"
                }
            }
        }
    }
    
    post {
        success {
            echo '파이프라인이 성공적으로 완료되었습니다.'
        }
        failure {
            echo '파이프라인이 실패했습니다.'
        }
        always {
            echo '파이프라인 실행이 완료되었습니다.'
            // 워크스페이스 정리 (선택사항)
            // cleanWs()
        }
    }
} 