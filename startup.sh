#!/bin/bash

echo "Server Setup - TOMCAT10"
yum update -y
yum install -y docker
systemctl start docker
systemctl enable docker
sleep 10
docker container run -d --name tomcat -p 8090:8080 vibishnathang/vibish-ops-repo:tomcat-app