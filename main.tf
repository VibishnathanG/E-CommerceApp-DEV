provider "aws" {
  region = "ap-south-1"
}
resource "aws_instance" "tomcat_server" {
  ami                         = "ami-0f1dcc636b69a6438"
  instance_type               = "t2.micro"
  subnet_id                   = "subnet-0eb08298f65c9596d" # Replace with actual subnet ID for 'Main-VPC-subnet-public1-ap-south-1a'
  vpc_security_group_ids      = ["sg-0c0339010561d889f"]
  associate_public_ip_address = true
  tags = {
    Name = "TomcatServer"
  }
  key_name = "ap-south-keypair"
  provisioner "file" {
    source      = "startup.sh" 
    destination = "/tmp/startup.sh"
    
  }
  provisioner "remote-exec" {
    inline = [
      "chmod +x /tmp/startup.sh",
      "/tmp/startup.sh",
    ]
  }
  
}
