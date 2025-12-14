# MedImage System - Medical Image Analysis Platform

## Project Overview

**MedImage** is a microservices-based medical image analysis platform designed for clinical workflows in radiology and pathology departments. The system handles medical image processing, AI-powered analysis, patient record management, and real-time notifications in a distributed, scalable architecture.

## System Architecture

The MedImage system consists of 6 core microservices and a shared common module:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          MedImage System                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Frontend/UI Layer                                                      │
│  (Browser-based - React/Vue)                                            │
│         │                                                               │
├────────┼──────────────────────────────────────────────────────────┤
│         ▼                                                          │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │              REST API Gateway / Load Balancer            │    │
│  └───┬──────────┬──────────┬──────────┬────────────────────┘    │
│      │          │          │          │                         │
│      ▼          ▼          ▼          ▼                         │
│  ┌────────┐ ┌──────────┐ ┌────────┐ ┌──────────┐             │
│  │Patient │ │ Radiology│ │Imaging │ │Analytical│             │
│  │Service │ │ Service  │ │Service │ │  Model   │             │
│  │(8085)  │ │ (8081)   │ │ (8082) │ │ (8083)   │             │
│  └────────┘ └──────────┘ └────────┘ └──────────┘             │
│      │          │          │          │                        │
│      └──────────┼──────────┼──────────┘                        │
│                 │          │                                   │
│          ┌──────▼──────────▼──────┐                            │
│          │   Notification Service │                            │
│          │       (8084)           │                            │
│          └───────────────────────┘                             │
│                 ▲                                               │
│                 │                                               │
│  ┌──────────────┼──────────────┬──────────────┬──────────────┐ │
│  ▼              ▼              ▼              ▼              ▼ │
│┌─────────┐ ┌─────────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐
││PostgreSQL  │  RabbitMQ   │  │  MinIO   │ │  Common  │ │ Config ││
││(Metadata)  │ (Messaging) │  │   (S3)   │ │ (Shared) │ │        ││
│└─────────┘ └─────────────┘ └──────────┘ └──────────┘ └────────┘
└─────────────────────────────────────────────────────────────────┘
```

## Modules Overview

### 1. **[Common Module](./common/README.md)** - Shared Library
- **Purpose**: Provides shared utilities, data models, and S3 integration
- **Key Components**: Shared DTOs, S3StorageService, Configuration
- **Port**: N/A (Library module)
- **Database**: None
- **Dependencies**: Lombok, Spring Boot, AWS SDK S3

### 2. **[Analytical Model Service](./analytical-model/README.md)** - AI Analysis Engine
- **Purpose**: Performs AI-powered medical image analysis and risk assessment
- **Key Components**: MockAiModelService, AnalysisListener, RabbitConfig
- **Port**: 8083
- **Database**: None (Stateless)
- **Key Technologies**: Spring Boot AMQP, RabbitMQ, MinIO/S3
- **Workflow**: Receives images via RabbitMQ → Downloads from S3 → Performs analysis → Publishes results

### 3. **[Imaging Service](./imaging-service/README.md)** - Image Repository
- **Purpose**: Manages medical image storage, retrieval, and metadata
- **Key Components**: ImagingController, ImageRepository, ImageMetadata Entity
- **Port**: 8082
- **Database**: PostgreSQL (`medimage_db`)
- **Key Technologies**: Spring Web, Spring Data JPA, PostgreSQL
- **REST API**: Upload/download images, list patient images

### 4. **[Patient Service](./patient-service/README.md)** - Patient Management & Orchestration
- **Purpose**: Manages patient records and coordinates examination workflows
- **Key Components**: PatientRecordController, PatientService, AnalysisResultListener
- **Port**: 8085
- **Database**: PostgreSQL (`medimage_db`)
- **Key Technologies**: Spring Web, Spring Data JPA, Spring AMQP, RabbitMQ
- **REST API**: Create examinations, manage patient records
- **Messaging**: Receives analysis results, sends examination requests

### 5. **[Radiology Service](./radiology-service/README.md)** - Radiology Workflow Manager
- **Purpose**: Manages radiologist workflow and examination tasks
- **Key Components**: RadiologyController, ExaminationTaskService, ExaminationRequestListener
- **Port**: 8081
- **Database**: PostgreSQL (`medimage_db`)
- **Key Technologies**: Spring Web, Spring Data JPA, Spring AMQP, RabbitMQ, S3
- **REST API**: Direct scan upload and processing
- **Messaging**: Receives examination requests, sends image analysis requests

### 6. **[Notification Service](./notification-service/README.md)** - Real-Time Notifications
- **Purpose**: Delivers real-time notifications via Server-Sent Events (SSE)
- **Key Components**: ResearchCompletedNotificationController, SSE Management
- **Port**: 8084
- **Database**: None (In-memory emitter management)
- **Key Technologies**: Spring Web, Spring AMQP, RabbitMQ, Server-Sent Events
- **REST API**: SSE stream subscription for real-time updates
- **Messaging**: Receives completion/analysis events

## Complete System Workflow

### Scenario: Patient Examination & Analysis

```
1. REGISTRATION
   └─→ Client calls: POST /api/patient/examine
       └─→ Patient Service: Creates Patient record & Examination task
           └─→ Publishes: PatientExaminationRequest to examination_requests_queue

