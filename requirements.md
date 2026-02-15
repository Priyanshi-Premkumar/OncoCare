### 1. Problem Statement

Traditional liver monitoring systems rely on fixed laboratory thresholds or population-based models, detecting risk only after clinical deterioration begins. There is a need for a personalized system that identifies subtle liver instability earlier by comparing patients against their own historical baseline.

### 2. Objectives

Detect personalized liver function drift before clinical abnormality thresholds are crossed
Provide explainable risk insights to clinicians
Enable proactive treatment adjustments
Ensure scalability and deployability in real hospital environments

### 3. Functional Requirements
### 3.1 Patient Data Management
Store patient laboratory history (bilirubin, INR, ALT, albumin, etc.)
Store treatment and medication history
Maintain 30–60 days of historical data for baseline creation

### 3.2 Digital Liver Profile Generation
Generate a personalized baseline using historical lab data
Update baseline dynamically as new data arrives

### 3.3 Drift Detection Engine
Accept new lab values via API
Perform anomaly detection using lightweight ML models (e.g., Isolation Forest, statistical deviation modeling)
Compute a Drift Score (0–100 scale)
Assign risk level (Low / Moderate / High)
Identify and report contributing biomarkers

### 3.4 Alert & Visualization
Display drift score and risk level on dashboard
Show lab trend charts
Provide explainable marker contribution breakdown
Generate early warning alerts for clinicians

### 3.5 API & Integration
Provide REST APIs for lab ingestion
Enable integration with hospital lab systems
Support secure authentication and role-based access

### 4. Non-Functional Requirements
4.1 Performance
Real-time or near real-time drift analysis (<5 seconds response time)
Handle concurrent patient data processing

### 4.2 Scalability
Microservices-based architecture
Horizontally scalable AI engine

### 4.3 Reliability
Ensure high availability of backend services
Maintain data integrity and consistency

### 4.4 Security
Secure API endpoints (JWT or OAuth-based authentication)
Encrypt sensitive patient data at rest and in transit
Comply with healthcare data privacy standards

### 4.5 Maintainability
Modular service separation (Backend, AI Engine, Frontend)
Containerized deployment using Docker

### 5. Assumptions
Patients have at least 30 days of prior lab history
Lab data is structured and standardized
Hospitals allow API-level integration

### 6. Constraints
Data availability may vary across institutions
Model accuracy depends on quality of historical records
Compliance with healthcare regulations must be ensured

### 7. Technology Stack
Backend: Spring Boot
AI Engine: Python + FastAPI
Database: PostgreSQL
Frontend: React
Cloud: AWS (EC2/ECS, S3, RDS)   
Containerization: Docker
