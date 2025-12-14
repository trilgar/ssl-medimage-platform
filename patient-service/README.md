# Patient Service

## Overview

The **Patient Service** is a core microservice in the MedImage system responsible for managing patient records, medical examinations, and coordinating analysis workflows. It serves as a central hub for patient data management and orchestrates interactions between imaging, radiology, and analytical services.

## Purpose

This service acts as a **patient management and workflow orchestrator** that:
- Maintains patient demographic records
- Manages patient examinations and history
- Orchestrates examination requests to radiology service
- Receives and processes analysis results
- Coordinates with imaging and notification services
- Handles patient record creation and updates

## Key Features

### 1. **Patient Record Management**
- Create and update patient records
- Store patient demographics (name, email, DOB)
- Maintain patient history
- UUID-based patient identification

### 2. **Examination Management**
- Initiate new medical examinations
- Track examination status and history
- Associate examinations with modalities
- Link examinations to patients
- Support urgent examination flagging

### 3. **Async Result Processing**
- Listen for analysis results from analytical model
- Process risk assessment outcomes
- Update examination records with results
- Trigger notification workflows

### 4. **Service Orchestration**
- Communicate with imaging service
- Dispatch requests to radiology service
- Receive results from analytical model
- Coordinate multi-service workflows

### 5. **Database Persistence**
- PostgreSQL-backed patient records
- Examination history tracking
- Relationship management
- Transaction support

## Service Dependencies

### **Internal Dependencies**

| Service/Module | Purpose |
|---|---|
| **Common Module** | Provides shared DTOs and models (RiskAssessmentResult, PatientExaminationRequest) |

### **External Service Dependencies**

| Service | Interaction | Type |
|---|---|---|
| **Imaging Service** | Retrieves patient images via REST API | HTTP Client |
| **Radiology Service** | Sends examination requests via RabbitMQ | Async Messaging |
| **Analytical Model** | Receives analysis results via RabbitMQ | Async Messaging |

### **External Technologies**

| Technology | Purpose |
|---|---|
| **Spring Boot Web** | REST API framework |
| **Spring Data JPA** | ORM and database access |
| **Spring AMQP** | RabbitMQ messaging |
| **PostgreSQL** | Patient and examination data storage |
| **Lombok** | Code generation for entities and services |

### **Services That Depend on This Module**

| Service | Interaction |
|---|---|
| **Radiology Service** | Receives examination initiation |
| **Notification Service** | May receive examination events |

## REST API Endpoints

### **Create Examination**
```
POST /api/patient/examine
Content-Type: application/json

Request Body:
{
  "patientId": "UUID (optional)",
  "fullName": "String",
  "email": "String",
  "dob": "LocalDate",
  "modality": "String",
  "notes": "String",
  "isUrgent": "boolean"
}

Response:
  "Examination created for patient: {patientId}. ID: {examinationId}"
```

## Configuration

### Application Properties (`application.yaml`)

