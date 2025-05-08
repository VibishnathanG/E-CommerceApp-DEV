pipeline {
    agent any

    environment {
        GIT_URL = 'https://github.com/VibishnathanG/E-CommerceApp-DEV.git'
        MAVEN_HOME = tool 'Default Maven'
        SBOM_OUTPUT = 'sbom.json'
        TARGET_DIR = 'target'
        SONAR_HOST_URL = 'http://localhost:9000'
        NEXUS_URL = 'http://10.0.14.233:8081'
        NEXUS_REPO = '/repository/maven-releases/'
        NEXUS_CREDENTIALS_ID = 'nexus-creds'
        DOCKER_BUILD_NAME = 'jakartaee9-app'
    }

    stages {
        stage('Git Pull Source Code') {
            steps {
                echo 'Pulling source code...'
                withCredentials([usernamePassword(credentialsId: 'git-creds', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                    sh '''
                        echo "Cleaning up previous workspace..."
                        rm -rf E-CommerceApp-DEV
                        git clone https://${GIT_USER}:${GIT_PASS}@github.com/VibishnathanG/E-CommerceApp-DEV.git
                        cd E-CommerceApp-DEV/
                        ls -lrt
                    '''
                }
            }
        }

        stage('Starting SAST Scan on SonarQube for E-CommerceApp-DEV') {
            steps {
                echo 'Running SonarQube scan...'
                dir('E-CommerceApp-DEV') {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            ${MAVEN_HOME} clean package verify sonar:sonar \
                            -Dsonar.projectKey=E-CommerceApp-DEV \
                            -Dsonar.projectName='E-CommerceApp-DEV' \
                            -Dsonar.host.url=${SONAR_HOST_URL}
                        '''
                    }
                }
            }
        }

        stage('SonarQube Quality Gate Check') {
            steps {
                echo 'Waiting for SonarQube quality gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('SBOM Scan With Trivy') {
            steps {
                echo 'Running Trivy SBOM scan...'
                dir('E-CommerceApp-DEV') {
                    sh '''
                        mkdir -p reports
                        trivy fs --format cyclonedx --output "reports/${SBOM_OUTPUT}" "${TARGET_DIR}/jakartaee9-servlet.war"
                    '''
                }
            }
        }

        stage('upload Artifact to Nexus') {
            steps {
                echo 'Uploading artifact to Nexus...'
                dir('E-CommerceApp-DEV') {
                    withCredentials([usernamePassword(credentialsId: NEXUS_CREDENTIALS_ID, passwordVariable: 'NEXUS_PASS', usernameVariable: 'NEXUS_USER')]) {
                        sh '''
                            echo "Uploading WAR file to Nexus..."
                            ls -lrt
                            echo "WAR file path: ${TARGET_DIR}/jakartaee9-servlet.war"
                            curl -v -u admin:vibishnathan --upload-file target/jakartaee9-servlet.war http://13.233.73.72:8081/repository/maven-releases/com/microsoft/example/jakartaee9-servlet/1.0.2/jakartaee9-servlet-1.0.2.war
                        '''
                    }
                }
            }
        }

        stage('Docker Image Build') {
            steps{
                sh '''
                echo 'Building Docker image...'
                docker build -t ${DOCKER_BUILD_NAME} .
                docker image tag tomcat-app vibishnathang/vibish-ops-repo:tomcat-app
                docker push vibishnathang/vibish-ops-repo:tomcat-app
                echo 'Docker image built and pushed successfully.'
                echo 'Image can be used to deploy the application. with Following Command ::: docker push vibishnathang/vibish-ops-repo:tomcat-app'
                '''
            }
        }

        stage('Trivy Image Scan') {
            steps {
                echo 'Running Trivy image scan...'
                sh '''
                    trivy image --format json --output "reports/trivy-image-scan.json" vibishnathang/vibish-ops-repo:tomcat-app
                    echo 'Trivy image scan completed.'
                '''
            }
        }
        
        stage('Displaying Trivy Image Scan Report') {
            steps {
                echo 'Displaying Trivy image scan report...'
                sh '''
                    cat reports/trivy-image-scan.json
                    echo 'Trivy image scan report displayed.'
                '''
            }
        }

        stage('Scanning IAC Code with Synk') {
            steps {
                echo 'Running Snyk IaC scan...'
                dir('E-CommerceApp-DEV') {
                    sh '''
                        snyk iac test --all-projects --json-file-output=reports/snyk-iac-scan.json
                        echo 'Snyk IaC scan completed.'
                    '''
                }
            }
        }
        stage('Pushing Snyk report to synk dashboard') {
            steps {
                echo 'Pushing Snyk report to Snyk dashboard...'
                dir('E-CommerceApp-DEV') {
                    sh '''
                        snyk monitor --all-projects --json-file-output=reports/snyk-iac-scan.json
                        echo 'Snyk report pushed to Snyk dashboard.'
                    '''
                }
            }
        }
        stage('Setting up Tomcat Server on EC2 with terraform'){

            steps {
                sh '''
                    echo "Setting up Tomcat server on EC2 with Terraform..."
                    ls -lrt
                    terraform init
                    terraform apply -auto-approve
                    echo "Tomcat server setup completed."
                '''
            }
        }
        
    }

    post {
        always {
            echo 'Cleaning up...'
            sh 'rm -rf E-CommerceApp-DEV'
        }
        success {
            echo 'Pipeline completed successfully.'
            sh '''
                PUBLIC_IP=$(curl -s https://checkip.amazonaws.com)
                echo "Access the application at: http://${PUBLIC_IP}:8090/jakartaee9-servlet"
                echo "Access the SonarQube report at: http://${SONAR_HOST_URL}/dashboard?id=E-CommerceApp-DEV"
                echo "Access SBOM report at: http://${PUBLIC_IP}:8080/reports/${SBOM_OUTPUT}"
            '''
        }
        failure {
            echo 'Pipeline failed.'
        }
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }
}