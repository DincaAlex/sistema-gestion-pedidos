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

## Run

```bash
./gradlew bootRun
```

## Docker

```bash
docker build -t ms-productos-v3 .
docker run -p 8088:8088 ms-productos-v3
```

## Endpoints

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
