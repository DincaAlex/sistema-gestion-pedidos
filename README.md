# Sistema de Gestión de Pedidos

Sistema de microservicios reactivo para la gestión de pedidos y productos, con arquitectura event-driven, OAuth2, service discovery y observabilidad completa.

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
           │   (Port: 8081)  │  │  (Port: 8086)   │  │  (Port: 8088)   │  │ (Port: 8082) │
           │   Sync CRUD     │  │  Async + Kafka  │  │  API-First+K8s  │  │              │
           └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  └──────┬───────┘
                    │                    │ Publish           │
                    │           ┌────────▼────────┐          │
                    │           │  Kafka (9092)   │          │
                    │           │  producto-events│          │
                    │           └────────┬────────┘          │
                    │                    │ Consume           │
                    │           ┌────────▼────────┐          │
                    │           │ ms-productos-   │          │
                    │           │   writer        │          │
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

## Tecnologías Utilizadas

### Core
- **Spring Boot 3.5.7** / 3.2.2
- **Spring WebFlux** - Framework reactivo
- **Spring Data R2DBC** - Acceso reactivo a base de datos
- **PostgreSQL** - Base de datos relacional
- **Lombok** - Reducción de código boilerplate

### Microservicios
- **Spring Cloud Config** - Configuración centralizada
- **Spring Cloud Gateway** - API Gateway reactivo
- **Netflix Eureka** - Service Discovery
- **Spring Security OAuth2** - Autenticación y autorización

### Event-Driven
- **Apache Kafka** - Message broker para comunicación asíncrona
- **Spring Kafka** - Integración reactiva con Kafka

### Observabilidad
- **Prometheus** - Recolección de métricas
- **Grafana** - Visualización y dashboards
- **Loki** - Agregación de logs
- **Tempo** - Trazas distribuidas (OpenTelemetry)
- **Micrometer** - Métricas y trazas

### Otros
- **SpringDoc OpenAPI** - Documentación de APIs (Swagger)
- **Maven / Gradle** - Gestión de dependencias

## Requisitos Previos

### Para ejecución con Docker (Recomendado)
- Docker 20.10+
- Docker Compose 2.0+

### Para ejecución local
- Java 17 o superior
- PostgreSQL 12 o superior
- Maven 3.6+ (para ms-pedidos)
- Gradle 8.0+ (para ms-productos)
- Apache Kafka 3.0+ (para ms-productos-v2)
- Git

## Quick Start con Docker 🚀

La forma más rápida de ejecutar todo el sistema:

**Nota**: `ms-productos-v3` se despliega con **Kubernetes + Harbor** (ver su [README](ms-productos-v3/README.md)). Los demás servicios usan Docker Compose.

```bash
# 1. Construir todas las imágenes
./build-all.sh  # Linux/Mac
# o ejecutar los .bat individuales en Windows

# 2. Iniciar todos los servicios (excepto ms-productos-v3)
docker-compose up -d

# 3. Verificar que todo esté corriendo
docker-compose ps

# 4. Obtener un token OAuth2
curl -X POST "http://localhost:8080/oauth2/token" \
  -u "oauth-client:12345678910" \
  -d "grant_type=client_credentials&scope=read write"

# 5. Crear un producto (guarda el token del paso anterior)
curl -X POST "http://localhost:8086/api/productos" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","descripcion":"Producto de prueba","precio":99.99,"stock":10}'

# 6. Listar productos
curl -H "Authorization: Bearer <token>" http://localhost:8086/api/productos
```

**Acceder a las UIs:**
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090
- Eureka: http://localhost:8099
- Kafka UI: http://localhost:8090
- Swagger ms-productos-v2: http://localhost:8086/swagger-ui.html

Ver más detalles de Docker en [README-Docker.md](README-Docker.md)

