# Imaging Service

## Overview

The **Imaging Service** is a core microservice in the MedImage system responsible for managing medical image storage, retrieval, and metadata tracking. It provides REST APIs for image upload and retrieval operations while maintaining a persistent database of image records.

## Purpose

This service acts as an **image repository and metadata manager** that:
- Accepts medical image uploads with associated metadata
- Stores image data and metadata persistently
- Provides retrieval endpoints for stored images
- Tracks imaging modalities (CT, X-Ray, etc.)
- Manages patient image collections
- Maintains audit trails of imaging activities

## Key Features

### 1. **REST API for Image Operations**
- File upload with patient and modality information
- Image content retrieval by ID
- Patient image collection listing
- Multipart form data support

### 2. **Persistent Image Storage**
- PostgreSQL database for metadata storage
- Binary image data storage
- UUID-based image identification
- Patient-image relationships

### 3. **Image Metadata Management**
- Image ID tracking
- Patient ID association
- Modality classification (CT, X-Ray, MRI, etc.)
- Image format tracking (PNG, JPEG, DICOM)
- Timestamp recording

### 4. **Patient-centric Organization**
- Images grouped by patient
- Query patient's complete imaging history
- Multi-image examination support

## Service Dependencies

### **Internal Dependencies**

| Service/Module | Purpose |
|---|---|
| **Common Module** | Provides shared DTOs and S3 service interface |

### **External Dependencies**

| Technology | Purpose |
|---|---|
| **Spring Boot Web** | REST API and HTTP server |
| **Spring Data JPA** | ORM and database abstraction |
| **PostgreSQL** | Relational database for metadata |
| **Lombok** | Code generation for entities and services |

### **Services That Depend on This Module**

| Service | Interaction |
|---|---|
| **Patient Service** | Calls imaging service to retrieve images |
| **Radiology Service** | Potential integration for imaging workflows |

## REST API Endpoints

### **Upload Image**
```
POST /api/images
Content-Type: multipart/form-data

Parameters:
  - patientId (UUID): Patient identifier
  - file (MultipartFile): Binary image data
  - modality (String): Imaging type (CT, XRay, MRI, etc.)

Response:
  {
    "id": "UUID",
    "patientId": "UUID",
    "modality": "String",
    "format": "String",
    "data": "byte[]",
    "createdAt": "LocalDateTime"
  }
```

### **Get Image Content**
```
GET /api/images/{id}/content
Accept: image/png

Response:
  Binary image data (byte array)
```

### **List Patient Images**
```
GET /api/images/patient/{patientId}

Response:
  [
    {
      "id": "UUID",
      "patientId": "UUID",
      "modality": "String",
      "format": "String",
      "createdAt": "LocalDateTime"
    },
    ...
  ]
```

## Configuration

### Application Properties (`application.yaml`)

