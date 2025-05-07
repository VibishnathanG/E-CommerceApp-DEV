pipeline {
    agent any
    environment {
        GIT_URL = 'https://github.com/VibishnathanG/E-CommerceApp-DEV.git'
        MAVEN_HOME = tool 'Default Maven'
        SBOM_OUTPUT = 'sbom.json'
        TARGET_DIR = '/var/lib/jenkins/workspace/Devsecops-Pipeline/E-CommerceApp-DEV/reports/'
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
                echo 'Starting SAST Scan on SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME} clean install verify sonar:sonar -Dsonar.projectKey=E-CommerceApp-DEV -Dsonar.projectName='E-CommerceApp-DEV'"
                }
            }
        }
        stage('SBOM Scan With Trivy') {
            steps {
                sh 'mkdir -p /var/lib/jenkins/workspace/Devsecops-Pipeline/E-CommerceApp-DEV/reports/'
                sh 'trivy sbom --format cyclonedx --output "$SBOM_OUTPUT" "$TARGET_DIR"'
            }
        }
        stage('wait for SonarQube analysis') {
            steps {
                script {
                    timeout(time: 10, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
        stage('SonarQube Quality Gate') {
            steps {
                script {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                        error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    } else {
                        echo "Quality gate passed: ${qg.status}"
                    }
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