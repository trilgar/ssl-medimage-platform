# Analytical Model Service

## Overview

The **Analytical Model Service** is a core microservice in the MedImage system responsible for performing AI-powered medical image analysis and risk assessment. It processes medical images (primarily chest X-rays) to detect and classify pathologies, generating risk scores and clinical assessments.

## Purpose

This service acts as an **AI inference engine** that:
- Receives image analysis requests from other services via message queues
- Downloads medical images from S3-compatible object storage (MinIO)
- Performs image analysis using machine learning models
- Generates risk assessment results with pathology classifications
- Publishes results back to message queues for downstream processing

## Key Features

### 1. **Asynchronous Message Processing**
- Listens for image analysis requests from the `analysis_queue` (RabbitMQ)
- Processes requests without blocking, ensuring scalability
- Uses Jackson2JsonMessageConverter for JSON serialization

### 2. **AI Model Inference**
- Currently implements a **MockAiModelService** for development/testing
- Generates risk scores (0.0 - 1.0) and pathology classifications
- Supports three risk levels:
  - **HIGH_RISK_PNEUMONIA**: score > 0.85
  - **MODERATE_RISK**: score 0.50 - 0.85
  - **NO_PATHOLOGY**: score < 0.50

### 3. **S3 Image Storage Integration**
- Downloads medical images from MinIO (or any S3-compatible storage)
- Works with the `med-staging` bucket
- Supports binary image data processing

### 4. **Result Publishing**
- Publishes risk assessment results to the `risk_assessment_queue`
- Results include:
  - Request ID and Patient ID
  - Risk score and pathology label
  - Critical flag for high-risk cases
  - Timestamp of analysis

## Service Dependencies

### **Internal Dependencies**

| Service/Module | Purpose |
|---|---|
| **Common Module** | Provides shared DTOs and utilities (RiskAssessmentResult, ImageAnalysisRequest) |
| **S3 Service (CommonS3Config)** | Provides S3/MinIO storage access for image retrieval |

### **External Dependencies**

| Technology | Purpose |
|---|---|
| **RabbitMQ** | Message broker for async request/response communication |
| **MinIO/S3** | Object storage for medical image persistence |
| **Spring Boot** | Application framework and microservice foundation |
| **Spring AMQP** | RabbitMQ integration and message handling |
| **Lombok** | Code generation for DTOs and utility classes |

## Configuration

### Application Properties (`application.yaml`)

| Property | Description | Default Value | Environment Variable |
|---|---|---|---|
| **server.port** | HTTP server port for the service | `8083` | - |
| **spring.application.name** | Application name identifier | `analytical-model` | - |
| **spring.rabbitmq.host** | RabbitMQ broker hostname | `localhost` | `RABBIT_HOST` |
| **spring.rabbitmq.port** | RabbitMQ broker port | `5672` | `RABBIT_PORT` |
| **spring.rabbitmq.username** | RabbitMQ authentication username | `user` | `RABBIT_USER` |
| **spring.rabbitmq.password** | RabbitMQ authentication password | `password` | `RABBIT_PASS` |
| **s3.endpoint** | S3/MinIO endpoint URL | `http://localhost:9000` | `S3_ENDPOINT` |
| **s3.access-key** | S3 access key (AWS Access Key ID) | `minioadmin` | `S3_ACCESS_KEY` |
| **s3.secret-key** | S3 secret key (AWS Secret Access Key) | `minioadmin` | `S3_SECRET_KEY` |
| **s3.bucket** | S3 bucket name for medical images | `med-staging` | - |
| **s3.region** | AWS region for S3 operations | `us-east-1` | - |

## Key Classes

### **AnalyticalServiceApplication**
- Spring Boot entry point
- Imports CommonS3Config for S3 integration
- Initializes the microservice

### **AnalysisListener**
- RabbitMQ message listener
- Listens on `analysis_queue`
- Orchestrates image download → analysis → result publishing
- Implements error handling and logging

### **AnalyticalService (Interface)**
- Defines the contract for image analysis
- Method: `analyze(requestId, patientId, s3Key, imageData) → RiskAssessmentResult`

### **MockAiModelService**
- Implements AnalyticalService
- Simulates AI model inference with 2-second delay
- Generates random risk scores for development/testing
- Can be replaced with real ML model integration

### **RabbitConfig**
- Configures RabbitMQ queues
- Defines message converters (Jackson2Json)
- Queue definitions:
  - `analysis_queue`: Input for analysis requests
  - `risk_assessment_queue`: Output for results

## Related Documentation

- See `docker-compose.yaml` for local environment setup
- See `k8s/` directory for Kubernetes deployment manifests
- See `common/` module for shared data models



