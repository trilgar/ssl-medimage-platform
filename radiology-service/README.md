# Radiology Service

## Overview

The **Radiology Service** is a core microservice in the MedImage system responsible for managing radiologist workflows and medical examinations. It receives examination requests, creates examination tasks for radiologists, and manages the workflow from request to image acquisition and analysis initiation.

## Purpose

This service acts as a **radiology workflow manager** that:
- Receives patient examination requests from the patient service
- Creates examination tasks for radiologists to complete
- Manages radiology examination states and statuses
- Orchestrates image uploads and analysis initiation
- Coordinates between clinical requests and imaging operations
- Maintains persistent examination task records

## Key Features

### 1. **Examination Task Management**
- Create examination tasks from patient requests
- Track examination task status
- Store examination details and metadata
- UUID-based examination identification
- Persistent database storage

### 2. **Message-Driven Processing**
- Listens for examination requests via RabbitMQ
- Asynchronous request processing
- Pub/Sub communication pattern
- Jackson2Json message serialization

### 3. **REST API for Radiologists**
- Direct image upload endpoint
- Scan processing and acceptance
- Error handling and reporting
- Multipart file support

### 4. **S3 Image Storage Integration**
- Upload images to MinIO/S3
- Link examinations to stored images
- Support for multiple image formats
- Binary data streaming

### 5. **Database Persistence**
- PostgreSQL-backed examination records
- Examination status tracking
- Audit trail of examination lifecycle
- Transaction support

## Service Dependencies

### **Internal Dependencies**

| Service/Module | Purpose |
|---|---|
| **Common Module** | Provides shared DTOs (PatientExaminationRequest) and S3 service |

### **External Service Dependencies**

| Service | Interaction | Type |
|---|---|---|
| **Patient Service** | Receives examination requests via RabbitMQ | Async Messaging |
| **Analytical Model** | Sends images for analysis via RabbitMQ | Async Messaging |

### **External Technologies**

| Technology | Purpose |
|---|---|
| **Spring Boot Web** | REST API framework |
| **Spring Data JPA** | ORM and database access |
| **Spring AMQP** | RabbitMQ messaging |
| **PostgreSQL** | Examination data storage |
| **S3/MinIO** | Medical image storage |
| **AWS SDK S3** | S3-compatible storage client |
| **Lombok** | Code generation for entities and services |

### **Services That Depend on This Module**

| Service | Interaction |
|---|---|
| **Patient Service** | Receives confirmation/status updates |
| **Analytical Model** | Receives images for analysis |

## REST API Endpoints

### **Upload and Process Scan**
```
POST /api/radiology/scan
Content-Type: multipart/form-data

Parameters:
  - patientId (UUID): Patient identifier
  - modality (String): Imaging type (CT, XRay, MRI, etc.)
  - file (MultipartFile): Binary image data

Response:
  "Scan uploaded successfully. Analysis started."
```

## Configuration

### Application Properties (`application.yaml`)

| Property | Description | Default Value | Environment Variable |
|---|---|---|---|
| **server.port** | HTTP server port for the service | `8081` | - |
| **spring.application.name** | Application name identifier | `radiology-service` | - |
| **spring.rabbitmq.host** | RabbitMQ broker hostname | `localhost` | `RABBIT_HOST` |
| **spring.rabbitmq.port** | RabbitMQ broker port | `5672` | `RABBIT_PORT` |
| **spring.rabbitmq.username** | RabbitMQ authentication username | `user` | `RABBIT_USER` |
| **spring.rabbitmq.password** | RabbitMQ authentication password | `password` | `RABBIT_PASS` |
| **spring.datasource.url** | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/medimage_db` | `DB_HOST` |
| **spring.datasource.username** | Database username | `postgres` | `DB_USER` |
| **spring.datasource.password** | Database password | `password` | `DB_PASS` |
| **spring.jpa.hibernate.ddl-auto** | Hibernate DDL strategy | `update` | - |
| **spring.jpa.show-sql** | Enable SQL logging | `true` | - |
| **s3.endpoint** | S3/MinIO endpoint URL | `http://localhost:9000` | `S3_ENDPOINT` |
| **s3.access-key** | S3 access key | `minioadmin` | `S3_ACCESS_KEY` |
| **s3.secret-key** | S3 secret key | `minioadmin` | `S3_SECRET_KEY` |
| **s3.bucket** | S3 bucket for medical images | `med-staging` | - |
| **s3.region** | AWS region for S3 operations | `us-east-1` | - |

