# Base image with Tomcat 10
FROM tomcat:10-jdk17-corretto

# Maintainer info (optional)
LABEL maintainer="vibishnathan"

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file from build context to webapps
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat default port
EXPOSE 8080

# Start Tomcat (already in base image's CMD)