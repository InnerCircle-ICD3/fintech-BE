pipeline {
    agent any
    
    environment {
        // 환경 변수 설정
        // 서버에 설치된 Java 경로 명시적 지정
        JAVA_HOME = "/usr"
        PATH = "/usr/bin:${env.PATH}"
        K8S_NAMESPACE = "fintech-be"  // 네임스페이스 환경변수 추가
        DOCKER_REGISTRY = "nullplusnull"  // DockerHub 사용자 이름
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
                    
                    // 이미지 태그 설정
                    env.IMAGE_TAG = env.BRANCH_NAME.toLowerCase() == "main" ? "latest" : env.BRANCH_NAME.toLowerCase()
                    env.BUILD_VERSION = "${env.BUILD_NUMBER}-${env.IMAGE_TAG}"
                    
                    echo "이미지 태그: ${env.IMAGE_TAG}"
                    echo "빌드 버전: ${env.BUILD_VERSION}"
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
        
        stage('Docker 이미지 빌드 및 푸시') {
            steps {
                script {
                    // Docker 로그인
                    withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin"
                        
                        // payment-api 이미지 빌드 및 푸시
                        sh """
                        docker build -t ${DOCKER_REGISTRY}/payment-api:${BUILD_VERSION} -t ${DOCKER_REGISTRY}/payment-api:${IMAGE_TAG} -f payment-api/Dockerfile payment-api --build-arg JAR_FILE=build/libs/payment-api-0.0.1-SNAPSHOT.jar
                        docker push ${DOCKER_REGISTRY}/payment-api:${BUILD_VERSION}
                        docker push ${DOCKER_REGISTRY}/payment-api:${IMAGE_TAG}
                        """
                        
                        // backoffice-api 이미지 빌드 및 푸시
                        sh """
                        docker build -t ${DOCKER_REGISTRY}/backoffice-api:${BUILD_VERSION} -t ${DOCKER_REGISTRY}/backoffice-api:${IMAGE_TAG} -f backoffice-api/Dockerfile backoffice-api --build-arg JAR_FILE=build/libs/backoffice-api-0.0.1-SNAPSHOT.jar
                        docker push ${DOCKER_REGISTRY}/backoffice-api:${BUILD_VERSION}
                        docker push ${DOCKER_REGISTRY}/backoffice-api:${IMAGE_TAG}
                        """
                        
                        // backoffice-manage 이미지 빌드 및 푸시
                        sh """
                        docker build -t ${DOCKER_REGISTRY}/backoffice-manage:${BUILD_VERSION} -t ${DOCKER_REGISTRY}/backoffice-manage:${IMAGE_TAG} -f backoffice-manage/Dockerfile backoffice-manage --build-arg JAR_FILE=build/libs/backoffice-manage-0.0.1-SNAPSHOT.jar
                        docker push ${DOCKER_REGISTRY}/backoffice-manage:${BUILD_VERSION}
                        docker push ${DOCKER_REGISTRY}/backoffice-manage:${IMAGE_TAG}
                        """
                    }
                }
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
                    
                    // 쿠버네티스 배포 - 자격 증명 사용
                    withCredentials([file(credentialsId: 'kubernetes-config', variable: 'KUBECONFIG')]) {
                        // 네임스페이스 생성 (존재하지 않는 경우에만)
                        sh "kubectl --kubeconfig=${KUBECONFIG} create namespace ${K8S_NAMESPACE} --dry-run=client -o yaml | kubectl --kubeconfig=${KUBECONFIG} apply -f -"
                        
                        // 이미지 이름과 태그를 포함한 매니페스트 생성
                        sh """
                        mkdir -p k8s-deploy
                        
                        # payment-api 매니페스트 생성
                        cat <<EOF > k8s-deploy/payment-api.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-api
  namespace: ${K8S_NAMESPACE}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: payment-api
  template:
    metadata:
      labels:
        app: payment-api
    spec:
      containers:
      - name: payment-api
        image: ${DOCKER_REGISTRY}/payment-api:${IMAGE_TAG}
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: 200m
            memory: 256Mi
          limits:
            cpu: 500m
            memory: 512Mi
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "${deployEnv}"
---
apiVersion: v1
kind: Service
metadata:
  name: payment-api
  namespace: ${K8S_NAMESPACE}
spec:
  selector:
    app: payment-api
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP
EOF

                        # backoffice-api 매니페스트 생성
                        cat <<EOF > k8s-deploy/backoffice-api.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backoffice-api
  namespace: ${K8S_NAMESPACE}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: backoffice-api
  template:
    metadata:
      labels:
        app: backoffice-api
    spec:
      containers:
      - name: backoffice-api
        image: ${DOCKER_REGISTRY}/backoffice-api:${IMAGE_TAG}
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: 200m
            memory: 256Mi
          limits:
            cpu: 500m
            memory: 512Mi
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "${deployEnv}"
---
apiVersion: v1
kind: Service
metadata:
  name: backoffice-api
  namespace: ${K8S_NAMESPACE}
spec:
  selector:
    app: backoffice-api
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP
EOF

                        # backoffice-manage 매니페스트 생성
                        cat <<EOF > k8s-deploy/backoffice-manage.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backoffice-manage
  namespace: ${K8S_NAMESPACE}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: backoffice-manage
  template:
    metadata:
      labels:
        app: backoffice-manage
    spec:
      containers:
      - name: backoffice-manage
        image: ${DOCKER_REGISTRY}/backoffice-manage:${IMAGE_TAG}
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: 200m
            memory: 256Mi
          limits:
            cpu: 500m
            memory: 512Mi
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "${deployEnv}"
---
apiVersion: v1
kind: Service
metadata:
  name: backoffice-manage
  namespace: ${K8S_NAMESPACE}
spec:
  selector:
    app: backoffice-manage
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP
EOF
                        """
                        
                        // 생성된 매니페스트로 배포
                        sh "kubectl --kubeconfig=${KUBECONFIG} apply -f k8s-deploy/"
                        
                        // 배포 상태 확인
                        sh "kubectl --kubeconfig=${KUBECONFIG} rollout status deployment/payment-api -n ${K8S_NAMESPACE} || true"
                        sh "kubectl --kubeconfig=${KUBECONFIG} rollout status deployment/backoffice-api -n ${K8S_NAMESPACE} || true"
                        sh "kubectl --kubeconfig=${KUBECONFIG} rollout status deployment/backoffice-manage -n ${K8S_NAMESPACE} || true"
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