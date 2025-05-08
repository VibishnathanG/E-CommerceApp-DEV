#!/bin/bash

echo "Server Setup - TOMCAT10"
yum update -y
yum install -y docker
systemctl start docker
systemctl enable docker
sleep 10
docker container run -d --name tomcat -p 8090:8080 vibishnathang/vibish-ops-repo:tomcat-app

# Fetch the public IP using Amazon's public IP service and display the application URL
PUBLIC_IP=$(curl -s https://checkip.amazonaws.com)
echo "Access the application at: http://${PUBLIC_IP}:8090/"