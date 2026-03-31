# LiverCare — Full Stack Project
### Early Liver Instability Detection in Cancer Care
**Team RealIntel · AI for Bharat Hackathon · Powered by AWS**

---

## Project Structure

```
livercare-full/
├── backend/          Spring Boot 3.2 — REST API, ingestion, baseline, alerts
├── ai-engine/        Python 3.11 + FastAPI — Isolation Forest drift detection
├── frontend/         React 18 — Dashboard, trend charts, alert UI
└── infra/            Docker Compose + AWS deployment configs
```

---

## Quick Start (Local)

```bash
# 1. Start everything
docker compose up --build

# 2. Access points
#    React Dashboard    →  http://localhost:3000
#    Spring Boot API    →  http://localhost:8080/api
#    Swagger UI         →  http://localhost:8080/api/swagger-ui.html
#    AI Drift Engine    →  http://localhost:8000/docs
#    PostgreSQL         →  localhost:5432

# Credentials
# API Basic Auth: admin / livercare2024
```

---

## Architecture

```
Browser
  └─► React Dashboard (port 3000)
           │
           ▼
    Spring Boot API (port 8080)
           │
           ├──► PostgreSQL (port 5432)   — patient + lab + alert storage
           │
           ├──► AI Drift Engine (port 8000)  — Isolation Forest + Z-score
           │
           └──► Amazon Bedrock (Claude 3 Sonnet)  — NLG alert narratives
```

---

## AWS Services Used

| Service | Purpose |
|---------|---------|
| **Amazon Bedrock** | Claude 3 Sonnet for clinical NLG |
| **ECS Fargate** | Containerised service hosting |
| **RDS PostgreSQL** | Managed database |
| **S3** | ML model + report storage |
| **ALB** | HTTPS load balancing |
| **CloudWatch** | Logs + monitoring |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17 · Spring Boot 3.2 · Spring Security · Flyway |
| AI Engine | Python 3.11 · FastAPI · scikit-learn · Isolation Forest |
| LLM | Anthropic Claude 3 Sonnet via Amazon Bedrock |
| Database | PostgreSQL 15 |
| Frontend | React 18 · Recharts · TailwindCSS |
| Infrastructure | Docker · AWS ECS/RDS/S3/Bedrock |
