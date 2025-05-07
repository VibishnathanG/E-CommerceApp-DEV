pipeline {
    agent any
    environment {
        GIT_URL = 'https://github.com/VibishnathanG/E-CommerceApp-DEV.git'
        MAVEN_HOME = tool 'Default Maven'
        SBOM_OUTPUT = 'sbom.json'
        TARGET_DIR = 'target'
        SONAR_TOKEN = credentials('SonarQube')
    }

    stages {
        stage('Git Pull Source Code') {
            steps {
                echo 'Pulling source code...'
                withCredentials([usernamePassword(credentialsId: 'git-creds', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                    sh '''
                        pwd
                        echo "Removing existing Build directory..."
                        rm -rf E-CommerceApp-DEV
                        echo "Build directory removed successfully"
                        git clone ${GIT_URL}
                        cd E-CommerceApp-DEV/
                        ls -lrt
                        echo "Source code pulled successfully"
                    '''
                }
            }
        }

        stage('Starting SAST Scan on SonarQube for E-CommerceApp-DEV') {
            steps {
                echo 'Starting SAST scan...'
                sh '''
                    ${MAVEN_HOME} clean package verify sonar:sonar \
                    -Dsonar.projectKey=E-CommerceApp-DEV \
                    -Dsonar.projectName='E-CommerceApp-DEV'
                '''
            }
        }

        stage('SBOM Scan With Trivy') {
            steps {
                echo 'Running Trivy SBOM scan...'
                sh '''
                    pwd
                    mkdir reports
                    trivy fs --format cyclonedx --output "reports/${SBOM_OUTPUT}" "${TARGET_DIR}/jakartaee9-servlet.war"
                    echo "SBOM scan completed successfully"
                '''
            }
        }

        stage('SonarQube Quality Gate Check') {
            steps {
                echo 'Checking SonarQube quality gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
                echo 'SonarQube quality gate passed!'
            }
        }

        stage("Tomcat Deployment - Copying WAR file to Tomcat") {
            steps {
                echo 'Deploying WAR file to Tomcat...'
                sh '''
                    echo "Stopping Tomcat..."
                    sudo tomcatdown
                    echo "Tomcat stopped successfully"
                    echo "Copying WAR file to Tomcat..."
                    sudo cp ${TARGET_DIR}/*.war /opt/tomcat/webapps/
                    echo "WAR file copied successfully"
                    sudo tomcatup
                    echo "Starting Tomcat..."
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
                rm -rf Devsecops-Pipeline*
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