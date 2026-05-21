# Eureka Registry - API Gateway & Service Discovery

## Overview
The `eureka-registry` module has been configured as a comprehensive **API Gateway and Service Discovery** hub for the Sakila microservices architecture. It now provides:

1. **Eureka Service Discovery** - Centralized service registration and discovery
2. **API Gateway** - Route requests to appropriate microservices
3. **Swagger/OpenAPI Documentation** - Unified API documentation accessible without authentication
4. **Service Status Monitoring** - Health checks and service availability information

## Architecture

### Integrated Microservices
The gateway aggregates and routes to the following microservices:

| Service | Route Path | Port | Function |
|---------|-----------|------|----------|
| Catalog Service | `/catalog/**` | 8081 | Product catalog management |
| Customer Service | `/customer/**` | 8082 | Customer information management |
| Inventory Service | `/inventory/**` | 8083 | Inventory management |
| Rental Service | `/rental/**` | 8084 | Rental operations |

## Server Configuration

### Default Port
- **Eureka Registry**: `8761`

### Key Endpoints

#### Eureka & Gateway Management
- **Eureka Dashboard**: `http://localhost:8761/eureka/`
- **Gateway Status**: `http://localhost:8761/api/gateway/status`
- **Available Services**: `http://localhost:8761/api/gateway/services`
- **Documentation Links**: `http://localhost:8761/api/gateway/documentation`

#### API Documentation (No Authentication Required)
- **Swagger UI**: `http://localhost:8761/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8761/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8761/v3/api-docs.yaml`
- **API Resources**: `http://localhost:8761/swagger-resources`

#### Service Routes
- **Catalog API**: `http://localhost:8761/catalog/...`
- **Customer API**: `http://localhost:8761/customer/...`
- **Inventory API**: `http://localhost:8761/inventory/...`
- **Rental API**: `http://localhost:8761/rental/...`

#### Health & Monitoring (Actuator)
- **Health Check**: `http://localhost:8761/actuator/health`
- **Metrics**: `http://localhost:8761/actuator/metrics`
- **All Endpoints**: `http://localhost:8761/actuator/`

## Configuration Details

### 1. Dependencies Added
The `pom.xml` now includes:
- **Spring Cloud Netflix Eureka Server** - Service discovery
- **Spring Cloud Gateway** - API routing and load balancing
- **SpringDoc OpenAPI UI** - Swagger/OpenAPI documentation
- **All Microservice Modules** - Catalog, Customer, Inventory, and Rental Services

