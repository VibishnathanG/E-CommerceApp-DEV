provider "aws" {
  region = "ap-south-1"
}

terraform {
  backend "s3" {
    bucket         = "vibish-terraform-bucket" # Replace with your S3 bucket name
    key            = "ecommerce-app/terraform.tfstate"
    region         = "ap-south-1"
    dynamodb_table = "terraform-lock-table" # DynamoDB table for state locking
  }
}
# Create EC2 instance for Tomcat server
resource "aws_instance" "tomcat_server" {
  ami                         = "ami-0f1dcc636b69a6438"
  instance_type               = "t2.micro"
  subnet_id                   = "subnet-0eb08298f65c9596d" # Replace with actual subnet ID
  vpc_security_group_ids      = ["sg-0c0339010561d889f"]  # Replace with actual security group ID
  associate_public_ip_address = true
  key_name                    = "terraform-keypair"

  tags = {
    Name = "TomcatServer"
    Environment = "DevSecOps"
  }

  # SSH connection configuration
  connection {
    type        = "ssh"
    host        = self.public_ip
    user        = "ec2-user"
    private_key = file("~/.ssh/terraform-keypair.pem") # Replace with the path to your private key
  }

  # File provisioner to copy startup script
  provisioner "file" {
    source      = "startup.sh"
    destination = "/tmp/startup.sh"
  }

  # Remote execution provisioner to run the startup script
  provisioner "remote-exec" {
    inline = [
      "sudo chmod +x /tmp/startup.sh",
      "sudo /tmp/startup.sh",
    ]
  }
}