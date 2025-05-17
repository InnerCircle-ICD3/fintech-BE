# 핀테크 백엔드 서비스

## CI/CD 파이프라인 구성

### 구성 요소
- **GitHub Actions**: 코드 테스트, 빌드 및 Jenkins 트리거
- **Jenkins**: Docker 이미지 빌드 및 Kubernetes 배포

### 브랜치와 환경
- `main` 브랜치: 프로덕션(`prod`) 환경
- `stage` 브랜치: 스테이징(`stage`) 환경
- `dev` 브랜치: 개발(`dev`) 환경

### 워크플로우
1. GitHub에 코드 푸시/PR 생성
2. GitHub Actions에서 변경된 모듈 감지 및 테스트/빌드
3. 빌드 성공 시 Jenkins 작업 트리거
4. Jenkins에서 Docker 이미지 빌드 및 Docker Hub에 푸시
5. Jenkins에서 Kubernetes에 배포

### GitHub Actions 설정
GitHub 프로젝트 설정에 다음 시크릿을 추가:
- `JENKINS_URL`: Jenkins URL (예: http://jenkins.example.com)
- `JENKINS_USER`: Jenkins 사용자 이름
- `JENKINS_TOKEN`: Jenkins API 토큰
- `JENKINS_JOB_NAME`: 트리거할 Jenkins 작업 이름
- `JENKINS_API_TOKEN`: Jenkins 작업 트리거 토큰

### Jenkins 설정
Jenkins에 다음 자격 증명을 추가:
- `dockerhub-creds`: DockerHub 자격 증명 (ID/패스워드)

### 수동 배포
1. Jenkins 대시보드에서 작업 선택
2. "Build with Parameters" 클릭
3. 파라미터 설정:
   - `BUILD_MODE`: `all` 또는 `only_changed`
   - `MODULE`: 빌드할 모듈 또는 `all` 선택
   - `DEPLOY_NAMESPACE`: 배포할 쿠버네티스 네임스페이스 선택

### 배포 모니터링
- 빌드 및 배포 과정은 Jenkins 콘솔 출력에서 확인
- 빌드 결과는 Slack 또는 이메일로 통보
