# Notification Service

## Overview

The **Notification Service** is a microservice in the MedImage system responsible for delivering real-time notifications to clients about medical examination completion and analysis results. It uses Server-Sent Events (SSE) for real-time push notifications and RabbitMQ for asynchronous event consumption.

## Purpose

This service acts as a **real-time notification hub** that:
- Receives examination completion and analysis result events
- Manages client notification subscriptions
- Delivers real-time notifications via Server-Sent Events (SSE)
- Handles multiple concurrent client connections
- Implements pub/sub notification pattern
- Provides WebSocket-like real-time communication

## Key Features

### 1. **Real-Time Notification Delivery**
- Server-Sent Events (SSE) for browser-native real-time communication
- Multiple concurrent client connections
- Persistent emitter management
- Graceful client disconnection handling

### 2. **Message-Driven Architecture**
- RabbitMQ-based event consumption
- Pub/Sub notification pattern
- Asynchronous event processing
- Jackson2Json message serialization

### 3. **Subscription Management**
- Register/unregister notification subscribers
- Maintain persistent emitter connections
- Track active subscribers
- Automatic cleanup of closed connections

### 4. **Event Type Support**
- Research completion events
- Analysis result notifications
- Examination status updates
- Extensible event model

### 5. **Multi-Receiver Broadcasting**
- Send notifications to multiple subscribers
- Filter events by user/patient
- Batch notification sending

## Service Dependencies

### **Internal Dependencies**

| Service/Module | Purpose |
|---|---|
| **Common Module** | Provides shared event models (ResearchCompletedNotificationEvent) |

### **External Service Dependencies**

| Service | Interaction | Type |
|---|---|---|
| **Patient Service** | Receives completion events via RabbitMQ | Async Messaging |
| **Analytical Model** | Receives analysis results via RabbitMQ | Async Messaging |

### **External Technologies**

| Technology | Purpose |
|---|---|
| **Spring Boot Web** | REST API and HTTP server |
| **Spring AMQP** | RabbitMQ messaging |
| **RabbitMQ** | Asynchronous event broker |
| **Server-Sent Events (SSE)** | Real-time push to browsers |
| **Lombok** | Code generation for services |

### **Services That Depend on This Module**

| Service | Interaction |
|---|---|
| **Frontend/UI** | Receives real-time notifications via SSE |
| **Patient Service** | Sends events to notification queue |
| **Analytical Model** | Sends result events |

## REST API Endpoints

### **Subscribe to Notifications**
```
GET /api/notifications/subscribe
Accept: text/event-stream

Response:
  Server-Sent Events stream
  
Example Event:
  event: research-completed
  data: {"type":"RESEARCH_COMPLETED","patientId":"uuid","result":{...}}
```

### **Receive Notifications (SSE)**
Clients subscribe via standard SSE protocol:

```javascript
const eventSource = new EventSource('/api/notifications/subscribe');

eventSource.addEventListener('research-completed', (event) => {
  console.log('Notification:', JSON.parse(event.data));
});

eventSource.addEventListener('error', (event) => {
  console.error('Connection error:', event);
  eventSource.close();
});
```

## Configuration

### Application Properties (`application.yaml`)

| Property | Description | Default Value | Environment Variable |
|---|---|---|---|
| **server.port** | HTTP server port for the service | `8084` | - |
| **spring.application.name** | Application name identifier | `notification-service` | - |
| **spring.rabbitmq.host** | RabbitMQ broker hostname | `localhost` | `RABBIT_HOST` |
| **spring.rabbitmq.port** | RabbitMQ broker port | `5672` | `RABBIT_PORT` |
| **spring.rabbitmq.username** | RabbitMQ authentication username | `user` | `RABBIT_USER` |
| **spring.rabbitmq.password** | RabbitMQ authentication password | `password` | `RABBIT_PASS` |
| **s3.endpoint** | S3/MinIO endpoint URL | `http://localhost:9000` | `S3_ENDPOINT` |
| **s3.access-key** | S3 access key | `minioadmin` | `S3_ACCESS_KEY` |
| **s3.secret-key** | S3 secret key | `minioadmin` | `S3_SECRET_KEY` |
| **s3.bucket** | S3 bucket for resources | `med-staging` | - |
| **s3.region** | AWS region for S3 | `us-east-1` | - |

## Project Structure

