# 🚀 E-CommerceApp-DEV: DevSecOps Pipeline

This project implements a complete **DevSecOps pipeline** for the `E-CommerceApp-DEV` application using **Jenkins, Terraform, Docker, SonarQube, Trivy, Nexus, Maven and Snyk**. It automates code quality checks, security scanning, artifact management, and infrastructure deployment to AWS.

---

## 📦 Features

- 🔁 CI/CD with Jenkins Pipeline
- 🔍 Static Analysis with **SonarQube**
- 🧾 Software Bill of Materials (SBOM) with **Trivy**
- 🐳 Container Image Build & Scan with Docker + Trivy
- 📦 Artifact storage in **Nexus Repository**
- ⚙️ IaC Scanning using **Snyk**
- ☁️ Infrastructure provisioning via **Terraform** on AWS EC2
- 🐱‍💻 Deployment on **Apache Tomcat**

---

## 🛠️ Pipeline Stages

1. **Git Pull Source Code**  
   Clones source from GitHub and cleans previous workspace.

2. **SAST Scan on SonarQube**  
   Static code analysis and Maven build with SonarQube.

3. **SonarQube Quality Gate Check**  
   Waits for quality gate status; fails pipeline if not passed.

4. **SBOM Scan with Trivy**  
   Scans app dependencies and generates `sbom.json`.

5. **Upload Artifact to Nexus**  
   Uploads WAR file to Nexus for versioned storage.

6. **Docker Image Build**  
   Builds and pushes app Docker image to Docker Hub.

7. **Trivy Image Scan**  
   Scans Docker image for known CVEs (outputs JSON).

8. **Scanning IaC Code with Snyk**  
   Detects misconfigurations in Terraform using Snyk IaC.

9. **Setup Tomcat on EC2 with Terraform**  
   Provisions EC2 and deploys the app on Apache Tomcat.

---

## ✅ Post Actions

- **Always**: Cleanup workspace
- **On Success**:
  - 🌐 App URL: `http://<public-ip-of-ResuledDeployementInstance>:8090/`
  - 📊 SonarQube: `http://<sonarqube-host>/dashboard?id=E-CommerceApp-DEV`
  - 📄 SBOM Report: `http://<public-ip>:8080/reports/sbom.json`
- **On Failure**: Pipeline logs failure message

---

## 🧰 Tools & Technologies

| Tool        | Purpose                                  |
|-------------|-------------------------------------------|
| Jenkins     | Pipeline orchestration                   |
| SonarQube   | Static code analysis                     |
| Trivy       | SBOM and container vulnerability scans   |
| Snyk        | IaC vulnerability scanning               |
| Nexus       | Artifact repository                      |
| Docker      | Containerization                         |
| Terraform   | Infrastructure as Code (IaC)             |
| Apache Tomcat | Java app deployment on EC2             |
| Maven        | Build Automation Tool                   |
---

## 📋 Prerequisites

### Jenkins Setup
- Install plugins:
  - `SonarQube Scanner`
  - `Pipeline`
  - `Docker Pipeline`
- Add Jenkins credentials:
  - `git-creds`: GitHub credentials
  - `nexus-creds`: Nexus credentials
  - `snyk-token`: Snyk API token

### AWS Setup
- S3 bucket and DynamoDB table for Terraform backend
- IAM role with permissions: EC2, S3, DynamoDB

### SonarQube
- Configure under: `Manage Jenkins → Configure System`
- Add SonarQube server details

### Docker Hub
- Ensure Docker Hub repo exists
- Provide correct credentials in Jenkins

---

## 🧪 How to Run the Pipeline

```bash
# On Jenkins server:
1. Clone this repo
2. Create a pipeline job in Jenkins
3. Point it to the Jenkinsfile
4. Run the pipeline