2. RADIOLOGY WORKFLOW
   └─→ Radiology Service receives PatientExaminationRequest
       └─→ Creates ExaminationTask for radiologist
           └─→ Radiologist uploads scan: POST /api/radiology/scan
               └─→ Radiology Service: Uploads image to MinIO/S3
                   └─→ Creates ImageAnalysisRequest
                       └─→ Publishes to analysis_queue

3. IMAGE ANALYSIS
   └─→ Analytical Model Service receives ImageAnalysisRequest
       └─→ Downloads image from S3
           └─→ Runs AI inference (MockAiModelService)
               └─→ Generates RiskAssessmentResult
                   └─→ Publishes to risk_assessment_queue

4. RESULT PROCESSING
   └─→ Patient Service receives RiskAssessmentResult
       └─→ Updates Examination with result
           └─→ Publishes ResearchCompletedNotificationEvent
               └─→ Notification Service broadcasts to SSE clients
                   └─→ Real-time UI update

5. IMAGE RETRIEVAL (Optional)
   └─→ Client: GET /api/images/patient/{patientId}
       └─→ Imaging Service: Returns patient's images from PostgreSQL
```

## Key Technologies Stack

| Component | Technology | Purpose |
|---|---|---|
| **Microservices** | Spring Boot 2.x, Java 11+ | Framework for services |
| **REST APIs** | Spring Web MVC | HTTP endpoints |
| **Data Persistence** | PostgreSQL | Relational database |
| **ORM** | Spring Data JPA, Hibernate | Object-relational mapping |
| **Async Messaging** | RabbitMQ, Spring AMQP | Inter-service communication |
| **Object Storage** | MinIO / AWS S3 | Medical image storage |
| **Real-Time Notifications** | Server-Sent Events (SSE) | Push notifications to clients |
| **Code Generation** | Lombok | Boilerplate reduction |
| **Build Tool** | Maven | Project build and dependency management |
| **Containerization** | Docker | Service containerization |
| **Orchestration** | Kubernetes | Production deployment |

## Service Communication Map

### Synchronous (REST/HTTP)
```
Imaging Service
  ├─ POST /api/images - Upload image
  ├─ GET /api/images/{id}/content - Get image data
  └─ GET /api/images/patient/{patientId} - List patient images

Patient Service
  ├─ POST /api/patient/examine - Create examination
  └─ (Internal HTTP call to Imaging Service for image retrieval)

Radiology Service
  └─ POST /api/radiology/scan - Upload scan

Notification Service
  └─ GET /api/notifications/subscribe - SSE stream
```

### Asynchronous (RabbitMQ/Message Queues)
```
Queue: examination_requests_queue
  Producer: Patient Service
  Consumer: Radiology Service

Queue: analysis_queue
  Producer: Radiology Service
  Consumer: Analytical Model Service

Queue: risk_assessment_queue
  Producer: Analytical Model Service
  Consumer: Patient Service

Queue: notification_queue
  Producer: Patient Service
  Consumer: Notification Service
