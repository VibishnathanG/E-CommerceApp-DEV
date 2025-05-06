pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8.1'
        jdk 'JDK 8'
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                // OWASP Dependency Check - intentionally commented out to make the app more vulnerable
                // sh 'mvn org.owasp:dependency-check-maven:check'
                
                // Spotbugs - also commented out to keep vulnerabilities
                // sh 'mvn com.github.spotbugs:spotbugs-maven-plugin:check'
                
                echo 'Security scanning intentionally skipped to preserve vulnerabilities'
            }
        }
        
        stage('Deploy to Staging') {
            steps {
                // Insecure deployment script with hardcoded credentials - security issue!
                sh '''
                    sshpass -p "admin123" scp target/vulnerable-ecommerce.war admin@staging-server:/opt/tomcat/webapps/
                    sshpass -p "admin123" ssh admin@staging-server "sudo systemctl restart tomcat"
                '''
            }
        }
        
        stage('Integration Tests') {
            steps {
                // Insecure test with hardcoded credentials
                sh '''
                    curl -u admin:admin123 http://staging-server:8080/vulnerable-ecommerce/health
                    echo "Integration tests passed" 
                '''
            }
        }
        
        stage('Deploy to Production') {
            steps {
                // Insecure direct deployment to production without proper approval
                sh '''
                    sshpass -p "production123" scp target/vulnerable-ecommerce.war admin@production-server:/opt/tomcat/webapps/
                    sshpass -p "production123" ssh admin@production-server "sudo systemctl restart tomcat"
                '''
                
                // No proper verification of deployment
                echo 'Deployed to production without verification'
            }
        }
    }
    
    post {
        always {
            // Cleanup with a security flaw - leaving sensitive data
            sh 'echo "admin123" > /tmp/deploy_creds.txt'
            sh 'echo "Build completed"'
            // Missing cleanup of credentials file
        }
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}