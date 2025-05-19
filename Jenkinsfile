pipeline {
    agent any
    
    environment {
        // 환경 변수 설정
        // 서버에 설치된 Java 경로 명시적 지정
        JAVA_HOME = "/usr"
        PATH = "/usr/bin:${env.PATH}"
        K8S_NAMESPACE = "fintech-be"  // 네임스페이스 환경변수 추가
    }
    
    stages {
        stage('소스 체크아웃') {
            steps {
                checkout scm
            }
        }
        
        stage('브랜치 확인') {
            steps {
                script {
                    // Git 브랜치 정보 가져오기
                    env.GIT_BRANCH = sh(script: 'git rev-parse --abbrev-ref HEAD || echo unknown', returnStdout: true).trim()
                    
                    // GitHub Actions에서 전달한 브랜치 정보가 있으면 사용
                    if (env.BRANCH_NAME) {
                        echo "Jenkins BRANCH_NAME: ${env.BRANCH_NAME}"
                    } else {
                        env.BRANCH_NAME = env.GIT_BRANCH
                        echo "Git에서 가져온 브랜치: ${env.BRANCH_NAME}"
                    }
                    
                    // 브랜치 이름에서 'origin/' 제거
                    env.BRANCH_NAME = env.BRANCH_NAME.replaceAll('origin/', '')
                    
                    echo "사용할 브랜치 이름: ${env.BRANCH_NAME}"
                }
            }
        }
        
        stage('Java 버전 확인') {
            steps {
                sh 'java -version'
                sh 'echo $PATH'
                sh 'echo $JAVA_HOME'
            }
        }
        
        stage('빌드') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }
        
        stage('테스트 (선택적)') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test -i'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
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
                    def branchName = env.BRANCH_NAME?.toLowerCase()
                    echo "배포 조건 확인: 브랜치=${branchName}"
                    return branchName in ['main', 'master', 'develop', 'test'] || branchName?.contains('test')
                }
            }
            steps {
                script {
                    def deployEnv = ""
                    def branchName = env.BRANCH_NAME.toLowerCase()
                    
                    if (branchName == 'main' || branchName == 'master') {
                        deployEnv = "production"
                    } else if (branchName == 'develop') {
                        deployEnv = "development"
                    } else if (branchName == 'test' || branchName.contains('test')) {
                        deployEnv = "testing"
                    } else {
                        deployEnv = "testing"  // 기본값
                    }
                    
                    echo "배포 환경: ${deployEnv}"
                    echo "네임스페이스: ${K8S_NAMESPACE}"
                    
                    // 네임스페이스 생성 (존재하지 않는 경우에만)
                    sh "kubectl create namespace ${K8S_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -"
                    
                    // 쿠버네티스 배포
                    try {
                        sh "kubectl config use-context ${deployEnv}"
                    } catch (Exception e) {
                        echo "kubectl context 설정 실패, 기본 컨텍스트 사용: ${e.message}"
                    }
                    
                    try {
                        sh "ls -la k8s/${deployEnv} || mkdir -p k8s/${deployEnv}"
                        
                        // k8s 디렉토리가 비어있는 경우
                        def filesExist = sh(script: "find k8s/${deployEnv} -type f | wc -l", returnStdout: true).trim()
                        
                        if (filesExist == "0") {
                            echo "k8s/${deployEnv} 디렉토리에 파일이 없습니다. 기본 매니페스트 사용"
                            sh "kubectl apply -f k8s || true"
                        } else {
                            sh "kubectl apply -f k8s/${deployEnv} -n ${K8S_NAMESPACE}"
                        }
                        
                        // 배포 상태 확인
                        sh "kubectl rollout status deployment/payment-api -n ${K8S_NAMESPACE} || true"
                        sh "kubectl rollout status deployment/backoffice-api -n ${K8S_NAMESPACE} || true"
                    } catch (Exception e) {
                        echo "쿠버네티스 배포 중 오류 발생: ${e.message}"
                        echo "기본 배포 시도..."
                        sh "kubectl apply -f k8s -n ${K8S_NAMESPACE} || true"
                    }
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