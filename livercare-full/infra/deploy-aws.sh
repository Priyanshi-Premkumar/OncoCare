# ============================================================
#  LiverCare — AWS Deployment Guide
#  AI for Bharat Hackathon · Team RealIntel
# ============================================================

## Prerequisites
# - AWS CLI configured: aws configure
# - Docker installed
# - AWS account with Bedrock access (Claude 3 Sonnet enabled in us-east-1)

AWS_REGION=us-east-1
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REGISTRY=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# ── Step 1: Enable Bedrock model access ─────────────────────
# In AWS Console → Amazon Bedrock → Model access
# Enable: anthropic.claude-3-sonnet-20240229-v1:0

# ── Step 2: Create ECR repositories ─────────────────────────
aws ecr create-repository --repository-name livercare-backend  --region $AWS_REGION
aws ecr create-repository --repository-name livercare-ai-engine --region $AWS_REGION
aws ecr create-repository --repository-name livercare-frontend  --region $AWS_REGION

# ── Step 3: Build & push images ─────────────────────────────
aws ecr get-login-password --region $AWS_REGION | \
  docker login --username AWS --password-stdin $ECR_REGISTRY

docker build -t livercare-backend  ./backend
docker build -t livercare-ai-engine ./ai-engine
docker build -t livercare-frontend  ./frontend

docker tag livercare-backend   $ECR_REGISTRY/livercare-backend:latest
docker tag livercare-ai-engine $ECR_REGISTRY/livercare-ai-engine:latest
docker tag livercare-frontend  $ECR_REGISTRY/livercare-frontend:latest

docker push $ECR_REGISTRY/livercare-backend:latest
docker push $ECR_REGISTRY/livercare-ai-engine:latest
docker push $ECR_REGISTRY/livercare-frontend:latest

# ── Step 4: Create RDS PostgreSQL ───────────────────────────
aws rds create-db-instance \
  --db-instance-identifier livercare-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 15.4 \
  --master-username livercare \
  --master-user-password livercare_secret_prod \
  --db-name livercaredb \
  --allocated-storage 20 \
  --storage-type gp2 \
  --publicly-accessible false \
  --multi-az false \
  --region $AWS_REGION

# ── Step 5: Create ECS cluster ──────────────────────────────
aws ecs create-cluster \
  --cluster-name livercare-cluster \
  --capacity-providers FARGATE \
  --region $AWS_REGION

# ── Step 6: Create IAM role for Bedrock access ──────────────
# The ECS task role needs bedrock:InvokeModel permission
cat > /tmp/bedrock-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": ["bedrock:InvokeModel"],
    "Resource": "arn:aws:bedrock:us-east-1::foundation-model/anthropic.claude-3-sonnet-20240229-v1:0"
  }]
}
EOF

aws iam create-policy \
  --policy-name LiverCareBedrockPolicy \
  --policy-document file:///tmp/bedrock-policy.json

# Attach to ECS task role after creating it in console

# ── Step 7: Deploy with ECS task definitions ────────────────
# See ecs-task-definitions/ directory for JSON task definition files
# Register and run via:
#   aws ecs register-task-definition --cli-input-json file://ecs-task-definitions/backend.json
#   aws ecs create-service --cluster livercare-cluster --service-name livercare-backend ...

echo "Deployment steps complete. See AWS Console for service status."