## Project Structure

```
radiology-service/
├── src/
│   ├── main/
│   │   ├── java/org/trilgar/medimage/ssl/radiology/
│   │   │   ├── RadiologyApplication.java           # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   ├── RadiologyController.java        # REST API for scan upload
│   │   │   │   └── ExaminationTaskController.java  # Radiologist examination tasks
│   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   │   ├── RadiologyService.java       # Radiology service interface
│   │   │   │   │   └── ExaminationTaskService.java # Task service interface
│   │   │   │   ├── RadiologyServiceImpl.java        # Radiology implementation
│   │   │   │   └── ExaminationTaskServiceImpl.java  # Task service implementation
│   │   │   ├── entity/
│   │   │   │   └── ExaminationTask.java            # JPA entity for exam tasks
│   │   │   ├── repository/
│   │   │   │   └── ExaminationTaskRepository.java  # Spring Data JPA repository
│   │   │   ├── config/
│   │   │   │   └── RabbitConfig.java               # RabbitMQ configuration
│   │   │   ├── listener/
│   │   │   │   └── ExaminationRequestListener.java # RabbitMQ message listener
│   │   │   └── resources/
│   │   │       └── application.yaml                # Application configuration
│   └── test/
│       └── java/
├── pom.xml
├── Dockerfile
└── README.md (this file)
```

## Key Classes

### **RadiologyApplication**
- Spring Boot entry point
- Imports CommonS3Config for S3 integration
- Initializes the microservice

### **RadiologyController**
REST endpoint handler for radiologist operations.

**Endpoints:**
- `POST /api/radiology/scan` - Direct scan upload and processing

**Features:**
- Multipart file handling
- Patient and modality association
- Error handling and reporting
- Binary stream processing

### **ExaminationTaskController**
REST endpoint handler for examination task management.

**Responsibilities:**
- List pending examination tasks
- Retrieve examination task details
- Mark tasks as completed

### **RadiologyService (Interface)**
Contract for radiology operations.

**Key Methods:**
- `processScan(patientId, imageData, modality): void`
- `getScanHistory(patientId): List<ExaminationTask>`

### **RadiologyServiceImpl**
Implementation of radiology service.

**Responsibilities:**
- Scan file processing
- Image upload to S3
- Examination task coordination
- Error handling

### **ExaminationTaskService (Interface)**
Contract for examination task management.

**Key Methods:**
- `createTask(request): ExaminationTask`
- `completeTask(taskId): void`
- `getPendingTasks(): List<ExaminationTask>`

### **ExaminationTaskServiceImpl**
Implementation of examination task service.

**Responsibilities:**
- Task creation from examination requests
- Task status management
- Radiologist assignment (future)
- Task persistence

### **ExaminationRequestListener**
RabbitMQ message listener for examination requests.

**Functionality:**
- Listens on `examination_requests_queue`
- Processes PatientExaminationRequest messages
- Creates examination tasks
- Handles errors and failures

**Workflow:**
```
ExaminationRequestListener
  ↓
handleDoctorRequest(PatientExaminationRequest)
  ↓
ExaminationTaskService.createTask()
  ↓
Save to PostgreSQL
  ↓
Log: "Task created. Radiologist can now see it."
```

### **ExaminationTask (Entity)**
JPA entity representing examination tasks.

**Fields:**
- `id`: UUID - Primary key
- `patientId`: UUID - Patient reference
- `modality`: String - Examination type (CT, XRay, MRI)
- `notes`: String - Clinical notes
- `isUrgent`: Boolean - Urgency flag
- `s3ImageKey`: String - Reference to stored image
- `status`: String - Task status (PENDING, IN_PROGRESS, COMPLETED)
- `createdAt`: LocalDateTime - Task creation
- `completedAt`: LocalDateTime - Completion timestamp

### **ExaminationTaskRepository**
Spring Data JPA repository for database operations.

**Query Methods:**
- `findById(id): Optional<ExaminationTask>`
- `findByPatientId(patientId): List<ExaminationTask>`
- `findByStatus(status): List<ExaminationTask>`
- Standard CRUD operations

## Data Models

