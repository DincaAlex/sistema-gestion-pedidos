# MS-Productos-V3 - API-First Implementation

Microservicio de gestión de productos implementado con enfoque **API-First** usando OpenAPI Generator.

## Características

- **API-First**: Contrato OpenAPI → Código generado
- **Reactive**: Spring WebFlux + R2DBC
- **Event-Driven**: Kafka para operaciones async
- **OAuth2**: JWT authentication
- **Observability**: OpenTelemetry tracing + Prometheus metrics

## Estructura

```
ms-productos-v3/
├── api-specs/productos/
│   └── productos-api-v3.yaml        # Contrato OpenAPI
├── src/main/java/
│   └── com/practica/productos/
│       ├── adapter/rest/            # Código GENERADO por OpenAPI
│       │   ├── api/                 # ProductosApi interface
│       │   └── model/               # DTOs generados
│       ├── controller/              # Implementa API generada
│       ├── converter/               # DTOs ↔ Models
│       ├── model/                   # Modelos de dominio
│       ├── service/                 # Lógica de negocio
│       ├── repository/              # R2DBC repository
│       ├── kafka/                   # Kafka producer
│       ├── event/                   # Event models
│       ├── exception/               # Custom exceptions
│       └── handler/                 # Exception handlers
└── build.gradle                     # OpenAPI Generator plugin
```

## Generar código desde OpenAPI

```bash
./gradlew openApiGenerate
```

Esto genera automáticamente:
- Interfaces de API en `adapter.rest.api`
- DTOs en `adapter.rest.model`
- Validaciones desde OpenAPI spec

## Build

```bash
./gradlew build
```

## Run Local

```bash
./gradlew bootRun
```

## Deploy con Kubernetes + Harbor

### Pre-requisitos

- Kubernetes cluster configurado
- kubectl instalado y configurado
- Harbor registry corriendo (localhost:30002)
- Namespace `practica` creado en Kubernetes

### 1. Build imagen Docker

```bash
# Build con tag para Kubernetes
docker build -t ms-productos-v3:k8s .

# Tag para Harbor
docker tag ms-productos-v3:k8s localhost:30002/library/ms-productos-v3:latest
```

### 2. Push a Harbor registry

```bash
# Login a Harbor
docker login localhost:30002

# Push imagen
docker push localhost:30002/library/ms-productos-v3:latest
```

### 3. Crear namespace (si no existe)

```bash
kubectl create namespace practica
```

### 4. Crear secret para Harbor

```bash
kubectl create secret docker-registry harbor-secret \
  --docker-server=localhost:30002 \
  --docker-username=admin \
  --docker-password=Harbor12345 \
  --namespace=practica
```

### 5. Deploy en Kubernetes

```bash
# Aplicar todos los manifiestos
kubectl apply -f k8s/ms-productos-v3/

# O aplicarlos uno por uno:
kubectl apply -f k8s/ms-productos-v3/01-configmap.yaml
kubectl apply -f k8s/ms-productos-v3/02-secret.yaml
kubectl apply -f k8s/ms-productos-v3/03-deployment.yaml
kubectl apply -f k8s/ms-productos-v3/04-service.yaml
kubectl apply -f k8s/ms-productos-v3/05-service-nodeport.yaml
```

### 6. Verificar despliegue

```bash
# Verificar pods
kubectl get pods -n practica

# Ver logs
kubectl logs -f deployment/ms-productos-v3 -n practica

# Verificar servicio
kubectl get svc -n practica

# Check health
kubectl exec -n practica deployment/ms-productos-v3 -- curl localhost:8088/actuator/health
```

### 7. Acceder al servicio

El servicio está expuesto vía NodePort en el puerto 30088:

```bash
# Acceder al API
curl http://localhost:30088/api/v3/productos

# Swagger UI
http://localhost:30088/swagger-ui.html
```

### Actualizar imagen

```bash
# 1. Build nueva versión
docker build -t ms-productos-v3:k8s .
docker tag ms-productos-v3:k8s localhost:30002/library/ms-productos-v3:latest

# 2. Push a Harbor
docker push localhost:30002/library/ms-productos-v3:latest

# 3. Reiniciar deployment (pull nueva imagen)
kubectl rollout restart deployment/ms-productos-v3 -n practica

# 4. Verificar rollout
kubectl rollout status deployment/ms-productos-v3 -n practica
```

## Configuración Kubernetes

Los manifiestos de Kubernetes están en `k8s/ms-productos-v3/`:

### ConfigMap (01-configmap.yaml)
Configuración de entorno:
- `SPRING_PROFILES_ACTIVE`: k8s
- `SPRING_R2DBC_URL`: Conexión a PostgreSQL
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka endpoint
- `EUREKA_CLIENT_ENABLED`: false (no usa Eureka en K8s)

### Secret (02-secret.yaml)
Credenciales sensibles:
- `DB_PASSWORD`: Contraseña de base de datos

### Deployment (03-deployment.yaml)
- **Namespace**: practica
- **Replicas**: 1
- **Image**: localhost:30002/library/ms-productos-v3:latest
- **ImagePullSecret**: harbor-secret
- **Health Probes**:
  - Liveness: `/actuator/health/liveness` (delay 60s)
  - Readiness: `/actuator/health/readiness` (delay 30s)
- **Resources**:
  - Requests: 512Mi RAM, 250m CPU
  - Limits: 1Gi RAM, 500m CPU

### Service (04-service.yaml)
- Type: ClusterIP
- Port: 8088

### Service NodePort (05-service-nodeport.yaml)
- Type: NodePort
- NodePort: 30088 (acceso externo)

## Endpoints

**En Kubernetes (NodePort)**:
- **API Base**: http://localhost:30088/api/v3/productos
- **Swagger UI**: http://localhost:30088/swagger-ui.html
- **Health**: http://localhost:30088/actuator/health

**En local (bootRun)**:
- **API Base**: http://localhost:8088/api/v3/productos
- **Swagger UI**: http://localhost:8088/swagger-ui.html
- **Health**: http://localhost:8088/actuator/health

## Diferencias con v2

| Aspecto | v2 (Code-First) | v3 (API-First) |
|---------|-----------------|----------------|
| Contrato | Código → OpenAPI | OpenAPI → Código |
| DTOs | Escritos a mano | Generados |
| Validaciones | Anotaciones manuales | Desde OpenAPI |
| Controller | Implementación directa | Implementa interface |
| Modelos | Entity = DTO | Separados con converters |

## Kafka Events

- Topic: `producto-events`
- Events: PRODUCTO_CREATED, PRODUCTO_UPDATED, PRODUCTO_DELETED, STOCK_UPDATED
- Source: `ms-productos-v3`
