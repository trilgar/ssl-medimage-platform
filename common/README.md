# Common Module

## Overview

The **Common Module** is a shared library module in the MedImage system that provides reusable utilities, data models, and configurations across all microservices. It serves as the foundation for inter-service communication and S3/MinIO integration.

## Purpose

This module acts as a **dependency hub** that:
- Defines shared domain models and DTOs for all services
- Provides S3/MinIO storage abstraction and integration
- Offers common configurations and utilities
- Enables standardized communication between microservices
- Ensures consistency across the entire system

## Key Features

### 1. **Shared Data Models**
- Standardized DTOs for medical data processing
- Consistent entity definitions across services
- Serializable message objects for async communication

### 2. **S3/MinIO Integration**
- Unified storage service interface
- Image upload and download capabilities
- Bucket initialization and management
- Support for AWS S3 and S3-compatible storages (MinIO)

### 3. **AWS SDK Integration**
- Built-in AWS SDK for S3 operations
- S3Client configuration and management
- Automatic bucket provisioning

### 4. **Spring Boot Integration**
- Auto-configured beans for dependency injection
- CommonS3Config for easy integration
- Lombok support for reduced boilerplate

## Module Dependencies

### **External Dependencies**

| Technology | Purpose |
|---|---|
| **Spring Boot Starter** | Core Spring framework and DI |
| **AWS SDK S3** | Amazon S3 client for object storage operations |
| **Lombok** | Code generation for DTOs and utility classes |

### **Dependents**

All other microservices depend on this module:
- **Analytical Model Service** - Uses shared DTOs and S3 service
- **Imaging Service** - Uses shared data models
- **Patient Service** - Uses shared DTOs and domain models
- **Notification Service** - Uses shared event models
- **Radiology Service** - Uses shared models and S3 integration

## Shared Data Models

### **ImageAnalysisRequest**
Data transfer object for requesting medical image analysis.

```java
{
  "requestId": "UUID",          // Unique request identifier
  "patientId": "UUID",          // Patient being analyzed
  "s3ObjectKey": "String",      // S3 path to the image
  "modality": "String"          // Imaging modality (CT, XRay, etc.)
}
```

### **RiskAssessmentResult**
Result of AI analysis with risk scores and diagnosis.

```java
{
  "requestId": "UUID",          // Original request ID
  "patientId": "UUID",          // Patient analyzed
  "s3ObjectKey": "String",      // Image reference
  "riskScore": "double",        // Risk score (0.0-1.0)
  "diagnosisLabel": "String",   // Diagnosis classification
  "isCritical": "boolean",      // Critical case flag
  "analyzedAt": "LocalDateTime" // Timestamp of analysis
}
```

### **PatientExaminationRequest**
Request to initiate a patient examination.

```java
{
  "patientId": "UUID",          // Patient identifier
  "modality": "String",         // Examination type
  "notes": "String",            // Clinical notes
  "isUrgent": "boolean"         // Urgency flag
}
```

### **ResearchCompletedNotificationEvent**
Event notification when research/analysis completes.

```java
{
  "type": "String",             // Event type identifier
  "patientId": "UUID",          // Patient reference
  "result": "RiskAssessmentResult" // Analysis results
}
```

## Core Classes

### **S3StorageService (Interface)**
Abstract interface for S3 storage operations.

**Methods:**
- `downloadImage(String key): byte[]` - Download image from S3
- `upload(byte[] data, String extension): String` - Upload data to S3 and return key

### **S3StorageServiceImpl**
Implementation of S3StorageService using AWS SDK.

**Features:**
- Automatic bucket initialization
- S3-compatible storage support (MinIO)
- UUID-based file naming
- Error handling and logging
- Binary data streaming

### **CommonS3Config**
Spring configuration class for S3 integration.

**Provides:**
- S3Client bean configuration
- Endpoint customization
- Credentials management
- Auto-wire capabilities for other services

## Configuration

### Application Properties (Injected from Services)