| Property | Description | Default Value | Environment Variable |
|---|---|---|---|
| **server.port** | HTTP server port for the service | `8085` | - |
| **spring.application.name** | Application name identifier | `patient-service` | - |
| **spring.datasource.url** | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/medimage_db` | `DB_HOST` |
| **spring.datasource.username** | Database username | `postgres` | `DB_USER` |
| **spring.datasource.password** | Database password | `password` | `DB_PASS` |
| **spring.jpa.hibernate.ddl-auto** | Hibernate DDL strategy | `update` | - |
| **spring.jpa.show-sql** | Enable SQL logging | `true` | - |
| **spring.rabbitmq.host** | RabbitMQ broker hostname | `localhost` | `RABBIT_HOST` |
| **spring.rabbitmq.port** | RabbitMQ broker port | `5672` | `RABBIT_PORT` |
| **spring.rabbitmq.username** | RabbitMQ authentication username | `user` | `RABBIT_USER` |
| **spring.rabbitmq.password** | RabbitMQ authentication password | `password` | `RABBIT_PASS` |

## Project Structure

```
patient-service/
├── src/
│   ├── main/
│   │   ├── java/org/trilgar/medimage/ssl/patient/
│   │   │   ├── PatientServiceApplication.java      # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── PatientRecordController.java    # REST API endpoints
│   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   │   ├── PatientService.java         # Service interface
│   │   │   │   │   └── NotificationSender.java     # Notification interface
│   │   │   │   ├── PatientServiceImpl.java          # Main service implementation
│   │   │   │   └── RiskAssessmentNotificationSender.java # Notification sender
│   │   │   ├── entity/
│   │   │   │   ├── Patient.java                    # Patient JPA entity
│   │   │   │   └── Examination.java                # Examination JPA entity
│   │   │   ├── repository/
│   │   │   │   ├── PatientRepository.java          # Patient repository
│   │   │   │   └── ExaminationRepository.java      # Examination repository
│   │   │   ├── client/
│   │   │   │   └── ImagingServiceClient.java       # HTTP client for imaging service
│   │   │   ├── config/
│   │   │   │   └── RabbitConfig.java               # RabbitMQ configuration
│   │   │   ├── listener/
│   │   │   │   └── AnalysisResultListener.java     # RabbitMQ message listener
│   │   │   └── model/
│   │   │       └── CreateExaminationRequest.java   # Request DTO
│   │   └── resources/
│   │       └── application.yaml                    # Application configuration
│   └── test/
│       └── java/
├── pom.xml
├── Dockerfile
└── README.md (this file)
```

## Key Classes

### **PatientServiceApplication**
- Spring Boot entry point
- Initializes the microservice
- Configures Spring context

### **PatientRecordController**
REST endpoint handler for patient operations.

**Endpoints:**
- `POST /api/patient/examine` - Create and initiate examination

**Features:**
- Patient record creation
- Examination initialization
- Error handling

### **PatientService (Interface)**
Contract for patient management operations.

**Key Methods:**
- `initiateExamination(patient, modality, notes, isUrgent): Examination`
- `processAnalysisResult(result): void`
- `getPatientById(id): Patient`
- `getAllPatients(): List<Patient>`

### **PatientServiceImpl**
Main implementation of patient management service.

**Responsibilities:**
- Patient CRUD operations
- Examination creation and management
- Result processing and notification
- Inter-service communication
- Business logic orchestration

### **AnalysisResultListener**
RabbitMQ message listener for analysis results.

**Functionality:**
- Listens on `risk_assessment_queue`
- Processes RiskAssessmentResult messages
- Triggers result processing workflow
- Handles errors and failures

### **RiskAssessmentNotificationSender**
Sends notification events for analysis results.

**Features:**
- Publishes notification events
- Routes high-risk cases
- Coordinates with notification service

### **ImagingServiceClient**
HTTP client for calling imaging service REST API.

**Methods:**
- `getPatientImages(patientId): List<ImageMetadata>`
- `uploadImage(patientId, imageData, modality): ImageMetadata`

### **Patient (Entity)**
JPA entity representing patient records.

**Fields:**
- `id`: UUID - Primary key
- `fullName`: String - Patient name
- `email`: String - Contact email
- `dateOfBirth`: LocalDate - DOB
- `createdAt`: LocalDateTime - Record creation
- `updatedAt`: LocalDateTime - Last update

### **Examination (Entity)**
JPA entity representing examination records.

**Fields:**
- `id`: UUID - Primary key
- `patientId`: UUID - Foreign key to Patient
- `modality`: String - Examination type
- `notes`: String - Clinical notes
- `isUrgent`: Boolean - Urgency flag
- `status`: String - Examination status
- `result`: RiskAssessmentResult - Analysis result
- `createdAt`: LocalDateTime - Creation timestamp
- `completedAt`: LocalDateTime - Completion timestamp

## Data Models

### **Patient Entity**
```
Patient:
├── id (UUID) - Primary Key
├── fullName (VARCHAR)
├── email (VARCHAR)
├── dateOfBirth (DATE)
├── createdAt (TIMESTAMP)
└── updatedAt (TIMESTAMP)
```

### **Examination Entity**
```
Examination:
├── id (UUID) - Primary Key
├── patientId (UUID) - Foreign Key
├── modality (VARCHAR)
├── notes (TEXT)
├── isUrgent (BOOLEAN)
├── status (VARCHAR)
├── result (JSON/BLOB)
├── createdAt (TIMESTAMP)
└── completedAt (TIMESTAMP)
```

### **CreateExaminationRequest DTO**
```java
{
  "patientId": "UUID (optional)",
  "fullName": "String",
  "email": "String",
  "dob": "LocalDate",
  "modality": "String",
  "notes": "String",
  "isUrgent": "boolean"
}
```

## Message Queue Configuration

### **RabbitMQ Queues**

| Queue | Purpose | Producer | Consumer |
|---|---|---|---|
| `risk_assessment_queue` | Analysis results | Analytical Model | Patient Service |
| `examination_requests_queue` | Examination requests | Patient Service | Radiology Service |
| `notification_queue` | Notification events | Patient Service | Notification Service |

## Workflow Examples

### 1. Examination Creation Workflow
```
Client
  ↓
