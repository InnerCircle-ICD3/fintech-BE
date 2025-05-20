# GitHub Actions 배포 설정 가이드

이 가이드는 GitHub Actions를 통해 자동 배포를 설정하는 방법을 설명합니다.

## 필요한 시크릿(Secrets) 설정하기

GitHub 저장소에서 다음 시크릿을 설정해야 합니다:

1. GitHub 저장소로 이동합니다.
2. 'Settings' > 'Secrets and variables' > 'Actions'로 이동합니다.
3. 'New repository secret' 버튼을 클릭하여 다음 시크릿들을 추가합니다:

| 시크릿 이름 | 설명 |
|------------|------|
| `SSH_HOST` | 배포 서버의 IP 주소 또는 도메인 이름 |
| `SSH_USERNAME` | SSH 접속 사용자 이름 |
| `SSH_PRIVATE_KEY` | SSH 접속에 사용할 비공개 키 (전체 키 내용) |
| `SSH_PORT` | SSH 포트 (기본값: 22) |
| `DEPLOY_DIR` | 서버에서 애플리케이션이 배포될 디렉토리 경로 |

## SSH 키 생성 방법

서버에 접속하기 위한 SSH 키가 없다면 다음 단계를 따르세요:

1. 로컬 컴퓨터에서 다음 명령어 실행:
   ```bash
   ssh-keygen -t ed25519 -C "GitHub Actions"
   ```
   
2. 생성된 공개 키를 서버의 `~/.ssh/authorized_keys` 파일에 추가:
   ```bash
   cat ~/.ssh/id_ed25519.pub | ssh username@your-server "cat >> ~/.ssh/authorized_keys"
   ```
   
3. 비공개 키를 GitHub 시크릿으로 등록:
   ```bash
   cat ~/.ssh/id_ed25519
   ```
   위 명령어의 출력을 `SSH_PRIVATE_KEY` 시크릿 값으로 사용

## 워크플로우 작동 확인

설정이 끝나면 `main` 또는 `temp` 브랜치에 변경사항을 푸시하여 배포 워크플로우가 정상적으로 작동하는지 확인할 수 있습니다.

GitHub 저장소의 'Actions' 탭에서 워크플로우 실행 상태를 확인할 수 있습니다. 