| Property | Description | Default Value |
|---|---|---|
| **s3.endpoint** | S3/MinIO endpoint URL | `http://localhost:9000` |
| **s3.access-key** | S3 access key | `minioadmin` |
| **s3.secret-key** | S3 secret key | `minioadmin` |
| **s3.bucket** | Default bucket name | `med-staging` |
| **s3.region** | AWS region | `us-east-1` |

## Usage Example

### In a Microservice

```java
@SpringBootApplication
@Import(CommonS3Config.class)
public class MyMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyMicroserviceApplication.class, args);
    }
}

@Service
@RequiredArgsConstructor
public class MyService {
    private final S3StorageService s3Service;
    
    public void processImage(String s3Key) {
        byte[] imageData = s3Service.downloadImage(s3Key);
        // Process image...
    }
}
```

## Project Structure

```
common/
├── src/
│   ├── main/
│   │   ├── java/org/trilgar/medimage/ssl/
│   │   │   ├── model/
│   │   │   │   ├── ImageAnalysisRequest.java      # Analysis request DTO
│   │   │   │   ├── RiskAssessmentResult.java      # Risk assessment result DTO
│   │   │   │   ├── PatientExaminationRequest.java # Examination request DTO
│   │   │   │   └── ResearchCompletedNotificationEvent.java # Notification event
│   │   │   └── s3/
│   │   │       ├── api/
│   │   │       │   └── S3StorageService.java      # S3 service interface
│   │   │       ├── config/
│   │   │       │   └── CommonS3Config.java        # S3 configuration
│   │   │       └── S3StorageServiceImpl.java       # S3 implementation
│   └── test/
│       └── java/
├── pom.xml
└── README.md (this file)
```

## Integration Points

### With Analytical Model Service
- Provides: `RiskAssessmentResult`, `ImageAnalysisRequest`
- Uses: `S3StorageService` for image retrieval

### With Imaging Service
- Provides: `ImageAnalysisRequest` DTO
- Provides: `S3StorageService` for image management

### With Patient Service
- Provides: `RiskAssessmentResult`, `PatientExaminationRequest`
- Provides: Shared DTOs for communication

### With Notification Service
- Provides: `ResearchCompletedNotificationEvent`
- Provides: Event models for async messaging

### With Radiology Service
- Provides: `PatientExaminationRequest`, `ImageAnalysisRequest`
- Provides: `S3StorageService` for image upload/download

## Building

```bash
mvn clean package -DskipTests
```

## Installation in Other Services

Add this dependency to any service's `pom.xml`:

```xml
<dependency>
    <groupId>org.trilgar.medimage.ssl</groupId>
    <artifactId>common</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## S3 Storage Behavior

### Bucket Initialization
- On startup, the service checks if the configured S3 bucket exists
- If not found, it creates the bucket automatically
- If connection fails, it logs an error but continues operation

### File Upload
- Files are stored with UUID-based names
- File extension is preserved
- Content-Type is set based on extension
- Returns the S3 key for future reference

### File Download
- Files are downloaded as byte arrays
- Full error handling for missing files
- Logging of all operations

## Error Handling

- **NoSuchBucketException**: Handled by automatic bucket creation
- **S3 Connection Errors**: Logged with context information
- **File Not Found**: Throws exception with descriptive message
- **Access Errors**: Logged and propagated to caller

## Security Considerations

- S3 credentials should be provided via environment variables
- Default credentials (minioadmin) are for development only
- In production, use proper IAM credentials or S3 access keys
- Bucket access should be restricted via S3 policies

## Future Enhancements

1. **Versioning**: Support for S3 object versioning
2. **Encryption**: Server-side encryption support
3. **Caching**: Local caching of frequently accessed images
4. **Batch Operations**: Bulk upload/download support
5. **Lifecycle Management**: Automatic cleanup of old files
6. **Event Notifications**: S3 event subscriptions

## Related Documentation

- See individual microservice READMEs for specific implementations
- See `docker-compose.yaml` for MinIO setup
- See `k8s/` for production deployment

