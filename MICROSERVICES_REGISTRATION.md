# 🚀 How to Register Microservices with Eureka Registry

## Overview

Each microservice **registers itself** with the Eureka Registry at runtime. The registry doesn't need microService JARs as dependencies.

---

## Configuration Steps for Each Microservice

### Step 1: Add Eureka Client Dependency

Add to each service's `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Step 2: Configure Eureka Client

Add to `application.yaml` in each service:

```yaml
spring:
  application:
    name: catalog-service  # Use appropriate name for each service

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    hostname: localhost
    preferIpAddress: false
```

### Step 3: Enable Eureka Client

Add annotation to main application class:

```java
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEurekaClient
@SpringBootApplication
public class CatalogServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }
}
```

---

## Service Registration Details

### For Each Microservice:

**catalog-service** (`application.yaml`):
```yaml
spring:
  application:
    name: catalog-service

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

**customer-service** (`application.yaml`):
```yaml
spring:
  application:
    name: customer-service

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

**inventory-service** (`application.yaml`):
```yaml
spring:
  application:
    name: inventory-service

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

**rental-service** (`application.yaml`):
```yaml
spring:
  application:
    name: rental-service

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

---

## Startup Sequence

### 1. Start Eureka Registry
```bash
cd eureka-registry
mvn clean spring-boot:run
```

Starts on port 8761
Eureka dashboard: `http://localhost:8761/eureka/`

### 2. Start Services (Any Order)

In separate terminal windows:

```bash
# Terminal 1
cd catalog-service
mvn spring-boot:run
```

```bash
# Terminal 2
cd customer-service
mvn spring-boot:run
```

```bash
# Terminal 3
cd inventory-service
mvn spring-boot:run
```

```bash
# Terminal 4
cd rental-service
mvn spring-boot:run
```

---

## ✅ Verify Registration

### Check Eureka Dashboard

Navigate to: `http://localhost:8761/eureka/`

You should see:
```
Instances currently registered with Eureka

Application         Instances
────────────────────────────────
CATALOG-SERVICE     (1) - UP
CUSTOMER-SERVICE    (1) - UP
INVENTORY-SERVICE   (1) - UP
RENTAL-SERVICE      (1) - UP
```

### Check Gateway Status

```bash
curl http://localhost:8761/api/gateway/status
```

Response:
```json
{
  "status": "UP",
  "service": "API Gateway & Eureka Registry",
  "version": "1.0.0",
  "availableRoutes": {
    "catalog": "/catalog/**",
    "customer": "/customer/**",
    "inventory": "/inventory/**",
    "rental": "/rental/**"
  }
}
```

### Test Gateway Routing

Once services are registered:

```bash
# This routes through gateway to catalog-service
curl http://localhost:8761/catalog/v1/products

# This routes to customer-service
curl http://localhost:8761/customer/v1/customers

# This routes to inventory-service
curl http://localhost:8761/inventory/v1/stocks

# This routes to rental-service
curl http://localhost:8761/rental/v1/rentals
```

---

## Service Discovery in Action

```
How a request flows:

1. Client sends: GET /catalog/v1/products
2. Gateway receives on port 8761
3. Gateway checks route: /catalog/** matches catalog-service
4. Gateway queries Eureka: "Where is catalog-service?"
5. Eureka responds: "At localhost:8081"
6. Gateway forwards to: http://localhost:8081/v1/products
7. Catalog service processes and returns response
8. Gateway returns response to client
```

---

## 🔄 Service Lifecycle

### Startup
1. Service starts with eureka-client dependency
2. Reads Eureka server URL from config
3. Connects to: `http://localhost:8761/eureka/`
4. Registers itself with unique instance ID
5. Sets status to UP
6. Starts sending heartbeats (every 30 sec)

### Running
- Sends heartbeat to Eureka every 30 seconds
- Marked as UP if heartbeat received
- Available for discovery by gateway

### Shutdown
- Service stops sending heartbeats
- After 90 seconds of no heartbeat, Eureka eviction kicks in
- Service status changes to DOWN
- Removed from registry
- Gateway stops routing to it

### Fast Eviction (Configured)
Our configuration uses:
```yaml
eureka:
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 3000
```

This means:
- Faster detection of failed instances (3 seconds vs 90 seconds)
- Better for development and testing
- Services discovered/removed quickly

---

## 📊 Dependency Summary

