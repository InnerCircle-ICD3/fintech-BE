#!/bin/bash

# Java 설치
sudo apt update
sudo apt install -y openjdk-21-jdk

# 애플리케이션 디렉토리 생성
sudo mkdir -p /opt/backoffice-api
sudo chown $USER:$USER /opt/backoffice-api

# systemd 서비스 파일 생성
sudo tee /etc/systemd/system/backoffice-api.service << EOF
[Unit]
Description=Backoffice API Service
After=network.target postgresql.service

[Service]
User=$USER
WorkingDirectory=/opt/backoffice-api
ExecStart=/usr/bin/java -jar /opt/backoffice-api/backoffice-api.jar
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

# 서비스 활성화
sudo systemctl daemon-reload
sudo systemctl enable backoffice-api
sudo systemctl start backoffice-api

# 로그 확인
sudo journalctl -u backoffice-api -f 