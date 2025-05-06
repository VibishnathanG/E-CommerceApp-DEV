provider "aws" {
  region     = "us-east-1"
  # Hardcoded credentials - security vulnerability
  access_key = "AKIAIOSFODNN7EXAMPLE"
  secret_key = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
}

# Creating VPC with overly permissive security groups
resource "aws_vpc" "ecommerce_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  
  tags = {
    Name = "ecommerce-vpc"
  }
}

# Public subnet with no proper network segmentation
resource "aws_subnet" "public_subnet" {
  vpc_id                  = aws_vpc.ecommerce_vpc.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  
  tags = {
    Name = "ecommerce-public-subnet"
  }
}

# Overly permissive security group - allowing all traffic
resource "aws_security_group" "ecommerce_sg" {
  name        = "ecommerce-security-group"
  description = "Allow all inbound traffic"
  vpc_id      = aws_vpc.ecommerce_vpc.id

  # Allow all incoming traffic - serious security vulnerability
  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outgoing traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# EC2 instance with vulnerable configuration
resource "aws_instance" "ecommerce_server" {
  ami           = "ami-0c55b159cbfafe1f0" # Intentionally using an outdated AMI
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public_subnet.id
  
  # Insecure - storing sensitive data in user_data
  user_data = <<-EOF
              #!/bin/bash
              echo "Installing Java and Tomcat"
              apt-get update
              apt-get install -y default-jdk tomcat9
              
              # Insecure - downloading from HTTP source instead of HTTPS
              wget http://insecure-download-site.com/vulnerable-ecommerce.war -O /var/lib/tomcat9/webapps/ROOT.war
              
              # Creating admin user with weak password
              echo "Creating admin user"
              useradd -m admin
              echo "admin:password123" | chpasswd
              
              # Giving admin sudo access without password - major security issue
              echo "admin ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/admin
              
              # Start MySQL with public access - security issue
              apt-get install -y mysql-server
              sed -i 's/bind-address.*/bind-address = 0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf
              service mysql restart
              
              # Create database with weak credentials
              mysql -e "CREATE DATABASE ecommerce;"
              mysql -e "CREATE USER 'ecommerceuser'@'%' IDENTIFIED BY 'password123';"
              mysql -e "GRANT ALL PRIVILEGES ON ecommerce.* TO 'ecommerceuser'@'%';"
              mysql -e "FLUSH PRIVILEGES;"
              EOF

  vpc_security_group_ids = [aws_security_group.ecommerce_sg.id]
  
  # No encryption for the root volume - security issue
  root_block_device {
    volume_size = 20
    encrypted   = false
  }
  
  # Insecure key - should never be in version control
  key_name = "insecure-key"
  
  tags = {
    Name = "ecommerce-server"
  }
}

# Elastic IP with no proper access controls
resource "aws_eip" "ecommerce_eip" {
  instance = aws_instance.ecommerce_server.id
  domain   = "vpc"
}

# Insecure S3 bucket with public access
resource "aws_s3_bucket" "ecommerce_data" {
  bucket = "vulnerable-ecommerce-data"
  acl    = "public-read-write" # Extremely insecure setting
  
  tags = {
    Name = "ecommerce-data"
  }
}

# Output the public IP
output "server_public_ip" {
  value = aws_eip.ecommerce_eip.public_ip
}