```

## Database Schema

### PostgreSQL (`medimage_db`)

#### Patient Table
```sql
CREATE TABLE patient (
  id UUID PRIMARY KEY,
  full_name VARCHAR(255),
  email VARCHAR(255),
  date_of_birth DATE,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

#### Examination Table
```sql
CREATE TABLE examination (
  id UUID PRIMARY KEY,
  patient_id UUID REFERENCES patient(id),
  modality VARCHAR(50),
  notes TEXT,
  is_urgent BOOLEAN,
  status VARCHAR(50),
  result JSONB,
  created_at TIMESTAMP,
  completed_at TIMESTAMP
);
```

#### ExaminationTask Table
```sql
CREATE TABLE examination_task (
  id UUID PRIMARY KEY,
  patient_id UUID,
  modality VARCHAR(50),
  notes TEXT,
  is_urgent BOOLEAN,
  s3_image_key VARCHAR(255),
  status VARCHAR(50),
  created_at TIMESTAMP,
  completed_at TIMESTAMP
);
```

#### ImageMetadata Table
```sql
CREATE TABLE image_metadata (
  id UUID PRIMARY KEY,
  patient_id UUID,
  modality VARCHAR(50),
  format VARCHAR(20),
  data BYTEA,
  created_at TIMESTAMP
);
```

## Configuration Management

### Environment Variables

| Variable | Default | Services |
|---|---|---|
| **RABBIT_HOST** | localhost | All services using AMQP |
| **RABBIT_PORT** | 5672 | All services using AMQP |
| **RABBIT_USER** | user | All services using AMQP |
| **RABBIT_PASS** | password | All services using AMQP |
| **DB_HOST** | localhost | Patient, Imaging, Radiology |
| **DB_USER** | postgres | Patient, Imaging, Radiology |
| **DB_PASS** | password | Patient, Imaging, Radiology |
| **S3_ENDPOINT** | http://localhost:9000 | Analytical, Radiology, Notification |
| **S3_ACCESS_KEY** | minioadmin | S3-dependent services |
| **S3_SECRET_KEY** | minioadmin | S3-dependent services |

## Running the System

### Local Development with Docker Compose

```bash
# Start all services
docker-compose up -d

# Services will be available at:
# - Patient Service: http://localhost:8085
# - Radiology Service: http://localhost:8081
# - Imaging Service: http://localhost:8082
# - Analytical Model: http://localhost:8083
# - Notification Service: http://localhost:8084

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Kubernetes Deployment

```bash
# Apply Kubernetes configurations
kubectl apply -f k8s/01-config.yaml
kubectl apply -f k8s/02-infrastructure.yaml
kubectl apply -f k8s/03-minio-job.yaml
kubectl apply -f k8s/04-services.yaml
kubectl apply -f k8s/05-ingress.yaml

# Check deployment status
kubectl get pods
kubectl get services
```

## Development Guide

### Building Individual Services

```bash
# Build all modules
mvn clean package

# Build specific module
mvn clean package -f {module}/pom.xml

# Skip tests for faster build
mvn clean package -DskipTests
```

### Adding New Functionality

1. Add new DTOs to `common/` module
2. Implement service logic in respective service module
3. Create REST endpoints in controller
4. Add RabbitMQ listeners for async processing
5. Update configuration in `application.yaml`
6. Update this documentation

## Monitoring & Observability

### Key Metrics to Monitor

- **Service Availability**: Check /health endpoints
- **Database Connections**: PostgreSQL connection pool
- **Message Queue Depth**: RabbitMQ queue lengths
- **API Response Time**: Monitor endpoint latency
- **Error Rates**: Service error logs
- **Storage Usage**: S3 bucket size

### Logging

Services log to:
- Console (Docker/Kubernetes stdout)
- Application logs directory (local development)

Enable detailed logging:
```yaml
logging:
  level:
    root: INFO
    org.trilgar: DEBUG
    org.springframework: DEBUG
```

## Security Considerations

1. **Credentials**: Use environment variables, never hardcode secrets
2. **Database**: Use strong PostgreSQL passwords in production
3. **RabbitMQ**: Secure with credentials and SSL
4. **S3**: Use IAM roles instead of keys in production
5. **HTTPS**: Use SSL/TLS in production
6. **Authentication**: Implement OAuth2/JWT for API security
7. **Data Privacy**: Encrypt sensitive patient data
8. **Access Control**: Implement role-based access control (RBAC)

## Performance Optimization

1. **Database Indexing**: Ensure proper indexes on frequently queried columns
2. **Connection Pooling**: Configure optimal connection pools
3. **Caching**: Implement Redis for frequently accessed data
4. **Message Batching**: Batch multiple messages in RabbitMQ
5. **Load Balancing**: Use load balancer for distributing requests
6. **Horizontal Scaling**: Scale services independently based on load

## Troubleshooting

### Common Issues

| Issue | Solution |
|---|---|
| Services won't start | Check PostgreSQL, RabbitMQ, MinIO are running |
| Image upload fails | Verify S3 bucket exists and is accessible |
| Analysis not running | Check RabbitMQ queues exist and listeners are active |
| Notifications not received | Verify notification_queue configuration |
| Database connection error | Check DB_HOST and credentials |

### Useful Commands

```bash
# Check service health
curl http://localhost:8081/health
curl http://localhost:8082/health
curl http://localhost:8083/health
curl http://localhost:8084/health
curl http://localhost:8085/health

# View Docker logs
docker logs {container_name}

# Access PostgreSQL
psql -U postgres -h localhost -d medimage_db

# View RabbitMQ Management UI
# http://localhost:15672 (default: guest/guest)

# View MinIO Management UI
# http://localhost:9001 (default: minioadmin/minioadmin)
```

## Module Documentation

- **[Common Module](./common/README.md)** - Shared library
- **[Analytical Model Service](./analytical-model/README.md)** - AI inference engine
- **[Imaging Service](./imaging-service/README.md)** - Image repository
- **[Patient Service](./patient-service/README.md)** - Patient management
- **[Radiology Service](./radiology-service/README.md)** - Radiology workflows
- **[Notification Service](./notification-service/README.md)** - Real-time notifications

## Contributing Guidelines

1. Create feature branch from `develop`
2. Follow Java naming conventions
3. Add tests for new features
4. Update relevant documentation
5. Submit pull request with description
6. Ensure all tests pass before merge

## Future Roadmap

- [ ] Implement real ML models (replace MockAiModelService)
- [ ] Add authentication/authorization (OAuth2)
- [ ] Implement audit logging
- [ ] Add performance analytics
- [ ] Support DICOM format
- [ ] Implement data encryption at rest
- [ ] Add backup/disaster recovery
- [ ] Multi-tenant support
- [ ] Advanced reporting

## License

[Specify License Here]

## Contact & Support

For issues, questions, or contributions:
- Create GitHub Issues
- Email: support@medimage.local
- Documentation: See individual module READMEs

---

**Last Updated**: December 14, 2025  
**Version**: 1.0-SNAPSHOT  
**Status**: Development

