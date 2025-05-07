pipeline {
    agent any
    environment {
        GIT_URL = 'https://github.com/VibishnathanG/E-CommerceApp-DEV.git'
        MAVEN_HOME = tool 'Default Maven'
    }
    stages {
        stage('Git Pull Source Code') { 
            steps {
                echo 'Pulling source code...'
                withCredentials([usernamePassword(credentialsId: 'git-creds', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                    sh '''
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
                    sh "${MAVEN_HOME} clean verify sonar:sonar -Dsonar.projectKey=E-CommerceApp-DEV -Dsonar.projectName='E-CommerceApp-DEV'"
                }
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
            }
        }
    }
    post {
        always {
            echo 'Cleaning up...'
            sh '''
                cd E-CommerceApp-DEV
                rm -rf .git
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