## Puertos Utilizados

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Gateway Service | 8080 | API Gateway principal (punto de entrada) |
| ms-productos | 8081 | Microservicio de productos (sync) |
| ms-pedidos | 8082 | Microservicio de pedidos |
| ms-productos-v2 | 8086 | Microservicio de productos (async + Kafka) |
| ms-productos-writer | 8087 | Consumer de eventos Kafka |
| ms-productos-v3 | 8088 | Microservicio de productos (API-First + Kubernetes) |
| Kafka UI | 8090 | Interfaz web de Kafka |
| Registry (Eureka) | 8099 | Service Discovery |
| Config Server | 8888 | Servidor de configuración |
| OAuth Server | 9000 | Authorization Server |
| Kafka | 9092 | Message broker |
| Prometheus | 9090 | Métricas |
| Grafana | 3000 | Dashboards de observabilidad |
| Loki | 3100 | Agregación de logs |
| Tempo | 4317/4318 | Trazas distribuidas (gRPC/HTTP) |
| PostgreSQL | 5432 | Base de datos |

## Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd sistema-gestion-pedidos
```

### 2. Configurar las Bases de Datos

#### Crear las bases de datos

```sql
-- Conectarse a PostgreSQL
psql -U postgres

-- Crear base de datos para productos
CREATE DATABASE db_productos_dev;

-- Crear base de datos para pedidos
CREATE DATABASE db_pedidos_dev;
```

#### Ejecutar scripts de inicialización

**Para ms-productos (db_productos_dev):**

```sql
-- Tabla productos
CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de ejemplo
INSERT INTO productos (nombre, descripcion, precio, stock, activo) VALUES
('Mouse Logitech MX Master', 'Mouse inalámbrico ergonómico', 99.99, 50, true),
('Teclado Mecánico Keychron K2', 'Switches Brown, RGB', 89.99, 35, true),
('Monitor LG 27" 4K', 'IPS, HDR10, 60Hz', 449.99, 20, true),
('Webcam Logitech C920', 'Full HD 1080p', 79.99, 5, true);
```

**Para ms-pedidos (db_pedidos_dev):**

```sql
-- Tabla pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id SERIAL PRIMARY KEY,
    cliente VARCHAR(255) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(50) DEFAULT 'PENDIENTE'
);

-- Tabla detalle_pedidos
CREATE TABLE IF NOT EXISTS detalle_pedidos (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);
```

### 3. Configurar credenciales de base de datos

Las credenciales se pueden configurar mediante variables de entorno o modificando los archivos en `config-repo/`:

#### Usando variables de entorno (recomendado):

```bash
# Windows
set DB_USERNAME=postgres
set DB_PASSWORD=admin

# Linux/Mac
export DB_USERNAME=postgres
export DB_PASSWORD=admin
```

#### O modificar directamente en config-repo:

- `config-repo/ms-productos-dev.yml`
- `config-repo/ms-pedidos-dev.yml`

```yaml
spring:
  r2dbc:
    username: tu_usuario
    password: tu_contraseña
```

### 4. Ejecutar los Microservicios

#### Orden de ejecución:

1. **Config Server** (primero)
2. **ms-productos**
3. **ms-pedidos**

#### Config Server

```bash
cd config-server
./gradlew bootRun  # Linux/Mac
gradlew.bat bootRun  # Windows
```

Verificar que esté corriendo: http://localhost:8888/actuator/health

#### ms-productos

```bash
cd ms-productos
./gradlew bootRun  # Linux/Mac
gradlew.bat bootRun  # Windows
```

Verificar que esté corriendo: http://localhost:8081/actuator/health

#### ms-pedidos

```bash
cd ms-pedidos
./mvnw spring-boot:run  # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

Verificar que esté corriendo: http://localhost:8082/actuator/health

## Nuevas Características

### 🔐 OAuth2 Authorization Server

Sistema de autenticación y autorización centralizado con Spring Authorization Server.

**Configuración:**
- **Puerto**: 9000
- **Issuer**: http://localhost:8080 (a través del Gateway)
- **Grants soportados**: Authorization Code, Client Credentials, Refresh Token

