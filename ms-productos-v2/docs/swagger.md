# Productos API v2 - OpenAPI 3.0.3

## Definición OpenAPI (YAML)

```yaml
openapi: 3.0.3
info:
  title: Productos API v2 (Async Event-Driven)
  version: 2.0.0
  description: API REST reactiva para gestión de productos con eventos Kafka
  contact:
    name: API Support
    email: support@example.com
  license:
    name: MIT License
    url: https://opensource.org/licenses/MIT

servers:
  - url: http://localhost:8086/api/v2
    description: Development
  - url: http://gateway-service:8080/api/v2/productos
    description: Gateway

security:
  - BearerAuth: []

paths:
  /productos:
    get:
      tags:
        - Productos
      summary: Lista todos los productos
      operationId: listProductos
      parameters:
        - $ref: '#/components/parameters/OnlyActiveQueryParam'
      responses:
        '200':
          description: Lista de productos obtenida exitosamente
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SuccessListResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '500':
          $ref: '#/components/responses/InternalServerError'

    post:
      tags:
        - Productos
      summary: Crea un nuevo producto (async)
      operationId: createProducto
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductoCreateRequest'
            examples:
              BasicProduct:
                summary: Producto básico
                value:
                  nombre: Mouse Wireless
                  descripcion: Mouse inalámbrico ergonómico
                  precio: 25.99
                  stock: 150
                  activo: true
      responses:
        '202':
          $ref: '#/components/responses/AsyncAccepted'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '500':
          $ref: '#/components/responses/InternalServerError'

  /productos/{id}:
    get:
      tags:
        - Productos
      summary: Obtiene un producto por ID
      operationId: getProductoById
      parameters:
        - $ref: '#/components/parameters/ProductoIdPathParam'
      responses:
        '200':
          description: Producto encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SuccessObjectResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'
        '500':
          $ref: '#/components/responses/InternalServerError'

    put:
      tags:
        - Productos
      summary: Actualiza un producto existente (async)
      operationId: updateProducto
      parameters:
        - $ref: '#/components/parameters/ProductoIdPathParam'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductoUpdateRequest'
      responses:
        '202':
          $ref: '#/components/responses/AsyncAccepted'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'
        '500':
          $ref: '#/components/responses/InternalServerError'

    delete:
      tags:
        - Productos
      summary: Elimina un producto (async)
      operationId: deleteProducto
      parameters:
        - $ref: '#/components/parameters/ProductoIdPathParam'
      responses:
        '202':
          description: Producto marcado para eliminación
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                    default: true
                  message:
                    type: string
                    example: Producto marcado para eliminación
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'
        '500':
          $ref: '#/components/responses/InternalServerError'

  /productos/{id}/stock:
    put:
      tags:
        - Productos
      summary: Actualiza el stock de un producto (async)
      operationId: updateStock
      parameters:
        - $ref: '#/components/parameters/ProductoIdPathParam'
        - $ref: '#/components/parameters/CantidadStockQueryParam'
      responses:
        '202':
          description: Stock actualizado
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                    default: true
                  message:
                    type: string
                    example: Stock actualizado correctamente
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'
        '500':
          $ref: '#/components/responses/InternalServerError'

  /productos/low-stock:
    get:
      tags:
        - Productos
      summary: Obtiene productos con stock bajo
      operationId: getLowStockProductos
      parameters:
        - $ref: '#/components/parameters/MinimoStockQueryParam'
      responses:
        '200':
          description: Lista de productos con stock bajo
          content:
            application/json:
              schema:
                type: object
                properties:
                  success:
                    type: boolean
                    default: true
                  message:
                    type: string
                    example: Productos con stock bajo obtenidos
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/Producto'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '500':
          $ref: '#/components/responses/InternalServerError'

components:

  schemas:

    Producto:
      type: object
      required: [id, nombre, precio, stock, activo]
      properties:
        id:
          type: integer
          format: int64
          example: 1
        nombre:
          type: string
          maxLength: 255
          example: Mouse Logitech MX Master
        descripcion:
          type: string
          example: Mouse inalámbrico ergonómico
        precio:
          type: number
          format: double
          minimum: 0.01
          example: 99.99
        stock:
          type: integer
          format: int32
          minimum: 0
          example: 50
        activo:
          type: boolean
          default: true
          example: true
        fechaCreacion:
          type: string
          format: date-time
          example: "2024-11-18T10:30:00"

    ProductoCreateRequest:
      type: object
      required: [nombre, precio, stock]
      properties:
        nombre:
          type: string
          maxLength: 255
          example: Nuevo Producto
        descripcion:
          type: string
          example: Descripción del producto
        precio:
          type: number
          format: double
          minimum: 0.01
          example: 49.99
        stock:
          type: integer
          format: int32
          minimum: 0
          example: 100
        activo:
          type: boolean
          default: true
          example: true

    ProductoUpdateRequest:
      type: object
      required: [nombre, precio, stock]
      properties:
        nombre:
          type: string
          maxLength: 255
          example: Producto Actualizado
        descripcion:
          type: string
          example: Nueva descripción
        precio:
          type: number
          format: double
          minimum: 0.01
          example: 59.99
        stock:
          type: integer
          format: int32
          minimum: 0
          example: 75
        activo:
          type: boolean
          example: true

    PaginationMeta:
      type: object
      properties:
        total:
          type: integer
          example: 100
        page:
          type: integer
          example: 1
        limit:
          type: integer
          example: 20

    SuccessBase:
      type: object
      properties:
        success:
          type: boolean
          default: true
        message:
          type: string
          example: Operación completada satisfactoriamente

    SuccessObjectResponse:
      allOf:
        - $ref: '#/components/schemas/SuccessBase'
        - type: object
          properties:
            data:
              $ref: '#/components/schemas/Producto'

    SuccessListResponse:
      allOf:
        - $ref: '#/components/schemas/SuccessBase'
        - type: object
          properties:
            data:
              type: array
              items:
                $ref: '#/components/schemas/Producto'
            meta:
              $ref: '#/components/schemas/PaginationMeta'

    ErrorDetail:
      type: object
      properties:
        code:
          type: string
          example: E400_VALIDATION_FIELD
        message:
          type: string
          example: El valor no puede ser nulo
        field:
          type: string
          example: nombre
      required: [code, message]

    ErrorModel:
      type: object
      properties:
        timestamp:
          type: string
          format: date-time
          example: "2024-11-18T10:30:00"
        status:
          type: integer
          example: 400
        error:
          type: string
          example: Solicitud Incorrecta
        message:
          type: string
          example: Error de validación
        path:
          type: string
          example: /api/v2/productos
        errors:
          type: array
          items:
            $ref: '#/components/schemas/ErrorDetail'
      required: [timestamp, status, error, message, path]

  parameters:
    ProductoIdPathParam:
      name: id
      in: path
      required: true
      schema:
        type: integer
        format: int64
      description: ID del producto
      example: 1

    OnlyActiveQueryParam:
      name: onlyActive
      in: query
      required: false
      schema:
        type: boolean
        default: false
      description: Filtrar solo productos activos
      example: true

    MinimoStockQueryParam:
      name: minimo
      in: query
      required: false
      schema:
        type: integer
        format: int32
        default: 10
      description: Umbral mínimo de stock
      example: 20

    CantidadStockQueryParam:
      name: cantidad
      in: query
      required: true
      schema:
        type: integer
        format: int32
      description: Cantidad a incrementar (positivo) o decrementar (negativo)
      example: -10

  responses:

    AsyncAccepted:
      description: Operación aceptada, se procesará de forma asíncrona
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/SuccessObjectResponse'
          examples:
            ProductoCreated:
              summary: Producto creado
              value:
                success: true
                message: Producto creado y enviado para procesamiento
                data:
                  nombre: Mouse Wireless
                  descripcion: Mouse inalámbrico ergonómico
                  precio: 25.99
                  stock: 150
                  activo: true

    BadRequest:
      description: Solicitud inválida debido a errores de validación
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorModel'
          examples:
            MissingRequiredField:
              summary: Campo obligatorio omitido
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 400
                error: Solicitud Incorrecta
                message: El nombre del producto es obligatorio
                path: /api/v2/productos
                errors:
                  - code: E400_REQUIRED_FIELD
                    message: El nombre del producto es obligatorio
                    field: nombre
            InvalidPrice:
              summary: Precio inválido
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 400
                error: Solicitud Incorrecta
                message: El precio debe ser mayor a 0
                path: /api/v2/productos
                errors:
                  - code: E400_INVALID_PRICE
                    message: El precio debe ser mayor a 0
                    field: precio
            NegativeStock:
              summary: Stock negativo
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 400
                error: Solicitud Incorrecta
                message: El stock no puede ser negativo
                path: /api/v2/productos
                errors:
                  - code: E400_NEGATIVE_STOCK
                    message: El stock no puede ser negativo
                    field: stock
            InsufficientStock:
              summary: Stock insuficiente para operación
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 400
                error: Solicitud Incorrecta
                message: Stock insuficiente. Actual 30, solicitado 100
                path: /api/v2/productos/5/stock
                errors:
                  - code: E400_INSUFFICIENT_STOCK
                    message: Stock insuficiente. Actual 30, solicitado 100
                    field: stock
            ZeroQuantity:
              summary: Cantidad cero en actualización de stock
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 400
                error: Solicitud Incorrecta
                message: La cantidad no puede ser cero
                path: /api/v2/productos/5/stock
                errors:
                  - code: E400_ZERO_QUANTITY
                    message: La cantidad no puede ser cero
                    field: cantidad

    Unauthorized:
      description: Acceso no autorizado o credenciales inválidas
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorModel'
          examples:
            TokenMissing:
              summary: Token no proporcionado
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 401
                error: No Autorizado
                message: No se proporcionó el token de autenticación
                path: /api/v2/productos
                errors:
                  - code: E401_MISSING_TOKEN
                    message: Se requiere un token Bearer válido
                    field: Authorization
            InvalidToken:
              summary: Token inválido o expirado
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 401
                error: No Autorizado
                message: Token JWT inválido o expirado
                path: /api/v2/productos
                errors:
                  - code: E401_INVALID_TOKEN
                    message: El token proporcionado no es válido
                    field: Authorization

    NotFound:
      description: El recurso solicitado no fue encontrado
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorModel'
          examples:
            ProductoNotFound:
              summary: Producto no encontrado
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 404
                error: No Encontrado
                message: Producto no encontrado con id 123
                path: /api/v2/productos/123
                errors:
                  - code: E404_NOT_FOUND
                    message: No existe un producto con el identificador proporcionado
                    field: id

    InternalServerError:
      description: Error interno e inesperado del servidor
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorModel'
          examples:
            DatabaseError:
              summary: Error de base de datos
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 500
                error: Error Interno del Servidor
                message: Error al obtener productos de la base de datos
                path: /api/v2/productos
                errors:
                  - code: E500_DB_ERROR
                    message: No se pudo establecer conexión con la base de datos
                    field: null
            KafkaPublishError:
              summary: Error al publicar evento en Kafka
              value:
                timestamp: "2024-11-18T10:30:00"
                status: 500
                error: Error Interno del Servidor
                message: Error al publicar evento en Kafka
                path: /api/v2/productos
                errors:
                  - code: E500_KAFKA_ERROR
                    message: No se pudo enviar el evento al topic producto-events
                    field: null

  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: Autenticación OAuth2 con token JWT
```

