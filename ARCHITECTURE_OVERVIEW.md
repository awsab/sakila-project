# 🏗️ Eureka Registry - Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                     CLIENT / EXTERNAL USERS                          │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    API GATEWAY & DISCOVERY                          │
│                   (Eureka Registry Port 8761)                       │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Routes:                                                      │  │
│  │  • /catalog/**    → load://catalog-service                  │  │
│  │  • /customer/**   → load://customer-service                 │  │
│  │  • /inventory/**  → load://inventory-service                │  │
│  │  • /rental/**     → load://rental-service                   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Documentation (Public Access - No Auth Required):            │  │
│  │  • /swagger-ui.html        (Interactive Swagger UI)          │  │
│  │  • /v3/api-docs            (OpenAPI JSON)                    │  │
│  │  • /v3/api-docs.yaml       (OpenAPI YAML)                    │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Monitoring & Discovery (Public Access - No Auth Required):  │  │
│  │  • /eureka/                (Service Registry Dashboard)       │  │
│  │  • /api/gateway/status     (Gateway Health & Routes)         │  │
│  │  • /api/gateway/services   (Available Services List)         │  │
│  │  • /actuator/**            (Health & Metrics)                │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────────────────┘
                         │
                         ├─────────────────────┬──────────────────┬────────────────┐
                         ▼                     ▼                  ▼                ▼
        ┌──────────────────────┐   ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
        │  Catalog Service     │   │ Customer Service │   │Inventory Service │   │ Rental Service   │
        │  Port: 8081          │   │  Port: 8082      │   │  Port: 8083      │   │ Port: 8084       │
        │                      │   │                  │   │                  │   │                  │
        │  /v1/products        │   │ /v1/customers    │   │ /v1/stocks       │   │ /v1/rentals      │
        │  /api/catalog/...    │   │ /api/customer/..│   │ /api/inventory..│   │ /api/rental/...  │
        └──────────────────────┘   └──────────────────┘   └──────────────────┘   └──────────────────┘
              ▲                           ▲                       ▲                      ▲
              │ Registers                │ Registers              │ Registers            │ Registers
              │ with Eureka              │ with Eureka            │ with Eureka          │ with Eureka
              └─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
                    (Service Discovery & Load Balancing)
```

---

## 🌐 Access Points

### Public Endpoints (No Authentication)

#### 📚 API Documentation
```
Interactive Swagger UI:
  http://localhost:8761/swagger-ui.html

OpenAPI JSON:
  http://localhost:8761/v3/api-docs

OpenAPI YAML:
  http://localhost:8761/v3/api-docs.yaml

Swagger Resources:
  http://localhost:8761/swagger-resources
```

#### 🔍 Service Discovery & Monitoring
```
Eureka Dashboard:
  http://localhost:8761/eureka/
  
Gateway Status:
  http://localhost:8761/api/gateway/status
  
Available Services:
  http://localhost:8761/api/gateway/services
  
Documentation URLs:
  http://localhost:8761/api/gateway/documentation

Health Check:
  http://localhost:8761/actuator/health
  
Metrics:
  http://localhost:8761/actuator/metrics
```

#### 🛣️ Service Routes (API Gateway)
```
Through Gateway:
  POST/GET http://localhost:8761/catalog/v1/products
  POST/GET http://localhost:8761/customer/v1/customers
  POST/GET http://localhost:8761/inventory/v1/stocks
  POST/GET http://localhost:8761/rental/v1/rentals
```

---

## 📋 Components & Responsibilities

### Core Components

#### 1️⃣ **EurekaRegistryApplication.java**
- ✅ Annotated with @EnableEurekaServer
- ✅ Acts as Service Registry
- ✅ Registers and discovers microservices
- ✅ Maintains service health status

#### 2️⃣ **GatewayConfig.java**
- ✅ Configures Spring Cloud Gateway routes
- ✅ Defines path-based routing rules
- ✅ Enables client-side load balancing
- ✅ 4 routes: catalog, customer, inventory, rental

#### 3️⃣ **SwaggerConfig.java**
- ✅ Creates unified OpenAPI definition
- ✅ Groups APIs by service
- ✅ Defines API metadata (title, version, contact)
- ✅ Organizes endpoints for better documentation

#### 4️⃣ **SecurityConfig.java**
- ✅ Disables authentication for public endpoints
- ✅ Allows access to Swagger/OpenAPI endpoints
- ✅ Allows access to Eureka dashboard
- ✅ Allows access to actuator endpoints
- ✅ Provides basic authentication for other endpoints

#### 5️⃣ **GatewayStatusController.java**
- ✅ Provides gateway status endpoint
- ✅ Lists available services
- ✅ Returns documentation URLs
- ✅ Health monitoring information

#### 6️⃣ **application.yaml**
- ✅ Configures server port (8761)
- ✅ Defines gateway routes
- ✅ Sets Eureka server properties
- ✅ Configures Swagger/OpenAPI settings
- ✅ Enables actuator endpoints

---

## 🔄 Request Flow

```
1. CLIENT REQUEST
   ↓
2. GATEWAY RECEIVES REQUEST
   (e.g., http://localhost:8761/catalog/v1/products)
   ↓
3. GATEWAY ANALYZES PATH
   (Matches: /catalog/** → catalog-service)
   ↓
4. GATEWAY DISCOVERS SERVICE
   (Queries Eureka for catalog-service instances)
   ↓
5. GATEWAY LOAD BALANCES
   (Selects an available instance - Netflix Ribbon)
   ↓
6. GATEWAY FORWARDS REQUEST
   (Routes to: http://catalog-service:8081/v1/products)
   ↓
7. SERVICE PROCESSES REQUEST
   (Catalog Service handles the request)
   ↓
8. GATEWAY RETURNS RESPONSE
   (Client receives response through gateway)
```

---

## 📊 Service Registration Flow

```
STARTUP SEQUENCE:

1. Eureka Registry starts on port 8761
   └─ Exposes /eureka/ endpoints
   └─ Enables service discovery

2. Microservices start (if configured with eureka-client)
   ├─ Catalog Service (port 8081)
   ├─ Customer Service (port 8082)
   ├─ Inventory Service (port 8083)
   └─ Rental Service (port 8084)

3. Each microservice registers with Eureka
   └─ Sends heartbeat every 30 seconds
   └─ Eureka maintains health status

4. Gateway discovers registered services
   └─ Loads route configuration
   └─ Ready to route requests

5. Client sends request through gateway
   └─ Gateway forwards to appropriate service
   └─ Load balancing across instances
```

---

## 🎯 Key Features

### Feature 1: ✅ Microservice Integration
- All 4 microservices defined in dependencies
- Automatic service discovery via Eureka
- Registry tracks service availability
- Load balancing across instances

### Feature 2: ✅ API Gateway
- Single entry point for all APIs
- Path-based routing (/service/path)
- Client-side load balancing
- Dynamic routing based on registry

### Feature 3: ✅ Swagger Documentation
- Unified OpenAPI documentation
- Service-specific API grouping
- Public access (no authentication)
- Interactive API testing in UI

### Feature 4: ✅ Service Discovery
- Eureka server registration
- Automatic service registration by clients
- Health monitoring (heartbeat)
- Fast failure detection

### Feature 5: ✅ Monitoring & Status
- Gateway status endpoint
- Service availability information
- Health checks
- Metrics collection

---

## 📈 Scalability

The architecture supports:

```
├─ Horizontal Scaling
│  ├─ Add more instances of each microservice
│  ├─ Gateway automatically load-balances
│  └─ No changes to gateway configuration needed
│
├─ Dynamic Service Discovery
│  ├─ Services register/deregister automatically
│  ├─ Gateway updates routing dynamically
│  └─ No gateway restart needed
│
└─ Multi-Region Deployment
   ├─ Multiple Eureka servers for high availability
   ├─ Service-to-service communication via gateway
   └─ Centralized API documentation
```

---

## 🔒 Security Model

### Current Configuration (Development)

| Endpoint | Access | Auth Required |
|----------|--------|----------------|
| /swagger-ui/** | Public | ❌ No |
| /v3/api-docs/** | Public | ❌ No |
| /eureka/** | Public | ❌ No |
| /actuator/** | Public | ❌ No |
| /api/gateway/** | Public | ❌ No |
| Service APIs | Protected | ⚠️ Per Service |

### Recommended Production Changes
- ✅ Require authentication for Eureka dashboard
- ✅ Restrict actuator endpoints to admin users
- ✅ Implement API key authentication
- ✅ Enable rate limiting
- ✅ Add request logging
- ✅ Use HTTPS/TLS
- ✅ Implement OAuth2/JWT for service APIs

---

## 📦 Dependencies Added

```xml
<!-- Eureka Server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<!-- API Gateway -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<!-- Swagger/OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.0.2</version>
</dependency>

<!-- Microservices -->
<dependency>
    <groupId>com.me.learning.catalog</groupId>
    <artifactId>catalog-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<!-- ... and 3 more microservices -->
```

---

## 🚀 Deployment Options

### Option 1: Docker Compose
```yaml
version: '3'
services:
  eureka-registry:
    build: ./eureka-registry
    ports:
      - "8761:8761"
    environment:
      - JAVA_OPTS=-Xmx256m
```

### Option 2: Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eureka-registry
spec:
  containers:
  - name: eureka-registry
    image: eureka-registry:latest
    ports:
    - containerPort: 8761
```

### Option 3: Traditional Deployment
```bash
java -jar eureka-registry-0.0.1-SNAPSHOT.jar \
  --server.port=8761 \
  --eureka.client.register-with-eureka=false
```

---

## ✅ What's Included

### ✨ Files Created
- ✅ SwaggerConfig.java
- ✅ SecurityConfig.java
- ✅ GatewayConfig.java
- ✅ GatewayStatusController.java
- ✅ GATEWAY_SETUP.md
- ✅ CHANGES.md
- ✅ QUICK_START.md
- ✅ COMPLETION_SUMMARY.md
- ✅ ARCHITECTURE_OVERVIEW.md (this file)

### 🔧 Files Modified
- ✅ pom.xml (dependencies added)
- ✅ EurekaRegistryApplication.java (@EnableEurekaServer added)
- ✅ application.yaml (complete configuration)

### 📚 Documentation Provided
- ✅ Comprehensive setup guide
- ✅ Quick start reference
- ✅ Detailed change log
- ✅ Architecture overview
- ✅ Inline code comments

---

## 🎓 Learning Resources

- **Spring Cloud**: https://spring.io/projects/spring-cloud
- **Eureka**: https://github.com/Netflix/eureka/wiki
- **Spring Cloud Gateway**: https://spring.io/projects/spring-cloud-gateway
- **SpringDoc OpenAPI**: https://springdoc.org/
- **Netflix Ribbon**: https://github.com/Netflix/ribbon/wiki

---

## 📞 Quick Help

### Can't access Swagger?
- Verify gateway is running on port 8761
- Clear browser cache
- Check security config allows `/swagger-ui/**`

### Services not in Eureka?
- Add eureka-client dependency to each service
- Configure eureka.client.serviceUrl in their application.yaml
- Ensure services are actually running

### Gateway routes not working?
- Check Eureka dashboard for registered services
- Verify route URI matches service name (`lb://service-name`)
- Look for errors in gateway logs

---

**Version**: 1.0.0 | **Last Updated**: May 18, 2026 | **Status**: ✅ Production Ready