**Clientes registrados:**

1. **oauth-client** (para APIs)
   - Client ID: `oauth-client`
   - Client Secret: `12345678910`
   - Grant Types: authorization_code, refresh_token, client_credentials
   - Scopes: openid, profile, read, write

2. **oidc-client** (para aplicaciones web)
   - Client ID: `oidc-client`
   - Client Secret: `123456789`

**Usuario de prueba:**
- Username: `user`
- Password: `user`

**Obtener token (Client Credentials):**
```bash
curl -X POST "http://localhost:8080/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u "oauth-client:12345678910" \
  -d "grant_type=client_credentials&scope=read write"
```

### 🔄 ms-productos-v2 (Async + Event-Driven)

Versión asíncrona del servicio de productos que publica eventos a Kafka para procesamiento eventual.

**Puerto**: 8086
**Endpoint base**: `/api/productos`

**Características:**
- Operaciones **write** asíncronas (CREATE, UPDATE, DELETE, UPDATE_STOCK)
- Publica eventos a Kafka topic `producto-events`
- Operaciones **read** síncronas desde base de datos
- Retorna HTTP 202 (Accepted) para operaciones asíncronas

**Event Types:**
- `PRODUCTO_CREATED`
- `PRODUCTO_UPDATED`
- `PRODUCTO_DELETED`
- `STOCK_UPDATED`

**Ejemplo de uso:**
```bash
# Crear producto (asíncrono)
curl -X POST "http://localhost:8086/api/productos" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Gamer",
    "descripcion": "Alta gama",
    "precio": 1499.99,
    "stock": 10
  }'
# Respuesta: 202 Accepted (procesamiento asíncrono)
```

### 📨 ms-productos-writer (Kafka Consumer)

Consumer de eventos de Kafka que procesa las operaciones de escritura de productos.

**Características:**
- Consume del topic `producto-events`
- Consumer group: `productos-writer-group`
- Procesamiento idempotente
- Confirmación manual de offsets
- Manejo de errores con logging

**Flujo:**
1. ms-productos-v2 publica evento → Kafka
2. ms-productos-writer consume evento
3. Procesa operación en base de datos
4. Confirma offset

### 🚪 Gateway Service

API Gateway centralizado con Spring Cloud Gateway.

**Puerto**: 8080
**Características:**
- Routing a todos los microservicios
- Integración con OAuth2
- Load balancing vía Eureka
- CORS configurado

**Rutas:**
- `/api/productos/**` → ms-productos (8081)
- `/api/pedidos/**` → ms-pedidos (8082)
- `/oauth2/**` → oauth-server (9000)

### 🔍 Service Discovery (Eureka)

Registro y descubrimiento de servicios con Netflix Eureka.

**Puerto**: 8099
**Dashboard**: http://localhost:8099

**Servicios registrados:**
- ms-productos
- ms-productos-v2
- ms-pedidos
- gateway-service

### 📊 Observabilidad

Stack completo de observabilidad para monitoreo, logs y trazas distribuidas.

**Componentes:**

1. **Prometheus** (9090)
   - Recolección de métricas de todos los servicios
   - Endpoint: http://localhost:9090

2. **Grafana** (3000)
   - Dashboards de métricas, logs y trazas
   - Usuario: `admin` / Password: `admin`
   - URL: http://localhost:3000

3. **Loki** (3100)
   - Agregación de logs de todos los contenedores
   - Integrado con Promtail para recolección

4. **Tempo** (4317/4318)
   - Trazas distribuidas con OpenTelemetry
   - Soporta: OTLP (gRPC/HTTP), Jaeger, Zipkin

5. **Kafka UI** (8090)
   - Interfaz web para Kafka
   - Monitoreo de topics, consumers y mensajes
   - URL: http://localhost:8090

**Endpoints de métricas:**
- http://localhost:8081/actuator/prometheus (ms-productos)
- http://localhost:8086/actuator/prometheus (ms-productos-v2)
- http://localhost:8082/actuator/prometheus (ms-pedidos)

