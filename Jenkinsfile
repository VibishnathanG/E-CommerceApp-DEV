pipeline {
    agent any

    environment {
        GIT_URL = 'https://github.com/VibishnathanG/E-CommerceApp-DEV.git'
        MAVEN_HOME = tool 'Default Maven'
        SBOM_OUTPUT = 'sbom.json'
        TARGET_DIR = '/var/lib/jenkins/workspace/Devsecops-Pipeline/target'
    }

    stages {
        stage('Git Pull Source Code') {
            steps {
                echo 'Pulling source code...'
                withCredentials([usernamePassword(credentialsId: 'git-creds', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                    sh '''
                        echo "Removing existing Build directory..."
                        rm -rf E-CommerceApp-DEV 2>/dev/null || true
                        git config --global credential.helper store
                        git clone https://${GIT_USER}:${GIT_PASS}@${GIT_URL#https://}
                        cd E-CommerceApp-DEV
                        echo "Source code pulled successfully"
                    '''
                }
            }
        }

        stage('Starting SAST Scan on SonarQube for E-CommerceApp-DEV') {
            steps {
                echo 'Starting SAST scan...'
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME} clean install verify sonar:sonar -Dsonar.projectKey=E-CommerceApp-DEV -Dsonar.projectName='E-CommerceApp-DEV'"
                }
            }
        }

        stage('SBOM Scan With Trivy') {
            steps {
                echo 'Running Trivy SBOM scan...'
                sh '''
                    mkdir -p /var/lib/jenkins/workspace/Devsecops-Pipeline/reports/
                    trivy fs --format cyclonedx --output "/var/lib/jenkins/workspace/Devsecops-Pipeline/reports/${SBOM_OUTPUT}" "${TARGET_DIR}/jakartaee9-servlet.war"
                    echo "SBOM scan completed successfully"
                '''
            }
        }

        stage('SAST Scan Results') {
            steps {
                echo 'Displaying SAST scan results...'
                script {
                    def sonarReport = readFile("${WORKSPACE}/E-CommerceApp-DEV/target/sonar/report-task.txt")
                    def reportUrl = sonarReport.find(/(?<=reportUrl=).+/)
                    echo "SonarQube report URL: ${reportUrl}"
                }
            }
        }

        stage("Tomcat Deployment - Copying WAR file to Tomcat") {
            steps {
                echo 'Deploying WAR file to Tomcat...'
                sh '''
                    echo "Stopping Tomcat..."
                    tomcatdown
                    echo "Tomcat stopped successfully"
                    echo "Copying WAR file to Tomcat..."
                    cp ${TARGET_DIR}/*.war /opt/tomcat/webapps/
                    echo "WAR file copied successfully"
                    tomcatup
                    echo "Tomcat started successfully"
                    echo "Deployment completed successfully"
                '''
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
            sh '''
                cd /var/lib/jenkins/workspace/Devsecops-Pipeline
                rm -rf E-CommerceApp-DEV
                echo "Cleanup completed"
            '''
        }

        success {
            echo 'Pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed!'
        }
    }
}