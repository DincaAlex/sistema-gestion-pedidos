# Sistema de Gestión de Pedidos

Sistema de microservicios reactivo para la gestión de pedidos y productos, desarrollado con Spring Boot WebFlux y R2DBC.

## Arquitectura del Sistema

```
┌─────────────────┐      ┌──────────────────┐      ┌─────────────────┐
│                 │      │                  │      │                 │
│  ms-productos   │      │  config-server   │      │   ms-pedidos    │
│  (Port: 8081)   │◄────►│   (Port: 8888)   │◄────►│  (Port: 8082)   │
│                 │      │                  │      │                 │
└────────┬────────┘      └──────────────────┘      └────────┬────────┘
         │                                                   │
         │                                                   │
         ▼                                                   ▼
┌─────────────────┐                               ┌─────────────────┐
│   PostgreSQL    │                               │   PostgreSQL    │
│  db_productos   │                               │   db_pedidos    │
│  (Port: 5432)   │                               │  (Port: 5432)   │
└─────────────────┘                               └─────────────────┘
```

## Tecnologías Utilizadas

- **Spring Boot 3.5.7** (ms-pedidos) / 3.2.2 (ms-productos)
- **Spring Cloud Config 2025.0.0** - Configuración centralizada
- **Spring WebFlux** - Framework reactivo
- **Spring Data R2DBC** - Acceso reactivo a base de datos
- **PostgreSQL** - Base de datos relacional
- **R2DBC PostgreSQL** - Driver reactivo para PostgreSQL
- **Lombok** - Reducción de código boilerplate
- **SpringDoc OpenAPI** - Documentación de APIs (Swagger)
- **Maven / Gradle** - Gestión de dependencias

## Requisitos Previos

- Java 17 o superior
- PostgreSQL 12 o superior
- Maven 3.6+ (para ms-pedidos)
- Gradle 8.0+ (para ms-productos)
- Git

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

## Documentación de APIs (Swagger)

Una vez que los microservicios estén corriendo, puedes acceder a la documentación interactiva:

- **ms-productos Swagger UI**: http://localhost:8081/swagger-ui.html
- **ms-productos OpenAPI JSON**: http://localhost:8081/v3/api-docs

- **ms-pedidos Swagger UI**: http://localhost:8082/swagger-ui.html
- **ms-pedidos OpenAPI JSON**: http://localhost:8082/v3/api-docs

## API Endpoints

### MS-PRODUCTOS (Puerto 8081)

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

### ms-productos
- Gestión completa de productos (CRUD)
- Manejo de stock con operaciones atómicas
- Filtros por estado (activo/inactivo)
- Consultas de productos con stock bajo
- Base de datos: PostgreSQL con R2DBC
- Build tool: Gradle

### ms-pedidos
- Gestión de pedidos con múltiples productos
- Validación de stock antes de crear pedido
- Actualización automática de stock al crear pedido
- Manejo de estados: PENDIENTE, PROCESADO, CANCELADO
- Integración con ms-productos vía WebClient
- Base de datos: PostgreSQL con R2DBC
- Build tool: Maven

### config-server
- Configuración centralizada con Spring Cloud Config
- Soporte para múltiples ambientes (dev, qa, prd)
- Repositorio Git para versionado de configuraciones

## Solución de Problemas

### El microservicio no inicia
- Verificar que PostgreSQL esté corriendo
- Verificar credenciales de base de datos
- Verificar que config-server esté corriendo primero

### Error de conexión a base de datos
```
Unable to obtain connection from database
```
Solución: Verificar que la base de datos existe y las credenciales son correctas.

### Error de stock insuficiente al crear pedido
```
Producto inactivo o stock insuficiente
```
Solución: Verificar el stock disponible del producto antes de crear el pedido.

### Config Server no responde
```
Could not locate PropertySource
```
Solución: Verificar que config-server esté corriendo en puerto 8888.

## Contacto y Soporte

Para reportar bugs o solicitar features, crear un issue en el repositorio del proyecto.

## Licencia

MIT License
