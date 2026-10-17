provider "aws" {
  region = var.region

  default_tags {
    tags = {
      Project     = "enterprise-java-devops"
      ManagedBy   = "Terraform"
      Environment = var.environment
    }
  }
}
