pipeline {
    agent any

    environment {
        GIT_URL = 'https://github.com/VibishnathanG/E-CommerceApp-DEV.git'
        MAVEN_HOME = tool 'Default Maven'
        SBOM_OUTPUT = 'sbom.json'
        TARGET_DIR = 'target'
        SONAR_HOST_URL = 'http://localhost:9000'
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
                    withCredentials([string(credentialsId: 'SonarQube', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            ${MAVEN_HOME} clean package verify sonar:sonar \
                            -Dsonar.projectKey=E-CommerceApp-DEV \
                            -Dsonar.projectName='E-CommerceApp-DEV' \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_TOKEN}
                        '''
                    }
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

        stage('SonarQube Quality Gate Check') {
            steps {
                echo 'Waiting for SonarQube quality gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Tomcat Deployment - Copying WAR file to Tomcat') {
            steps {
                echo 'Deploying WAR file...'
                sh '''
                        echo "Stopping Tomcat..."
                        sudo tomcatdown
                        echo "Copying WAR..."
                        sudo cp ${TARGET_DIR}/*.war /opt/tomcat/webapps/
                        echo "Starting Tomcat..."
                        sudo tomcatup
                    '''
                }
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