| Property | Description | Default Value | Environment Variable |
|---|---|---|---|
| **server.port** | HTTP server port for the service | `8082` | - |
| **spring.application.name** | Application name identifier | `imaging-service` | - |
| **spring.datasource.url** | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/medimage_db` | `DB_HOST` |
| **spring.datasource.username** | Database username | `postgres` | `DB_USER` |
| **spring.datasource.password** | Database password | `password` | `DB_PASS` |
| **spring.jpa.hibernate.ddl-auto** | Hibernate DDL strategy | `update` | - |
| **spring.jpa.show-sql** | Enable SQL logging | `true` | - |
| **spring.jpa.hibernate.dialect** | SQL dialect | `PostgreSQLDialect` | - |

## Project Structure

```
imaging-service/
├── src/
│   ├── main/
│   │   ├── java/org/trilgar/medimage/ssl/imaging/
│   │   │   ├── ImagingServiceApplication.java       # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── ImagingController.java           # REST API endpoints
│   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   │   └── ImagingService.java          # Service interface
│   │   │   │   └── ImagingServiceImpl.java           # Service implementation
│   │   │   ├── entity/
│   │   │   │   └── ImageMetadata.java               # JPA entity for image records
│   │   │   └── repository/
│   │   │       └── ImageRepository.java             # Spring Data JPA repository
│   │   └── resources/
│   │       └── application.yaml                     # Application configuration
│   └── test/
│       └── java/
├── pom.xml
├── Dockerfile
└── README.md (this file)
```

## Key Classes

### **ImagingServiceApplication**
- Spring Boot entry point
- Initializes the microservice
- Configures Spring context

### **ImagingController**
REST endpoint handler for image operations.

**Endpoints:**
- `POST /api/images` - Upload new image
- `GET /api/images/{id}/content` - Retrieve image data
- `GET /api/images/patient/{patientId}` - List patient images

**Features:**
- File upload handling
- Content-type management
- Transactional operations
- Error handling

### **ImagingService (Interface)**
Contract for image management operations.

**Methods:**
- `saveImage(patientId, imageData, modality): ImageMetadata`
- `getImageData(imageId): byte[]`
- `getImagesByPatient(patientId): List<ImageMetadata>`

### **ImagingServiceImpl**
Implementation of image management service.

**Responsibilities:**
- Image metadata persistence
- Image data storage
- Patient-image relationship management
- Image retrieval logic

### **ImageMetadata (Entity)**
JPA entity representing image records in the database.

**Fields:**
- `id`: UUID - Primary key
- `patientId`: UUID - Patient reference
- `data`: byte[] - Binary image content
- `modality`: String - Imaging type
- `format`: String - Image format (PNG, JPEG)
- `createdAt`: LocalDateTime - Creation timestamp

### **ImageRepository**
Spring Data JPA repository for database operations.

**Query Methods:**
- `findById(id): Optional<ImageMetadata>`
- `findAllByPatientId(patientId): List<ImageMetadata>`
- Standard CRUD operations

## Data Model

```
ImageMetadata Entity:
├── id (UUID) - Primary Key
├── patientId (UUID) - Foreign Key to Patient
├── data (BYTEA) - Binary image data
├── modality (VARCHAR) - CT, XRay, MRI, etc.
├── format (VARCHAR) - PNG, JPEG, DICOM
└── createdAt (TIMESTAMP) - Insertion timestamp
```

## Database Setup

### PostgreSQL Initialization

The service automatically creates the `medimage_db` database and `ImageMetadata` table using Hibernate DDL (set to `update`).

### Connection Parameters

```
Host: localhost (configurable via DB_HOST)
Port: 5432
Database: medimage_db
User: postgres (configurable via DB_USER)
Password: password (configurable via DB_PASS)
```

## Workflow Example

### 1. Image Upload Workflow
```
Client
  ↓
POST /api/images (multipart)
  ↓
ImagingController.uploadImage()
  ↓
ImagingServiceImpl.saveImage()
  ↓
ImageRepository.save()
  ↓
PostgreSQL
  ↓
ImageMetadata DTO Response
```

### 2. Image Retrieval Workflow
```
Client
  ↓
GET /api/images/{id}/content
  ↓
ImagingController.getImageContent()
  ↓
ImagingServiceImpl.getImageData()
  ↓
ImageRepository.findById()
  ↓
PostgreSQL
  ↓
Binary Image Data Response
```

## Running the Service

### Prerequisites
- Java 11+
- Maven 3.6+
- PostgreSQL running
- Common module built

### Build
```bash
mvn clean package -DskipTests
```

### Run Locally
```bash
java -jar target/imaging-service-1.0-SNAPSHOT.jar
```

### Docker Build & Run
```bash
docker build -t imaging-service:1.0 .
docker run -d \
  -e DB_HOST=postgres \
  -e DB_USER=postgres \
  -e DB_PASS=password \
  -p 8082:8082 \
  --network medimage-network \
  imaging-service:1.0
```

## Error Handling

| Scenario | Response |
|---|---|
| Image not found | RuntimeException "Image not found" |
| Database error | 500 Internal Server Error |
| Invalid multipart data | 400 Bad Request |
| Large file upload | Process as byte stream |

## Performance Considerations

1. **Image Storage**: Binary data stored in PostgreSQL BYTEA column
2. **Indexing**: Patient ID indexed for fast queries
3. **Transactions**: Transactional read-only for retrieval
4. **Scalability**: Horizontal scaling with load balancing

## Future Enhancements

1. **S3 Integration**: Move image storage to MinIO/S3
2. **Image Compression**: JPEG/PNG compression on upload
3. **Caching**: Redis caching for frequently accessed images
4. **Image Processing**: Thumbnail generation
5. **Async Upload**: Background processing for large files
6. **Versioning**: Support for image versions
7. **Access Control**: Fine-grained permission management
8. **Search**: Advanced image search and filtering

## Troubleshooting

| Issue | Solution |
|---|---|
| Connection refused | Check PostgreSQL is running |
| Table not found | Ensure Hibernate DDL is set to `update` |
| Out of memory | Increase JVM heap size for large images |
| Slow queries | Verify database indexes on patient_id |

## Related Documentation

- See `common/` module for shared models
- See `docker-compose.yaml` for PostgreSQL setup
- See `k8s/` directory for Kubernetes deployment
- See `patient-service/` for client integration