### **ExaminationTask Entity**
```
ExaminationTask:
├── id (UUID) - Primary Key
├── patientId (UUID) - Foreign Key
├── modality (VARCHAR)
├── notes (TEXT)
├── isUrgent (BOOLEAN)
├── s3ImageKey (VARCHAR)
├── status (VARCHAR)
├── createdAt (TIMESTAMP)
└── completedAt (TIMESTAMP)
```

## Message Queue Configuration

### **RabbitMQ Queues**

| Queue | Purpose | Producer | Consumer |
|---|---|---|---|
| `examination_requests_queue` | Examination requests | Patient Service | Radiology Service |
| `analysis_queue` | Image analysis requests | Radiology Service | Analytical Model |

## Workflow Examples

### 1. Examination Request Processing Workflow
```
Patient Service
  ↓
Send PatientExaminationRequest to examination_requests_queue
  ↓
ExaminationRequestListener.handleDoctorRequest()
  ↓
ExaminationTaskService.createTask()
  ↓
Save ExaminationTask to PostgreSQL
  ↓
Log: "Task created for radiologist"
  ↓
Radiologist can see pending task in dashboard
```

### 2. Scan Upload and Analysis Workflow
```
Radiologist (via API)
  ↓
POST /api/radiology/scan (file, patientId, modality)
  ↓
RadiologyController.performScan()
  ↓
RadiologyServiceImpl.processScan()
  ↓
Upload image to S3 via S3StorageService
  ↓
Create ImageAnalysisRequest
  ↓
Send to analysis_queue (RabbitMQ)
  ↓
Analytical Model receives request
  ↓
Response: "Scan uploaded successfully. Analysis started."
```

## Running the Service

### Prerequisites
- Java 11+
- Maven 3.6+
- PostgreSQL running
- RabbitMQ running
- MinIO running
- Common module built

### Build
```bash
mvn clean package -DskipTests
```

### Run Locally
```bash
java -jar target/radiology-service-1.0-SNAPSHOT.jar
```

### Docker Build & Run
```bash
docker build -t radiology-service:1.0 .
docker run -d \
  -e DB_HOST=postgres \
  -e DB_USER=postgres \
  -e DB_PASS=password \
  -e RABBIT_HOST=rabbitmq \
  -e RABBIT_PORT=5672 \
  -e S3_ENDPOINT=http://minio:9000 \
  -p 8081:8081 \
  --network medimage-network \
  radiology-service:1.0
```

## Error Handling

| Scenario | Response |
|---|---|
| File processing error | 500 "File processing error: {message}" |
| Invalid request data | 400 "Error: {message}" |
| Database error | 500 Internal Server Error |
| S3 upload failure | Service logs error and retries |

## Performance Considerations

1. **Database**: Indexed queries on patient ID and status
2. **S3 Upload**: Streamed binary upload for large files
3. **Async Processing**: RabbitMQ ensures non-blocking request handling
4. **Transactions**: Proper transaction management for consistency

## Future Enhancements

1. **Radiologist Assignment**: Auto-assign tasks based on workload
2. **Priority Queuing**: Prioritize urgent examinations
3. **Task Expiration**: Auto-expire old pending tasks
4. **Real-time Notifications**: Push notifications to radiologists
5. **Batch Processing**: Bulk scan upload support
6. **Report Generation**: Generate examination reports
7. **Performance Metrics**: Track radiologist productivity
8. **Quality Assurance**: QA workflow for examination review

## Integration Points

### With Patient Service
- Receives: `PatientExaminationRequest` via RabbitMQ
- Sends: Task creation confirmation

### With Analytical Model Service
- Sends: `ImageAnalysisRequest` via RabbitMQ
- Receives: Analysis results

### With Common Module
- Uses: `S3StorageService` for image upload
- Uses: Shared DTOs for communication

## Troubleshooting

| Issue | Solution |
|---|---|
| Connection refused (DB) | Check PostgreSQL is running |
| Connection refused (RabbitMQ) | Check RabbitMQ is running |
| S3 connection error | Check MinIO endpoint and credentials |
| File upload fails | Check disk space and S3 permissions |
| Task not created | Verify RabbitMQ queue configuration |
| Analysis not starting | Check analysis_queue and Analytical Model |

## Related Documentation

- See `common/` module for shared services
- See `patient-service/` for examination requests
- See `analytical-model/` for analysis processing
- See `docker-compose.yaml` for infrastructure setup
- See `k8s/` directory for Kubernetes deployment

