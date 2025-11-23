# Sistema de Gestión de Pedidos

Sistema de microservicios reactivo para la gestión de pedidos y productos, implementando arquitecturas modernas como CQRS, Event-Driven, API-First, con OAuth2, Service Discovery y Observabilidad completa.

## Arquitectura del Sistema

```
                          ┌─────────────────────────────┐
                          │   Gateway Service (8080)    │
                          │   OAuth2 + Routing          │
                          └──────────────┬──────────────┘
                                         │
                    ┌────────────────────┼────────────────────┐
                    │                    │                    │
           ┌────────▼────────┐  ┌────────▼────────┐  ┌────────▼────────┐  ┌───────▼──────┐
           │  ms-productos   │  │ ms-productos-v2 │  │ ms-productos-v3 │  │  ms-pedidos  │
           │   (v1: 8081)    │  │  (CQRS Pattern) │  │  (API-First)    │  │ (Port: 8084) │
           │   Sync CRUD     │  │  Read: 8082     │  │  Reactive: 8088 │  │              │
           │                 │  │  Write: 8083    │  │  Kafka Events   │  │              │
           └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  └──────┬───────┘
                    │                    │ Events            │
                    │           ┌────────▼────────┐          │
                    │           │  Kafka (9092)   │          │
                    │           │  producto-events│          │
                    │           └────────┬────────┘          │
                    │                    │ Consumer          │
                    │           ┌────────▼────────┐          │
                    │           │ ms-productos-   │          │
                    │           │   writer        │          │
                    │           │  (Port: 8086)   │          │
                    │           └────────┬────────┘          │
                    │                    │                   │
                    └────────────────────┼───────────────────┘
                                         ▼
                                ┌─────────────────┐
                                │   PostgreSQL    │
                                │   (Port: 5432)  │
                                └─────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│              Infraestructura y Servicios                         │
├──────────────────┬─────────────────┬─────────────────────────────┤
│ Registry Service │ Config Server   │ OAuth Server (9000)         │
│  Eureka (8099)   │   (8888)        │ Authorization Server        │
└──────────────────┴─────────────────┴─────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                    Observabilidad Stack                          │
├──────────────┬─────────────┬─────────────┬──────────────────────┤
│ Prometheus   │    Loki     │    Tempo    │     Grafana          │
│   (9090)     │   (3100)    │  (4317/18)  │     (3000)           │
│   Metrics    │    Logs     │   Traces    │   Dashboards         │
└──────────────┴─────────────┴─────────────┴──────────────────────┘
```

## Microservicios

### MS-Productos (v1) - Puerto 8081
Microservicio clásico de productos con operaciones CRUD síncronas.
- **Swagger UI**: http://localhost:8080/webjars/swagger-ui/index.html (via Gateway)
- **Patrón**: Tradicional CRUD
- **Base de datos**: PostgreSQL

### MS-Productos-V2 (CQRS) - Puertos 8082 (Read) / 8083 (Write)
Implementación del patrón CQRS con separación de lectura y escritura.
- **Read Swagger**: http://localhost:8082/swagger-ui.html
- **Write Swagger**: http://localhost:8083/swagger-ui.html
- **Patrón**: CQRS + Event Sourcing
- **Mensajería**: Kafka (producto-events)

### MS-Productos-V3 (API-First) - Puerto 8088
Microservicio reactivo con enfoque API-First usando OpenAPI Generator.
- **Swagger UI**: http://localhost:8088/webjars/swagger-ui/index.html
- **Patrón**: API-First, Reactive WebFlux
- **Deployment**: Docker & Kubernetes
- **Mensajería**: Kafka (producto-events)
- **Documentación**: [README-ms-productos-v3](./ms-productos-v3/README.md)

### MS-Pedidos - Puerto 8084
Microservicio de gestión de pedidos.
- **Swagger UI**: http://localhost:8084/swagger-ui.html
- **Integración**: Consume APIs de productos

## Infraestructura

### Gateway Service - Puerto 8080
API Gateway con Spring Cloud Gateway, OAuth2 y enrutamiento inteligente.
- **URL**: http://localhost:8080
- **Autenticación**: OAuth2 Resource Server

### Eureka Registry - Puerto 8099
Service Discovery y registro de microservicios.
- **Dashboard**: http://localhost:8099

### Config Server - Puerto 8888
Configuración centralizada para todos los microservicios.
- **URL**: http://localhost:8888

### OAuth Server - Puerto 9000
Servidor de autorización OAuth2.
- **URL**: http://localhost:9000
- **Well-known**: http://localhost:9000/.well-known/oauth-authorization-server

## Observabilidad

### Grafana - Puerto 3000
Dashboard de visualización de métricas, logs y traces.
- **URL**: http://localhost:3000
- **Credenciales**: admin/admin

### Prometheus - Puerto 9090
Sistema de monitoreo y alertas basado en métricas.
- **URL**: http://localhost:9090

### Loki - Puerto 3100
Agregación y consulta de logs.

### Tempo - Puertos 4317/4318
Distributed tracing con OpenTelemetry.

## Quick Start

### Requisitos Previos
- Docker Desktop
- Java 17+
- Gradle 8.x
- (Opcional) Kubernetes + Harbor para deployment en K8s

### Opción 1: Despliegue con Docker Compose

```bash
# 1. Construir todas las imágenes
scripts\docker\build\build-all.bat

# 2. Iniciar todos los servicios
scripts\docker\compose\start-all.bat

# 3. Verificar estado
scripts\docker\compose\status.bat

# 4. Ver logs de un servicio específico
scripts\docker\compose\logs-service.bat ms-productos-v3

# 5. Detener todos los servicios
scripts\docker\compose\stop-all.bat
```