---

## Kafka Events Reference

**Topic:** `producto-events`
**Serialization:** JSON

### Event Types

**Base Event Structure:**
```json
{
  "eventId": "uuid",
  "eventType": "PRODUCTO_CREATED|PRODUCTO_UPDATED|PRODUCTO_DELETED|STOCK_UPDATED",
  "timestamp": "2024-11-18T10:30:00",
  "correlationId": "uuid",
  "version": "1.0",
  "metadata": {
    "userId": null,
    "source": "ms-productos-v2"
  }
}
```

**Event Payloads:**

- **PRODUCTO_CREATED**: `{ payload: ProductoDTO }`
- **PRODUCTO_UPDATED**: `{ productoId: Long, payload: ProductoDTO }`
- **PRODUCTO_DELETED**: `{ productoId: Long }`
- **STOCK_UPDATED**: `{ productoId: Long, cantidad: Integer, stockAnterior: Integer, stockNuevo: Integer }`

---

## Notas de Implementación

**Patrón Asíncrono:**
- Operaciones de escritura (POST, PUT, DELETE) retornan `202 Accepted`
- Eventos publicados a Kafka antes de responder
- Persistencia manejada por `ms-productos-writer` (consumer)

**Seguridad:**
- OAuth2 Resource Server con JWT
- Todos los endpoints requieren token válido

**Base de Datos:**
- PostgreSQL con R2DBC (reactive)
- Stored procedure: `productos_bajo_stock(minimo: Integer)`

**Observabilidad:**
- OpenTelemetry tracing
- Prometheus metrics en `/actuator/prometheus`
- Health check en `/actuator/health`