```
notification-service/
├── src/
│   ├── main/
│   │   ├── java/org/trilgar/medimage/ssl/notification/
│   │   │   ├── NotificationServiceApplication.java # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── ResearchCompletedNotificationController.java # SSE endpoint
│   │   │   ├── service/
│   │   │   │   ├── api/
│   │   │   │   │   └── NotificationSubscriptionService.java # Service interface
│   │   │   │   └── ResearchCompltedNotificationSubscriptionService.java # Impl
│   │   │   ├── config/
│   │   │   │   └── RabbitConfig.java               # RabbitMQ configuration
│   │   │   ├── listener/
│   │   │   │   └── ResearchCompletedNotificationListener.java # Event listener
│   │   │   └── resources/
│   │   │       └── application.yaml                # Application configuration
│   └── test/
│       └── java/
├── pom.xml
├── Dockerfile
└── README.md (this file)
```

## Key Classes

### **NotificationServiceApplication**
- Spring Boot entry point
- Initializes the microservice
- Configures Spring context

### **ResearchCompletedNotificationController**
REST endpoint handler for SSE subscription.

**Endpoints:**
- `GET /api/notifications/subscribe` - Subscribe to real-time events via SSE

**Features:**
- SSE connection management
- Event stream setup
- Client timeout handling
- Error handling

**Implementation:**
```java
@GetMapping("/subscribe")
public SseEmitter subscribe() {
  SseEmitter emitter = new SseEmitter(300000L); // 5 minute timeout
  subscriptionService.subscribe(emitter);
  return emitter;
}
```

### **NotificationSubscriptionService (Interface)**
Generic contract for subscription management.

**Type Parameters:**
- `T` - Emitter type (SseEmitter)
- `E` - Event type (ResearchCompletedNotificationEvent)

**Key Methods:**
- `subscribe(emitter: T): void`
- `sendNotification(event: E): void`
- `unsubscribe(emitter: T): void`

### **ResearchCompltedNotificationSubscriptionService**
Implementation of subscription management for research completion events.

**Responsibilities:**
- Manage SseEmitter collection
- Handle client subscriptions/unsubscriptions
- Broadcast notifications to all subscribers
- Handle emitter errors (client disconnect)
- Clean up closed connections

**Features:**
```java
public class ResearchCompltedNotificationSubscriptionService 
    implements NotificationSubscriptionService<SseEmitter, ResearchCompletedNotificationEvent> {
  
  private Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();
  
  @Override
  public void subscribe(SseEmitter emitter) {
    emitters.add(emitter);
  }
  
  @Override
  public void sendNotification(ResearchCompletedNotificationEvent event) {
    emitters.forEach(emitter -> {
      try {
        emitter.send(SseEmitter.event()
          .name("research-completed")
          .data(event)
          .id(UUID.randomUUID().toString()));
      } catch (IOException e) {
        emitters.remove(emitter); // Client disconnected
      }
    });
  }
}
```

### **ResearchCompletedNotificationListener**
RabbitMQ message listener for completion events.

**Functionality:**
- Listens on `notification_queue`
- Processes ResearchCompletedNotificationEvent messages
- Broadcasts events to all subscribers
- Handles errors gracefully

**Workflow:**
```
RabbitMQ Message
  ↓
ResearchCompletedNotificationListener.handleNotification()
  ↓
subscriptionService.sendNotification()
  ↓
Broadcast to all connected SSE clients
```

### **RabbitConfig**
Spring configuration for RabbitMQ integration.

**Provides:**
- Queue bean definitions
- Jackson2JsonMessageConverter
- Message routing configuration

## Message Queue Configuration

### **RabbitMQ Queues**

| Queue | Purpose | Producer | Consumer |
|---|---|---|---|
| `notification_queue` | Completion/result notifications | Patient Service | Notification Service |

## Event Model

### **ResearchCompletedNotificationEvent**
Event model for research completion notifications.

```java
{
  "type": "RESEARCH_COMPLETED",           // Event type identifier
  "patientId": "UUID",                    // Patient reference
  "result": {                             // Analysis result
    "requestId": "UUID",
    "patientId": "UUID",
    "s3ObjectKey": "String",
    "riskScore": "double",
    "diagnosisLabel": "String",
    "isCritical": "boolean",
    "analyzedAt": "LocalDateTime"
  }
}
```

## SSE Communication Details

### **Server-Sent Events**
- One-way real-time communication from server to client
- Standard HTTP protocol (no WebSocket overhead)
- Browser-native support (EventSource API)
- Automatic reconnection on disconnect
- Text-based event format

