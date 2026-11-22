# 🏥 Hospital Management System - Backend API (Spring Boot & AWS ECS Fargate)

[![Java](https://img.shields.io/badge/Java-8%20%2F%20OpenJDK-ED8B00?logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.2.2.RELEASE-6DB33F?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-Amazon%20RDS%20MySQL%208.0-4479A1?logo=mysql&logoColor=white)](https://aws.amazon.com/rds/)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage%20Build-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![AWS ECS](https://img.shields.io/badge/Compute-AWS%20ECS%20Fargate-FF9900?logo=amazon-ecs&logoColor=white)](https://aws.amazon.com/fargate/)
[![Security](https://img.shields.io/badge/Security-Gitleaks%20%26%20Trivy%20Scanned-green?logo=shield)](https://github.com/aquasecurity/trivy)

> **Enterprise-grade RESTful API** for the hospital management platform, built with Spring Boot, packaged as a security-hardened container, and orchestrated on AWS ECS Fargate.

---

## 🔗 Hospital System Ecosystem

This repository is the backend API engine of the decoupled three-tier hospital system:

| Repository | Tech Stack | Role |
| :--- | :--- | :--- |
| [🏗️ `hospital-system-infra`](https://github.com/Dubemphil/hospital-system-infra) | Terraform, AWS (VPC, ECS, RDS, CloudFront, KMS, OIDC) | Core Cloud Infrastructure & Security Foundation |
| [☕ `hospital-system-backend`](https://github.com/Dubemphil/hospital-system-backend) | Spring Boot (Java), MySQL, Docker, Maven | RESTful Healthcare API & Business Logic |
| [⚛️ `hospital-system-frontend`](https://github.com/Dubemphil/hospital-system-frontend) | React 18, HTML5/CSS3, Single-Page App | Patient & Staff Interactive Web Portal |

* **Live Web Portal**: [https://www.devopsstandard.name.ng](https://www.devopsstandard.name.ng)

![Live Web Portal](image-1.png)

* **Live Direct CDN**: [https://d1v3mxc3uivi1w.cloudfront.net](https://d1v3mxc3uivi1w.cloudfront.net)
* **Backend API Health Check**: [https://d1v3mxc3uivi1w.cloudfront.net/api/health](https://d1v3mxc3uivi1w.cloudfront.net/api/health)

![Backend API Health Check Response](image.png)

---

## 🏛️ Architecture & Runtime Overview

<p align="center">
  <img src="./enterprise_hospital_system_cloud_devsecops_blueprint.svg" alt="Enterprise Cloud Architecture and DevSecOps Blueprint" width="100%" />
</p>

```mermaid
flowchart LR
    subgraph Client["Client Tier"]
        React[React Single-Page App / Web Portal]
    end

    subgraph AWS["AWS Cloud (Production Environment)"]
        ALB[Application Load Balancer]
        
        subgraph Fargate["AWS ECS Fargate (Private Subnets)"]
            Container["Spring Boot Container (Port 8080)\nNon-root User: appuser\nJVM CGroup Memory Aware"]
            Health["Health Endpoint (/health)"]
        end

        subgraph RDS_Tier["Isolated Database Subnet"]
            RDS[(Amazon RDS MySQL 8.0\nKMS Encrypted Storage\nTLS 1.2+ Enforced)]
        end

        subgraph Secrets["Security & Identity"]
            SM[AWS Secrets Manager\nDB Master Credentials]
            CW[CloudWatch Container Logs]
        end
    end

    React -->|REST Calls /api/*| ALB
    ALB -->|Forward HTTP 8080| Container
    ALB -->|Periodic Health Probes| Health
    Container -->|JDBC TLS over 3306| RDS
    Container -.->|Fetch Credentials| SM
    Container -.->|stdout / stderr| CW
```

---

## 📋 REST API Endpoints Reference

### 1. Health & Diagnostics (ALB & Container Probes)
* `GET /health` or `GET /api/health` - Lightweight liveness/readiness probe returning `{"status": "UP"}`.

### 2. User & Authentication
* `POST /api/user/register` - Register patient, doctor, or staff accounts.
* `POST /api/user/login` - Authenticate users and return JWT Bearer token.
* `GET /api/user/roles` - Retrieve list of authorization roles.
* `GET /api/user/gender` - Retrieve supported demographic options.
* `GET /api/user/id?userId={id}` - Fetch user profile metadata.

### 3. Appointments & Scheduling
* `POST /api/appointment/patient/add` - Book a new clinical appointment.
* `GET /api/appointment/all` - List appointments (admin / clinical staff).
* `GET /api/appointment/patient/id?patientId={id}` - Retrieve patient appointment history.
* `GET /api/appointment/doctor/id?doctorId={id}` - Retrieve doctor's scheduled consultations.
* `POST /api/appointment/admin/assign/doctor` - Assign consulting physician to an appointment.
* `POST /api/appointment/doctor/update` - Doctor updates appointment notes and status.

### 4. Doctors & Medical Specialists
* `GET /api/doctor/all` - Directory of registered healthcare practitioners.
* `GET /api/doctor/specialist/all` - Filter practitioners by medical specialization.
* `POST /api/doctor/register` - Onboard practitioner profile.

### 5. Patient Records
* `GET /api/patient/all` - List all registered patients.
* `GET /api/patient/bloodgroup/all` - Directory of blood group classifications.

---

## 🛡️ Enterprise Security & CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/backend-ci-cd.yml`) automates comprehensive security validation and automated deployment:

1. **Secret Leak Detection**: Pre-build scanning via **Gitleaks** prevents credentials from reaching the repository.
2. **Dockerfile Linting**: Pre-build inspection via **Hadolint** ensures container best practices (least-privilege, clean caching).
3. **Dependency Vulnerability Scanning**: File system and Maven dependency scanning via **Aqua Security Trivy**.
4. **Keyless AWS OIDC Authentication**: Workflows authenticate to AWS STS using temporary OpenID Connect (OIDC) tokens—**zero static access keys in GitHub**.
5. **Secure Container Registry**: Image pushed to **Amazon ECR** with automated scan-on-push and SHA-pinned image tags.
6. **Rolling Zero-Downtime Deployment**: Updates the **AWS ECS Fargate** service with automated deployment circuit breakers and rollback protection.

---