### Eureka Registry (this module)
```xml
✅ spring-cloud-starter-netflix-eureka-server
✅ spring-cloud-starter-gateway
✅ springdoc-openapi-starter-webflux-ui
❌ NO microservice JARs as dependencies
```

### Each Microservice (catalog, customer, inventory, rental)
```xml
✅ spring-cloud-starter-netflix-eureka-client
✅ Their own dependencies (repositories, DTOs, etc.)
❌ DO NOT depend on eureka-registry
```

---

## ⚠️ Common Issues & Solutions

### Issue 1: Services not appearing in Eureka

**Problem**: Services not showing in Eureka dashboard after 1 minute

**Causes**:
- Eureka Server not running (port 8761)
- Service not compiled (has build errors)
- Eureka URL wrong in service config
- eureka-client dependency missing

**Solution**:
1. Verify Eureka running: `http://localhost:8761/eureka/`
2. Check service logs for errors
3. Verify `eureka.client.serviceUrl.defaultZone` in service config
4. Verify eureka-client dependency in service pom.xml

### Issue 2: Gateway routes not working

**Problem**: Cannot access `/catalog/...` through gateway

**Causes**:
- Service not registered in Eureka
- Route not configured in gateway
- Service not actually running

**Solution**:
1. Check Eureka dashboard for registration
2. Verify gateway config has the route
3. Start the specific microservice
4. Wait for Eureka registration (takes ~10 seconds)

### Issue 3: Circular port conflicts

**Problem**: Services can't start because ports in use

**Solution**: Ensure each service has unique port:
```yaml
# catalog-service
server:
  port: 8081

# customer-service
server:
  port: 8082

# inventory-service
server:
  port: 8083

# rental-service
server:
  port: 8084

# eureka-registry
server:
  port: 8761
```

### Issue 4: Services register but show UNKNOWN

**Problem**: Eureka shows services but status is UNKNOWN

**Solution**: Increase health check time in service config:
```yaml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
  client:
    registry-fetch-interval-seconds: 5
```

---

## 🎯 Best Practices

### Configuration Best Practices

1. **Use service name consistently**
   ```yaml
   spring:
     application:
       name: catalog-service  # Matches service JAR name (without version)
   ```

2. **Use load-balanced gateway routes**
   ```yaml
   # In application.yaml gateway config
   - id: catalog-service
     uri: lb://catalog-service  # lb:// enables load balancing
     predicates:
       - Path=/catalog/**
   ```

3. **Set appropriate health check intervals**
   ```yaml
   eureka:
     instance:
       lease-renewal-interval-in-seconds: 10  # Heartbeat interval
       lease-expiration-duration-in-seconds: 30  # Max gap before eviction
   ```

### Deployment Best Practices

1. **Always start Eureka first**
   - Microservices need it to register

2. **Services order doesn't matter after Eureka**
   - Start in any order once registry is up

3. **Each service independent**
   - Can restart/redeploy one without affecting others
   - Gateway updates routing automatically

4. **Monitor Eureka dashboard**
   - Keep track of service health
   - Watch for UNKNOWN status

---

## 📚 Quick Reference

| Action | Command |
|--------|---------|
| Start registry | `cd eureka-registry && mvn spring-boot:run` |
| Start catalog | `cd catalog-service && mvn spring-boot:run` |
| View Eureka | `http://localhost:8761/eureka/` |
| View Swagger | `http://localhost:8761/swagger-ui.html` |
| Test catalog via gateway | `curl http://localhost:8761/catalog/v1/products` |
| Check gateway status | `curl http://localhost:8761/api/gateway/status` |
| Check health | `curl http://localhost:8761/actuator/health` |

---

## ✅ Verification Checklist

Before considering setup complete:

- [ ] Eureka Registry running on port 8761
- [ ] Eureka dashboard accessible
- [ ] Can see at least one service registered
- [ ] All 4 services appear in Eureka dashboard
- [ ] All services show status as UP
- [ ] Can access Swagger UI at /swagger-ui.html
- [ ] Gateway status returns HTTP 200
- [ ] Can call service APIs through gateway routes
- [ ] All documentation endpoints working

---

**With this setup, you have:**
- ✅ Service discovery and registry
- ✅ Automatic service registration
- ✅ API gateway with load balancing
- ✅ Unified API documentation
- ✅ Health monitoring
- ✅ Independent microservices!