**Ver más detalles**: [README-docker.md](./README-docker.md)

### Opción 2: Despliegue en Kubernetes

```bash
# 1. Construir imagen
scripts\docker\build\individual\build-ms-productos-v3.bat

# 2. Subir a Harbor
scripts\k8s\push-to-harbor.bat ms-productos-v3

# 3. Desplegar en K8s
scripts\k8s\deploy-pod.bat ms-productos-v3

# 4. Ver estado
scripts\k8s\status.bat
```

**Ver más detalles**: [README-k8s.md](./README-k8s.md)

## Testing con Postman

Importa la colección actualizada con soporte OAuth2:
- **Archivo**: `sistema-gestion-pedidos-v2.postman_collection.json`
- **Incluye**:
  - Autenticación OAuth2 automática
  - Endpoints para v1, v2 (CQRS), y v3 (API-First)
  - Health checks
  - Variables de entorno configurables

### Obtener Access Token

1. Ejecuta el request: `OAuth2 > Get Access Token (Client Credentials)`
2. El token se guarda automáticamente en `{{access_token}}`
3. Los requests con OAuth2 lo usarán automáticamente

## Estructura del Proyecto

```
sistema-gestion-pedidos/
├── scripts/                      # Scripts de automatización
│   ├── docker/                   # Scripts Docker
│   │   ├── build/
│   │   │   ├── individual/       # Build de servicios individuales
│   │   │   ├── groups/           # Build por grupos
│   │   │   └── build-all.bat     # Build completo
│   │   └── compose/              # Gestión Docker Compose
│   │       ├── start-all.bat
│   │       ├── stop-all.bat
│   │       └── logs-service.bat
│   └── k8s/                      # Scripts Kubernetes
│       ├── push-to-harbor.bat
│       ├── deploy-pod.bat
│       ├── restart-pod.bat
│       └── build-and-deploy.bat
├── config-server/                # Configuración centralizada
├── registry-service/             # Service Discovery (Eureka)
├── gateway-service/              # API Gateway
├── oauth-server/                 # OAuth2 Authorization Server
├── ms-productos/                 # Productos v1 (CRUD tradicional)
├── ms-productos-v2/              # Productos v2 (CQRS)
├── ms-productos-v3/              # Productos v3 (API-First + K8s)
├── ms-productos-writer/          # Writer para CQRS
├── ms-pedidos/                   # Gestión de pedidos
├── k8s/                          # Manifiestos Kubernetes
│   └── ms-productos-v3/
├── observability/                # Stack de observabilidad
│   ├── grafana/
│   ├── prometheus/
│   └── loki/
├── docker-compose.yml            # Orquestación Docker
└── README.md                     # Este archivo
```

## Puertos y Accesos

| Servicio | Puerto | URL | Descripción |
|----------|--------|-----|-------------|
| Gateway | 8080 | http://localhost:8080 | API Gateway principal |
| Config Server | 8888 | http://localhost:8888 | Configuración centralizada |
| Eureka | 8099 | http://localhost:8099 | Service Registry |
| OAuth Server | 9000 | http://localhost:9000 | OAuth2 Server |
| MS-Productos (v1) | 8081 | http://localhost:8080/api/productos | CRUD tradicional |
| MS-Productos-V2 Read | 8082 | http://localhost:8080/api/v2/productos | CQRS Read |
| MS-Productos-V2 Write | 8083 | http://localhost:8080/api/v2/productos/writer | CQRS Write |
| MS-Productos-V3 | 8088 | http://localhost:8080/api/v3/productos | API-First Reactive |
| MS-Productos Writer | 8086 | - | Kafka Consumer |
| MS-Pedidos | 8084 | http://localhost:8080/api/pedidos | Gestión pedidos |
| Grafana | 3000 | http://localhost:3000 | Dashboards |
| Prometheus | 9090 | http://localhost:9090 | Métricas |
| PostgreSQL | 5432 | localhost:5432 | Base de datos |
| Kafka | 9092 | localhost:9092 | Message Broker |

## Patrones y Tecnologías

- **CQRS**: Separación Command/Query (ms-productos-v2)
- **Event-Driven**: Kafka para eventos asíncronos
- **API-First**: OpenAPI Generator (ms-productos-v3)
- **Reactive Programming**: Spring WebFlux (ms-productos-v3)
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **OAuth2**: Spring Authorization Server
- **Observability**: Grafana + Prometheus + Loki + Tempo
- **Containerization**: Docker + Docker Compose
- **Orchestration**: Kubernetes + Harbor Registry

## Desarrollo

### Agregar Nuevo Microservicio

1. Crear estructura del proyecto
2. Agregar script de build en `scripts/docker/build/individual/`
3. Agregar al grupo correspondiente en `scripts/docker/build/groups/`
4. Configurar en `docker-compose.yml`
5. (Opcional) Crear manifiesto K8s en `k8s/`

### Debugging

Ver logs en tiempo real:
```bash
# Docker
scripts\docker\compose\logs-service.bat [service-name] [lines]

# Kubernetes
scripts\k8s\logs-pod.bat [service-name] [lines]
```

## Troubleshooting

### Servicios no inician
```bash
# Verificar estado
scripts\docker\compose\status.bat

# Ver logs completos
scripts\docker\compose\logs-service.bat [service-name] 200
```

### Error de conexión a BD
- Verificar que PostgreSQL esté iniciado
- Revisar credenciales en `docker-compose.yml`
- Esperar a que el healthcheck pase

### OAuth2 tokens inválidos
- Re-ejecutar request de token en Postman
- Verificar que oauth-server esté corriendo
- Revisar configuración de client credentials

## Contribuir

1. Fork del repositorio
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## Licencia

Este proyecto es parte de un sistema de práctica educativa.
