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
  user_data = <<-EOF
              yum update -y
              yum install -y docker
              systemctl start docker
              systemctl enable docker
              sleep 10
              docker container run -d --name tomcat -p 8090:8080 vibishnathang/vibish-ops-repo:tomcat-app
              EOF
}