## Documentación de APIs (Swagger)

Una vez que los microservicios estén corriendo, puedes acceder a la documentación interactiva:

- **ms-productos Swagger UI**: http://localhost:8081/swagger-ui.html
- **ms-productos-v2 Swagger UI**: http://localhost:8086/swagger-ui.html
- **ms-pedidos Swagger UI**: http://localhost:8082/swagger-ui.html

## Testing con Postman + OAuth2

### Opción 1: Client Credentials (Recomendado para testing)

**1. Obtener Access Token:**
- URL: `http://localhost:8080/oauth2/token`
- Method: `POST`
- Authorization: Basic Auth
  - Username: `oauth-client`
  - Password: `12345678910`
- Body (x-www-form-urlencoded):
  - `grant_type`: `client_credentials`
  - `scope`: `read write`

**2. Usar el token en requests:**
- Header: `Authorization: Bearer <access_token>`

### Opción 2: Authorization Code Flow

**Configurar OAuth 2.0 en Postman:**
- Grant Type: `Authorization Code`
- Callback URL: `https://oauth.pstmn.io/v1/callback`
- Auth URL: `http://localhost:8080/oauth2/authorize`
- Access Token URL: `http://localhost:8080/oauth2/token`
- Client ID: `oauth-client`
- Client Secret: `12345678910`
- Scope: `read write openid profile`

Credenciales de login:
- Username: `user`
- Password: `user`

## API Endpoints

**⚠️ Nota:** Todos los endpoints (excepto `/actuator/health`) requieren autenticación OAuth2.

### MS-PRODUCTOS (Puerto 8081) - Síncrono

#### Listar todos los productos
```bash
GET http://localhost:8081/api/productos

Response:
[
  {
    "id": 1,
    "nombre": "Mouse Logitech MX Master",
    "descripcion": "Mouse inalámbrico ergonómico",
    "precio": 99.99,
    "stock": 50,
    "activo": true,
    "fechaCreacion": "2025-11-03T01:46:27.70884"
  }
]
```

#### Listar solo productos activos
```bash
GET http://localhost:8081/api/productos?onlyActive=true
```

#### Obtener producto por ID
```bash
GET http://localhost:8081/api/productos/{id}

Example:
GET http://localhost:8081/api/productos/1

Response:
{
  "id": 1,
  "nombre": "Mouse Logitech MX Master",
  "descripcion": "Mouse inalámbrico ergonómico",
  "precio": 99.99,
  "stock": 50,
  "activo": true,
  "fechaCreacion": "2025-11-03T01:46:27.70884"
}
```

#### Crear producto
```bash
POST http://localhost:8081/api/productos
Content-Type: application/json

Request Body:
{
  "nombre": "Laptop HP",
  "descripcion": "Laptop HP 15.6 pulgadas, Intel Core i5, 8GB RAM, 256GB SSD",
  "precio": 599.99,
  "stock": 25,
  "activo": true
}

Response: 201 Created
{
  "id": 10,
  "nombre": "Laptop HP",
  "descripcion": "Laptop HP 15.6 pulgadas, Intel Core i5, 8GB RAM, 256GB SSD",
  "precio": 599.99,
  "stock": 25,
  "activo": true,
  "fechaCreacion": "2025-11-03T01:47:16.671898"
}
```

#### Actualizar producto
```bash
PUT http://localhost:8081/api/productos/{id}
Content-Type: application/json

Request Body:
{
  "nombre": "Laptop HP (Actualizado)",
  "descripcion": "Laptop HP 15.6 pulgadas, Intel Core i7, 16GB RAM, 512GB SSD",
  "precio": 799.99,
  "stock": 30,
  "activo": true
}

Response: 200 OK
```

#### Eliminar producto
```bash
DELETE http://localhost:8081/api/productos/{id}

Response: 204 No Content
```