### **Event Format**
```
event: research-completed
id: {uuid}
data: {JSON event data}

```

### **Client Implementation**
```javascript
const eventSource = new EventSource('/api/notifications/subscribe');

// Handle research completion events
eventSource.addEventListener('research-completed', (event) => {
  const notification = JSON.parse(event.data);
  console.log('Patient:', notification.result.patientId);
  console.log('Risk Score:', notification.result.riskScore);
});

// Handle connection errors
eventSource.addEventListener('error', (event) => {
  if (event.readyState === EventSource.CLOSED) {
    console.log('Connection closed');
  } else {
    console.error('Connection error');
  }
});

// Close connection
eventSource.close();
```

## Workflow Example

### 1. Real-Time Notification Workflow
```
Analysis Complete
  ↓
Analytical Model sends result
  ↓
Patient Service publishes to notification_queue
  ↓
ResearchCompletedNotificationListener receives event
  ↓
subscriptionService.sendNotification()
  ↓
Broadcast to all subscribed SSE clients
  ↓
Browser receives event in real-time
  ↓
UI updates with result
```

## Running the Service

### Prerequisites
- Java 11+
- Maven 3.6+
- RabbitMQ running
- Common module built

### Build
```bash
mvn clean package -DskipTests
```

### Run Locally
```bash
java -jar target/notification-service-1.0-SNAPSHOT.jar
```

### Docker Build & Run
```bash
docker build -t notification-service:1.0 .
docker run -d \
  -e RABBIT_HOST=rabbitmq \
  -e RABBIT_PORT=5672 \
  -p 8084:8084 \
  --network medimage-network \
  notification-service:1.0
```

## Connection Management

### **Client Connection Lifecycle**

1. **Subscribe**
   - Client calls: `GET /api/notifications/subscribe`
   - Server creates SseEmitter
   - Server registers emitter in subscription service

2. **Active Connection**
   - Server sends events as they arrive
   - Client processes events in real-time
   - Connection stays open

3. **Disconnect**
   - Client closes connection (page close, manual close)
   - IOException thrown on send attempt
   - Server removes emitter from collection
   - Automatic cleanup

### **Timeout Handling**
- Default SSE timeout: 300 seconds (5 minutes)
- Client can re-subscribe after timeout
- Browser EventSource automatically reconnects

## Error Handling

| Scenario | Response |
|---|---|
| Client disconnect | IOException caught, emitter removed |
| RabbitMQ unavailable | Service degradation, queued events |
| Message parsing error | Logged, continue processing |
| Send to closed client | Silently remove and skip |

## Performance Considerations

1. **Concurrent Connections**: ConcurrentHashMap for thread-safe emitter storage
2. **Memory**: One SseEmitter per connected client
3. **Broadcasting**: O(n) complexity for n connected clients
4. **Network**: Low bandwidth - text-based SSE protocol

## Future Enhancements

1. **Event Filtering**: Filter events by patient/user
2. **Persistence**: Store notification history
3. **User Preferences**: Notification preferences (opt-in/out)
4. **Email/SMS Fallback**: Send critical alerts via email/SMS
5. **Notification Queue**: Queue events for disconnected clients
6. **Analytics**: Track notification delivery metrics
7. **WebSocket Support**: Alternative to SSE for better compatibility
8. **Message Templates**: Customizable notification messages

## Browser Compatibility

| Browser | SSE Support |
|---|---|
| Chrome | ✅ Yes |
| Firefox | ✅ Yes |
| Safari | ✅ Yes |
| Edge | ✅ Yes |
| IE 11 | ❌ No (use polyfill) |

## Security Considerations

1. **CORS**: Configure CORS for cross-origin notifications
2. **Authentication**: Implement client authentication if needed
3. **Event Filtering**: Ensure clients only see their events
4. **DoS Protection**: Limit concurrent connections
5. **SSL/TLS**: Use HTTPS for production

## Troubleshooting

| Issue | Solution |
|---|---|
| Notifications not received | Check RabbitMQ queue and listener |
| Connection dropped | Verify timeout settings and network |
| High memory usage | Monitor connected emitter count |
| Events lost | Check message queue persistence |
| Browser compatibility | Use SSE polyfill for IE11 |

## Related Documentation

- See `common/` module for shared event models
- See `patient-service/` for event publishing
- See `analytical-model/` for analysis completion
- See `docker-compose.yaml` for infrastructure setup
- See `k8s/` directory for Kubernetes deployment

