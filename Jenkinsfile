pipeline {
    agent any

    environment {
        GIT_URL = 'https://github.com/VibishnathanG/E-CommerceApp-DEV.git'
        MAVEN_HOME = tool 'Default Maven'
        SBOM_OUTPUT = 'sbom.json'
        TARGET_DIR = '/var/lib/jenkins/workspace/Devsecops-Pipeline/E-CommerceApp-DEV/targets'
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
                    mkdir -p /var/lib/jenkins/workspace/Devsecops-Pipeline/E-CommerceApp-DEV/reports/
                    for war in ${TARGET_DIR}/*.war; do
                        trivy fs --format cyclonedx --output "/var/lib/jenkins/workspace/Devsecops-Pipeline/E-CommerceApp-DEV/reports/${SBOM_OUTPUT}" "$war"
                    done
                    cat /var/lib/jenkins/workspace/Devsecops-Pipeline/E-CommerceApp-DEV/reports/${SBOM_OUTPUT}" | jq
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