#### Actualizar stock
```bash
PUT http://localhost:8081/api/productos/{id}/stock?cantidad={cantidad}

Examples:
# Incrementar stock
PUT http://localhost:8081/api/productos/1/stock?cantidad=10

# Decrementar stock
PUT http://localhost:8081/api/productos/1/stock?cantidad=-5

Response: 200 OK (vacío)
```

#### Productos con stock bajo
```bash
GET http://localhost:8081/api/productos/low-stock?minimo={minimo}

Example:
GET http://localhost:8081/api/productos/low-stock?minimo=10

Response:
[
  {
    "id": 5,
    "nombre": "Webcam Logitech C920",
    "descripcion": "Full HD 1080p",
    "precio": 79.99,
    "stock": 5,
    "activo": true,
    "fechaCreacion": "2025-11-03T01:46:27.70884"
  }
]
```

### MS-PEDIDOS (Puerto 8082)

#### Listar todos los pedidos
```bash
GET http://localhost:8082/api/pedidos

Response:
[
  {
    "id": 1,
    "cliente": "Juan Perez",
    "fecha": "2025-11-03T02:10:45.988336",
    "total": 469.95,
    "estado": "PENDIENTE",
    "detalles": [
      {
        "id": 1,
        "productoId": 2,
        "cantidad": 2,
        "precioUnitario": 99.99
      },
      {
        "id": 2,
        "productoId": 3,
        "cantidad": 3,
        "precioUnitario": 89.99
      }
    ]
  }
]
```

#### Obtener pedido por ID
```bash
GET http://localhost:8082/api/pedidos/{id}

Example:
GET http://localhost:8082/api/pedidos/1

Response: 200 OK (mismo formato que listar todos)
```

#### Crear pedido
```bash
POST http://localhost:8082/api/pedidos
Content-Type: application/json

Request Body:
{
  "cliente": "Juan Perez",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 2,
      "cantidad": 1
    }
  ]
}

Response: 201 Created
{
  "id": 5,
  "cliente": "Juan Perez",
  "fecha": "2025-11-03T02:10:45.9883356",
  "total": 469.95,
  "estado": "PENDIENTE",
  "detalles": [
    {
      "id": 7,
      "productoId": 2,
      "cantidad": 2,
      "precioUnitario": 99.99
    },
    {
      "id": 8,
      "productoId": 3,
      "cantidad": 3,
      "precioUnitario": 89.99
    }
  ]
}
```

#### Actualizar estado del pedido
```bash
PUT http://localhost:8082/api/pedidos/{id}/status?estado={estado}

Estados válidos: PENDIENTE, PROCESADO, CANCELADO

Example:
PUT http://localhost:8082/api/pedidos/1/status?estado=PROCESADO

Response: 200 OK
{
  "id": 5,
  "cliente": "Juan Perez",
  "fecha": "2025-11-03T02:10:45.988336",
  "total": 469.95,
  "estado": "PROCESADO",
  "detalles": [...]
}
```

#### Eliminar pedido
```bash
DELETE http://localhost:8082/api/pedidos/{id}

Response: 204 No Content
```

## Flujo de Pruebas Completo

### 1. Consultar productos disponibles
```bash
curl -X GET http://localhost:8081/api/productos
```

### 2. Crear un pedido con múltiples productos
```bash
curl -X POST http://localhost:8082/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": "Juan Perez",
    "detalles": [
      {"productoId": 2, "cantidad": 2},
      {"productoId": 3, "cantidad": 3}
    ]
  }'
```

### 3. Verificar actualización de stock
```bash
curl -X GET http://localhost:8081/api/productos/2
curl -X GET http://localhost:8081/api/productos/3
```

### 4. Consultar productos con bajo stock
```bash
curl -X GET "http://localhost:8081/api/productos/low-stock?minimo=10"
```

### 5. Actualizar estado del pedido
```bash
curl -X PUT "http://localhost:8082/api/pedidos/5/status?estado=PROCESADO"
```

