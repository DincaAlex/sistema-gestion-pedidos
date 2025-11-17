# Docker Deployment Guide

Esta guía explica cómo ejecutar el Sistema de Gestión de Pedidos usando Docker y Docker Compose.

## Requisitos Previos

- Docker 20.10 o superior
- Docker Compose 2.0 o superior
- Al menos 4GB de RAM disponible
- 10GB de espacio en disco

## Arquitectura de Contenedores

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Docker Network                               │
│                                                                      │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐        │
│  │  postgres   │      │   config-   │      │  registry-  │        │
│  │  :5432      │◄─────┤   server    │◄─────┤  service    │        │
│  │             │      │  :8888      │      │  :8099      │        │
│  └─────────────┘      └─────────────┘      └─────────────┘        │
│         ▲                                           ▲               │
│         │                                           │               │
│  ┌──────┴───────┐                           ┌──────┴───────┐       │
│  │              │                           │              │       │
│  │ ms-productos │                           │ ms-pedidos   │       │
│  │   :8081      │◄──────────────────────────│   :8082      │       │
│  │              │                           │              │       │
│  └──────────────┘                           └──────────────┘       │
│         ▲                                           ▲               │
│         │                                           │               │
│         └───────────────┐     ┌────────────────────┘               │
│                         │     │                                    │
│                   ┌─────┴─────┴─────┐                             │
│                   │   gateway-      │                             │
│                   │   service       │                             │
│                   │   :8080         │                             │
│                   └─────────────────┘                             │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

## Servicios Incluidos

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| postgres | 5432 | Base de datos PostgreSQL |
| config-server | 8888 | Spring Cloud Config Server |
| registry-service | 8099 | Eureka Service Registry |
| ms-productos | 8081 | Microservicio de Productos |
| ms-pedidos | 8082 | Microservicio de Pedidos |
| gateway-service | 8080 | API Gateway |

## Inicio Rápido

### 1. Construir y Levantar todos los servicios

```bash
docker-compose up --build
```

Este comando:
- Construye las imágenes Docker de todos los microservicios
- Levanta todos los contenedores en el orden correcto
- Inicializa las bases de datos automáticamente

### 2. Levantar en segundo plano (detached mode)

```bash
docker-compose up -d --build
```

### 3. Ver logs de todos los servicios

```bash
docker-compose logs -f
```

### 4. Ver logs de un servicio específico

```bash
# Ver logs del gateway
docker-compose logs -f gateway-service

# Ver logs de productos
docker-compose logs -f ms-productos

# Ver logs de pedidos
docker-compose logs -f ms-pedidos
```

## Comandos Útiles

### Verificar el estado de los servicios

```bash
docker-compose ps
```

### Detener todos los servicios

```bash
docker-compose down
```

### Detener y eliminar volúmenes (limpieza completa)

```bash
docker-compose down -v
```

### Reconstruir un servicio específico

```bash
# Reconstruir solo ms-productos
docker-compose up -d --build ms-productos

# Reconstruir solo ms-pedidos
docker-compose up -d --build ms-pedidos
```

### Reiniciar un servicio

```bash
docker-compose restart gateway-service
```

### Escalar servicios (ejemplo con productos)

```bash
docker-compose up -d --scale ms-productos=3
```

## Verificación de Servicios

### Health Checks

Todos los servicios exponen endpoints de health check:

```bash
# Config Server
curl http://localhost:8888/actuator/health

# Registry Service (Eureka)
curl http://localhost:8099/actuator/health

# MS-Productos
curl http://localhost:8081/actuator/health

# MS-Pedidos
curl http://localhost:8082/actuator/health

# Gateway
curl http://localhost:8080/actuator/health
```

### Eureka Dashboard

Acceder a: http://localhost:8099

Aquí podrás ver todos los microservicios registrados.

### Swagger UI

- **MS-Productos**: http://localhost:8081/swagger-ui.html
- **MS-Pedidos**: http://localhost:8082/swagger-ui.html

## Acceso a través del Gateway

Una vez que todos los servicios estén levantados, puedes acceder a través del Gateway:

```bash
# Productos a través del Gateway
curl http://localhost:8080/api/productos

# Pedidos a través del Gateway
curl http://localhost:8080/api/pedidos
```

## Inicialización de Base de Datos

Los scripts SQL en `docker-init/` se ejecutan automáticamente al levantar PostgreSQL por primera vez:

- `01-init-productos.sql`: Crea la BD de productos y datos de ejemplo
- `02-init-pedidos.sql`: Crea la BD de pedidos y datos de ejemplo

Si necesitas reinicializar las bases de datos:

```bash
# Detener y eliminar volúmenes
docker-compose down -v

# Levantar nuevamente
docker-compose up -d
```

## Variables de Entorno

Las variables de entorno se configuran en el archivo `docker-compose.yml`:

```yaml
environment:
  DB_USERNAME: postgres
  DB_PASSWORD: admin
  SPRING_PROFILES_ACTIVE: dev
```

Para cambiar las credenciales, edita el archivo `docker-compose.yml`.

## Orden de Inicio

Docker Compose maneja automáticamente el orden de inicio usando `depends_on` y `healthcheck`:

1. **postgres** - Base de datos (primero)
2. **config-server** - Configuración centralizada
3. **registry-service** - Registro de servicios
4. **ms-productos** y **ms-pedidos** - Microservicios
5. **gateway-service** - API Gateway (último)

## Solución de Problemas

### Los servicios no inician correctamente

```bash
# Ver logs detallados
docker-compose logs -f

# Verificar estado de contenedores
docker-compose ps
```

### Error de conexión a base de datos

```bash
# Verificar que postgres esté saludable
docker-compose ps postgres

# Reiniciar postgres
docker-compose restart postgres
```

### Reconstruir desde cero

```bash
# Detener todo y limpiar
docker-compose down -v

# Eliminar imágenes antiguas
docker-compose down --rmi all

# Reconstruir todo
docker-compose up --build
```

### Liberar puertos ocupados (Windows)

```powershell
# Ver procesos usando el puerto 8080
netstat -ano | findstr :8080

# Matar proceso (reemplazar PID)
taskkill /PID <PID> /F
```

### Liberar puertos ocupados (Linux/Mac)

```bash
# Ver procesos usando el puerto 8080
lsof -i :8080

# Matar proceso
kill -9 <PID>
```

## Monitoreo de Recursos

### Ver uso de recursos

```bash
docker stats
```

### Limitar recursos de un servicio

Editar `docker-compose.yml`:

```yaml
services:
  ms-productos:
    # ... otras configuraciones
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 512M
        reservations:
          memory: 256M
```

## Backup de Base de Datos

```bash
# Backup de productos
docker exec postgres-db pg_dump -U postgres db_productos_dev > backup_productos.sql

# Backup de pedidos
docker exec postgres-db pg_dump -U postgres db_pedidos_dev > backup_pedidos.sql
```

## Restaurar Base de Datos

```bash
# Restaurar productos
docker exec -i postgres-db psql -U postgres db_productos_dev < backup_productos.sql

# Restaurar pedidos
docker exec -i postgres-db psql -U postgres db_pedidos_dev < backup_pedidos.sql
```

## Desarrollo Local vs Docker

### Ventajas de usar Docker

- Entorno consistente entre desarrolladores
- No necesitas instalar PostgreSQL, Java, Maven, Gradle localmente
- Fácil limpieza y reinicio del ambiente
- Aislamiento de servicios
- Escalabilidad fácil

### Desarrollo Local

Si prefieres desarrollo local (más rápido para cambios frecuentes):

```bash
# Solo levantar postgres con Docker
docker-compose up -d postgres

# Ejecutar servicios localmente
cd ms-productos && ./gradlew bootRun
cd ms-pedidos && ./mvnw spring-boot:run
```

## Limpieza y Mantenimiento

```bash
# Eliminar contenedores detenidos
docker container prune

# Eliminar imágenes no usadas
docker image prune -a

# Eliminar volúmenes no usados
docker volume prune

# Limpieza completa del sistema
docker system prune -a --volumes
```

## Testing en Docker

```bash
# Ejecutar tests durante el build
docker-compose build --build-arg RUN_TESTS=true

# O manualmente en un contenedor
docker exec -it ms-productos sh
./gradlew test
```

## Producción

Para producción, considera:

1. Usar imágenes base más pequeñas (`-alpine`)
2. Configurar health checks más estrictos
3. Usar secrets de Docker para credenciales
4. Configurar restart policies
5. Implementar logging centralizado
6. Usar Docker Swarm o Kubernetes para orquestación

```yaml
services:
  ms-productos:
    restart: always
    deploy:
      replicas: 3
      restart_policy:
        condition: on-failure
```

## Referencias

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)

## Contacto y Soporte

Para problemas relacionados con Docker, crear un issue en el repositorio del proyecto.