### 2. Gateway Route Configuration
Routes are configured in `application.yaml` and enforced via `GatewayConfig.java`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: catalog-service
          uri: lb://catalog-service  # Load-balanced routing
          predicates:
            - Path=/catalog/**
        - id: customer-service
          uri: lb://customer-service
          predicates:
            - Path=/customer/**
        # ... additional routes
```

### 3. Security Configuration
`SecurityConfig.java` ensures:
- **Public Access** to Swagger UI, API docs, and Eureka dashboard
- **No Authentication Required** for API documentation
- **Protected** other endpoints that may require authentication
- **CSRF Protection** disabled for API-first architecture

### 4. OpenAPI/Swagger Configuration
`SwaggerConfig.java` provides:
- **Unified OpenAPI Documentation** with service grouping
- **Separated Documentation** for each microservice
- **Professional Information** including contact and license details
- **Grouped APIs** by service for better organization

### 5. Gateway Status Controller
`GatewayStatusController.java` exposes:
- `GET /api/gateway/status` - Overall gateway status
- `GET /api/gateway/services` - List of available services
- `GET /api/gateway/documentation` - Documentation endpoints

## Startup Instructions

### Prerequisites
Ensure all microservices are built and available:
```bash
cd sakila-project
mvn clean install -DskipTests
```

### Start Eureka Registry
```bash
cd eureka-registry
mvn spring-boot:run
```

Or build and run:
```bash
mvn clean package
java -jar target/eureka-registry-0.0.1-SNAPSHOT.jar
```

### Start Individual Microservices (Optional)
In separate terminals, start each service:
```bash
cd catalog-service && mvn spring-boot:run
cd customer-service && mvn spring-boot:run
cd inventory-service && mvn spring-boot:run
cd rental-service && mvn spring-boot:run
```

## Using the API Gateway

### Example Request
```bash
# Access catalog service through gateway
curl http://localhost:8761/catalog/v1/products

# Access customer service through gateway
curl http://localhost:8761/customer/v1/customers

# Access inventory service through gateway
curl http://localhost:8761/inventory/v1/stocks

# Access rental service through gateway
curl http://localhost:8761/rental/v1/rentals
```

## Eureka Service Discovery

### How It Works
1. **Registration**: Each microservice registers with Eureka using `spring-cloud-starter-netflix-eureka-client`
2. **Discovery**: The gateway discovers services dynamically from Eureka
3. **Load Balancing**: The `lb://` prefix in routes enables client-side load balancing
4. **Health Checks**: Eureka periodically checks service health and removes unhealthy instances

### View Registered Services
- Access the **Eureka Dashboard** at `http://localhost:8761/eureka/`
- All registered services will be listed with their status and instances

## OpenAPI Documentation

### Accessing Swagger UI
1. Navigate to: `http://localhost:8761/swagger-ui.html`
2. Select service from dropdown (catalog, customer, inventory, or rental)
3. Explore endpoints and execute requests directly from the UI

### API Documentation Format
The gateway provides documentation in multiple formats:
- **Swagger UI**: Interactive web-based interface
- **OpenAPI JSON**: Machine-readable format for integrations
- **OpenAPI YAML**: Human-readable configuration format

## API Groups
Each microservice has its own grouped API in the documentation:

- **Catalog Service** - Paths matching `/catalog/**`
- **Customer Service** - Paths matching `/customer/**`
- **Inventory Service** - Paths matching `/inventory/**`
- **Rental Service** - Paths matching `/rental/**`

## Troubleshooting

### Services Not Showing in Eureka
- Ensure each microservice has `spring-cloud-starter-netflix-eureka-client` dependency
- Check that each service has `eureka.client.serviceUrl.defaultZone` configured correctly
- Verify all services are running

### Gateway Routes Not Working
- Confirm services are registered in Eureka (check Eureka dashboard)
- Check logs for routing configuration errors
- Verify `lb://` prefix in route URIs for proper load balancing

### Swagger Documentation Not Loading
- Clear browser cache and reload
- Check that `springdoc-openapi-starter-webflux-ui` is in dependencies
- Verify security configuration allows access to `/swagger-ui/**` and `/v3/api-docs/**`

### Port Already in Use
If port 8761 is already in use, change it in `application.yaml`:
```yaml
server:
  port: 8762  # or any available port
```

## Security Notes

The current configuration disables authentication for:
- Swagger UI and documentation endpoints
- Eureka dashboard
- Actuator endpoints

For **production environments**, consider:
1. Enabling authentication for Eureka dashboard
2. Restricting access to actuator endpoints
3. Using API keys or OAuth2 for service-to-service communication
4. Implementing request throttling and rate limiting
5. Adding request/response logging and monitoring

## Performance Tuning

### Eureka Server Settings (in application.yaml)
```yaml
eureka:
  server:
    enable-self-preservation: false      # Disable self-preservation for faster detection
    eviction-interval-timer-in-ms: 3000  # Faster eviction of failed instances
```

### Gateway Performance
- Load balancing is handled by Netflix Ribbon
- Consider increasing thread pool size for high-traffic scenarios
- Monitor memory usage and adjust JVM settings as needed

## Next Steps

1. Configure each microservice to register with Eureka
2. Test API routing through the gateway
3. Monitor service health via Eureka dashboard
4. Set up logging and monitoring infrastructure
5. Configure environment-specific properties (dev, staging, prod)

## Support & Documentation

For more information:
- [Spring Cloud Eureka Documentation](https://spring.io/guides/gs/service-registration-and-discovery/)
- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Netflix Eureka Wiki](https://github.com/Netflix/eureka/wiki)