### 6. Intentar crear pedido sin stock (debe fallar)
```bash
curl -X POST http://localhost:8082/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": "Maria Lopez",
    "detalles": [
      {"productoId": 5, "cantidad": 10}
    ]
  }'

# Respuesta esperada:
# HTTP 400 Bad Request
# {"timestamp":"...","status":400,"error":"Bad Request","message":"Producto inactivo o stock insuficiente","path":"/api/pedidos"}
```

## Manejo de Errores

El sistema maneja diferentes tipos de errores:

### 400 Bad Request
- Stock insuficiente
- Producto inactivo
- Datos de entrada inválidos

```json
{
  "timestamp": "2025-11-03T02:11:42.9913828",
  "status": 400,
  "error": "Bad Request",
  "message": "Producto inactivo o stock insuficiente",
  "path": "/api/pedidos"
}
```

### 404 Not Found
- Producto no encontrado
- Pedido no encontrado

```json
{
  "timestamp": "2025-11-03T02:11:42.9913828",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/productos/999"
}
```

### 500 Internal Server Error
- Errores de comunicación entre microservicios
- Errores de base de datos

## Configuración de Entornos

El sistema soporta tres ambientes:

- **dev** (desarrollo)
- **qa** (pruebas)
- **prd** (producción)

Para cambiar de ambiente, modificar el perfil activo en `application.properties`:

```properties
spring.profiles.active=dev
```

O usar variable de entorno:
```bash
export SPRING_PROFILES_ACTIVE=qa
```

## Colección de Postman

Importar el archivo `sistema-gestion-pedidos.postman_collection.json` en Postman para acceder a todos los endpoints preconfigurados.

## Monitoreo

Los microservicios exponen endpoints de Actuator para monitoreo:

- **Health Check**: `/actuator/health`
- **Info**: `/actuator/info`

## Stack Reactivo

Este sistema está completamente construido con programación reactiva:

- **WebFlux**: Framework web reactivo basado en Project Reactor
- **R2DBC**: Driver reactivo para acceso a base de datos
- **WebClient**: Cliente HTTP reactivo para comunicación entre microservicios
- **Mono/Flux**: Tipos reactivos para manejo de 0-1 y 0-N elementos

## Características Técnicas

### ms-productos (Puerto 8081)
- Gestión completa de productos (CRUD) **síncrono**
- Manejo de stock con operaciones atómicas
- Filtros por estado (activo/inactivo)
- Consultas de productos con stock bajo
- Base de datos: PostgreSQL con R2DBC
- Build tool: Gradle
- Requiere OAuth2

### ms-productos-v2 (Puerto 8086)
- Gestión de productos **asíncrona** con Event Sourcing
- Operaciones write publicadas a Kafka
- Operaciones read síncronas desde BD
- Event types: CREATED, UPDATED, DELETED, STOCK_UPDATED
- Eventual consistency
- Base de datos: PostgreSQL con R2DBC
- Build tool: Gradle
- Requiere OAuth2

### ms-productos-writer
- Consumer de eventos de Kafka
- Procesa operaciones de escritura
- Consumer group: productos-writer-group
- Confirmación manual de offsets
- Manejo de errores y logging
- Build tool: Gradle

### ms-productos-v3 (Puerto 8088 / NodePort 30088)
- Gestión de productos con enfoque **API-First**
- Código generado desde especificación OpenAPI
- Arquitectura reactiva con WebFlux + R2DBC
- Event-Driven con Kafka
- **Único servicio desplegado en Kubernetes**
- Imágenes almacenadas en **Harbor** registry (localhost:30002)
- DTOs y validaciones generadas automáticamente
- Health probes (liveness/readiness)
- Resource limits configurados
- Build tool: Gradle
- Ver [ms-productos-v3/README.md](ms-productos-v3/README.md) para instrucciones de despliegue K8s

