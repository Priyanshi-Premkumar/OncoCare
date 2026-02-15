# System Design Document

# OncoCare

## Personalized Liver Drift Monitoring for Cancer Patients

------------------------------------------------------------------------

## 1. System Overview

OncoCare is a personalized liver drift detection system designed for
cancer patients undergoing treatment.

Unlike traditional threshold-based monitoring, OncoCare compares each
patient to their own historical baseline to detect subtle instability
patterns early and enable proactive clinical intervention.

The system follows a scalable microservices architecture for modularity,
flexibility, and hospital-ready deployment.

------------------------------------------------------------------------

## 2. High-Level Architecture

Hospital Lab System
↓
REST API Ingestion
↓
Spring Boot Backend
↓
PostgreSQL Database
↓
AI Drift Engine
↓
Risk Score + Explanation
↓
React Dashboard

![alt text](image-1.png)

------------------------------------------------------------------------

## 3. Architecture Components

### 3.1 Backend Service (Spring Boot)

Responsibilities: - Patient registration & profile management
- Lab result ingestion
- Data validation and preprocessing
- API exposure
- Communication with AI Drift Engine
- Authentication & authorization

Key APIs: - POST /patients
- POST /labs
- GET /risk/{patientId}
- GET /history/{patientId}

------------------------------------------------------------------------

### 3.2 AI Drift Engine (Python + FastAPI)

Responsibilities: - Generate Patient Digital Liver Profile (30--60 day
baseline)
- Perform personalized anomaly detection
- Compute Drift Score (0--100 scale)
- Assign Risk Level (Low / Moderate / High)
- Provide explainable marker-level insights

ML Techniques: - Isolation Forest
- Statistical deviation modeling
- Rolling baseline comparison

------------------------------------------------------------------------

### 3.3 Database Design (PostgreSQL)

Core Tables:

patients - patient_id (PK)
- name
- age
- gender

lab_records - record_id (PK)
- patient_id (FK) 
- bilirubin 
- INR 
- ALT 
- albumin 
- timestamp

treatment_history - treatment_id (PK) 
- patient_id (FK) 
- medication 
- dosage 
- start_date 
- end_date

------------------------------------------------------------------------

## 4. Data Flow Design

1.  Lab results are sent from hospital systems via REST API. 
2.  Backend validates and stores data in PostgreSQL. 
3.  Backend retrieves historical data (30--60 days). 
4.  Data is sent to AI Drift Engine. 
5.  AI engine calculates drift score and explanation. 
6.  Risk output is stored and returned to frontend. 
7.  Dashboard visualizes trends and alerts.

------------------------------------------------------------------------

## 5. Deployment Architecture (AWS)

Cloud Components: - EC2 / ECS -- Service hosting 
- RDS -- Managed PostgreSQL 
- S3 -- Model storage 
- Docker -- Containerization

------------------------------------------------------------------------

## 6. Scalability Strategy

-   Stateless backend services 
-   Horizontally scalable AI engine 
-   Load balancer for traffic distribution 
-   Managed database via AWS RDS 
-   Container orchestration via ECS

------------------------------------------------------------------------

## 7. Security & Compliance

-   JWT-based authentication 
-   Role-based access (Doctor / Admin) 
-   HTTPS secure communication 
-   Encrypted data at rest and in transit 
-   Audit logging for lab access

------------------------------------------------------------------------

## 8. Explainability Design

Each alert includes: - Drift Score 
- Risk Tier 
- Marker-level contribution analysis 
- Trend visualization

------------------------------------------------------------------------

## 9. Design Principles

-   Personalized over population-based modeling 
-   Early instability detection 
-   Explainable AI 
-   Modular microservices architecture 
-   Cloud-native and hospital-ready