POST /api/patient/examine
  ↓
PatientRecordController.createExamination()
  ↓
PatientServiceImpl.initiateExamination()
  ↓
Save Patient & Examination to PostgreSQL
  ↓
Send PatientExaminationRequest to RabbitMQ
  ↓
Response: "Examination created..."
  ↓
Radiology Service receives request
```

### 2. Analysis Result Processing Workflow
```
Analytical Model Service
  ↓
Publish RiskAssessmentResult to risk_assessment_queue
  ↓
AnalysisResultListener.onRiskAssessment()
  ↓
PatientServiceImpl.processAnalysisResult()
  ↓
Update Examination record with result
  ↓
Publish notification event (if critical)
  ↓
Notification Service receives event
```

## Running the Service

### Prerequisites
- Java 11+
- Maven 3.6+
- PostgreSQL running
- RabbitMQ running
- Common module built

### Build
```bash
mvn clean package -DskipTests
```

### Run Locally
```bash
java -jar target/patient-service-1.0-SNAPSHOT.jar
```

### Docker Build & Run
```bash
docker build -t patient-service:1.0 .
docker run -d \
  -e DB_HOST=postgres \
  -e DB_USER=postgres \
  -e DB_PASS=password \
  -e RABBIT_HOST=rabbitmq \
  -e RABBIT_PORT=5672 \
  -p 8085:8085 \
  --network medimage-network \
  patient-service:1.0
```

## Error Handling

| Scenario | Response |
|---|---|
| Invalid examination request | 400 Bad Request |
| Database error | 500 Internal Server Error |
| Patient not found | 404 Not Found |
| RabbitMQ unavailable | Service degradation |

## Performance Considerations

1. **Database**: Indexed queries on patient ID and examination status
2. **Caching**: Patient records can be cached for frequently accessed data
3. **Async Processing**: RabbitMQ ensures non-blocking result processing
4. **Transactions**: Proper transaction management for data consistency

## Future Enhancements

1. **Patient Search**: Advanced search by name, email, etc.
2. **Examination Filtering**: Filter by status, date range, modality
3. **Result Aggregation**: View historical examination results
4. **Export**: Export patient records in standard formats
5. **Access Control**: Role-based access to patient data
6. **Audit Logging**: Complete audit trail of patient data access
7. **Batch Operations**: Bulk examination creation
8. **Analysis Reports**: Generate analysis summary reports

## Integration Points

### With Radiology Service
- Sends: `PatientExaminationRequest` via RabbitMQ
- Receives: Examination confirmation/updates

### With Analytical Model Service
- Receives: `RiskAssessmentResult` via RabbitMQ
- Updates: Examination records with results

### With Imaging Service
- REST calls to: `GET /api/images/patient/{patientId}`
- Coordinates: Image upload and retrieval

### With Notification Service
- Sends: Notification events via RabbitMQ
- High-risk alerts

## Troubleshooting

| Issue | Solution |
|---|---|
| Connection refused (DB) | Check PostgreSQL is running |
| Connection refused (RabbitMQ) | Check RabbitMQ is running and accessible |
| Examination not created | Verify request format and required fields |
| Results not processed | Check RabbitMQ queues and consumer logs |
| Imaging service unavailable | Configure retry mechanism |

## Related Documentation

- See `common/` module for shared models
- See `imaging-service/` for image operations
- See `radiology-service/` for examination workflows
- See `analytical-model/` for analysis processing
- See `docker-compose.yaml` for infrastructure setup
- See `k8s/` directory for Kubernetes deployment