### ms-pedidos (Puerto 8082)
- Gestión de pedidos con múltiples productos
- Validación de stock antes de crear pedido
- Actualización automática de stock al crear pedido
- Manejo de estados: PENDIENTE, PROCESADO, CANCELADO
- Integración con ms-productos vía WebClient
- Base de datos: PostgreSQL con R2DBC
- Build tool: Maven
- Requiere OAuth2

### gateway-service (Puerto 8080)
- API Gateway reactivo con Spring Cloud Gateway
- Routing a todos los microservicios
- Integración con OAuth2 Authorization Server
- Load balancing automático vía Eureka
- CORS configurado

### oauth-server (Puerto 9000)
- Spring Authorization Server
- OAuth2 + OpenID Connect
- Grant types: Authorization Code, Client Credentials, Refresh Token
- JWT tokens firmados con RSA
- Usuarios y clientes en memoria (demo)

### registry-service (Puerto 8099)
- Netflix Eureka Server
- Service Discovery para todos los microservicios
- Dashboard web de monitoreo
- Health checks automáticos

### config-server (Puerto 8888)
- Configuración centralizada con Spring Cloud Config
- Soporte para múltiples ambientes (dev, qa, prd)
- Repositorio local para configuraciones

### Infraestructura

**Kafka (Puerto 9092)**
- Message broker para event-driven architecture
- Topic: `producto-events`
- Zookeeper para coordinación

**PostgreSQL (Puerto 5432)**
- Base de datos compartida
- Schemas: db_productos_dev, db_pedidos_dev
- Pooling de conexiones con R2DBC

**Stack de Observabilidad**
- Prometheus: Métricas
- Grafana: Dashboards
- Loki + Promtail: Logs
- Tempo: Trazas distribuidas
- Kafka UI: Monitoreo de Kafka

## Solución de Problemas

### El microservicio no inicia
- Verificar que PostgreSQL esté corriendo
- Verificar credenciales de base de datos
- Verificar que config-server y registry-service estén corriendo primero

### Error de conexión a base de datos
```
Unable to obtain connection from database
```
**Solución:** Verificar que la base de datos existe y las credenciales son correctas.

### Error 401 Unauthorized
```
HTTP/1.1 401 Unauthorized
```
**Solución:**
- Verificar que el token OAuth2 sea válido
- Obtener un nuevo token si expiró (expiran en 5 minutos)
- Verificar que el header `Authorization: Bearer <token>` esté presente

### Tempo crashea continuamente
```
failed parsing config: field http_otlp_listen_port not found
```
**Solución:** Verificar configuración en `monitoring/tempo-config.yml`. Los receivers deben estar en formato:
```yaml
otlp:
  protocols:
    http:
      endpoint: 0.0.0.0:4318
```

### ms-productos-writer no procesa eventos
**Síntomas:** Offset no aumenta, eventos no se consumen

**Soluciones:**
1. Verificar que Kafka esté corriendo: `docker logs kafka`
2. Verificar logs del writer: `docker logs ms-productos-writer`
3. Verificar consumer group:
   ```bash
   docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 \
     --group productos-writer-group --describe
   ```

### Error de stock insuficiente al crear pedido
```
Producto inactivo o stock insuficiente
```
**Solución:** Verificar el stock disponible del producto antes de crear el pedido.

### Config Server no responde
```
Could not locate PropertySource
```
**Solución:** Verificar que config-server esté corriendo en puerto 8888.

### Servicio no se registra en Eureka
**Solución:**
- Verificar que registry-service esté corriendo
- Revisar configuración de `eureka.client.serviceUrl.defaultZone`
- Esperar 30-60 segundos para el registro inicial

### Kafka connection refused
**Solución:**
- Verificar que Zookeeper esté healthy: `docker ps`
- Verificar que Kafka esté corriendo: `docker logs kafka`
- Esperar a que Kafka termine de iniciar (puede tomar 30-60 segundos)

## Contacto y Soporte

Para reportar bugs o solicitar features, crear un issue en el repositorio del proyecto.

## Licencia

MIT License
