# Stack de Observabilidad - Sistema de Gestión de Pedidos

Este proyecto incluye un stack completo de observabilidad basado en Grafana, Prometheus, Loki, Tempo y Promtail para monitorear microservicios Spring Boot.

## Componentes

### 1. Prometheus (Métricas)
- **Puerto**: 9090
- **URL**: http://localhost:9090
- **Función**: Recolecta y almacena métricas de todos los microservicios
- **Configuración**: `monitoring/prometheus.yml`

### 2. Loki (Logs)
- **Puerto**: 3100
- **URL**: http://localhost:3100
- **Función**: Agregación y almacenamiento de logs
- **Configuración**: `monitoring/loki-config.yml`

### 3. Promtail (Recolector de Logs)
- **Puerto**: 9080
- **Función**: Recolecta logs de containers Docker y los envía a Loki
- **Configuración**: `monitoring/promtail-config.yml`

### 4. Tempo (Trazas Distribuidas)
- **Puerto**: 3200 (HTTP), 4317 (OTLP gRPC), 4318 (OTLP HTTP)
- **URL**: http://localhost:3200
- **Función**: Almacena y consulta trazas distribuidas
- **Configuración**: `monitoring/tempo-config.yml`

### 5. Grafana (Visualización)
- **Puerto**: 3000
- **URL**: http://localhost:3000
- **Credenciales**: admin / admin
- **Función**: Interfaz de visualización unificada

## Características Implementadas

### Métricas (Prometheus)
- Métricas de JVM (heap, threads, GC)
- Métricas HTTP (requests, latencia, errores)
- Métricas de base de datos
- Métricas personalizadas de negocio

### Logs (Loki)
- Logs centralizados de todos los microservicios
- Correlación de logs con trazas (traceId, spanId)
- Logs estructurados en formato JSON
- Filtrado por servicio, nivel, thread

### Trazas (Tempo)
- Trazas distribuidas entre microservicios
- Correlación automática con métricas y logs
- Visualización de service graphs
- Análisis de latencias

## Uso

### Iniciar el Stack de Observabilidad

```bash
# Iniciar solo los servicios de observabilidad
docker-compose up -d prometheus loki promtail tempo grafana

# O iniciar todo el sistema
docker-compose up -d
```

### Acceder a las Interfaces

1. **Grafana**: http://localhost:3000
   - Usuario: `admin`
   - Contraseña: `admin`
   - Dashboards preconfigurads en la carpeta "Microservices"

2. **Prometheus**: http://localhost:9090
   - Explorar métricas directamente
   - Ver targets y su estado de salud

3. **Logs en Grafana**:
   - Ir a Explore → Seleccionar datasource "Loki"
   - Consultar logs por servicio: `{app="ms-productos"}`
   - Filtrar por nivel: `{app="ms-productos"} |= "ERROR"`

4. **Trazas en Grafana**:
   - Ir a Explore → Seleccionar datasource "Tempo"
   - Buscar por traceId o explorar service graph

## Endpoints de Actuator

Todos los microservicios exponen los siguientes endpoints:

- `/actuator/health` - Estado de salud del servicio
- `/actuator/info` - Información del servicio
- `/actuator/metrics` - Métricas en formato JSON
- `/actuator/prometheus` - Métricas en formato Prometheus

Ejemplo: http://localhost:8081/actuator/prometheus (ms-productos)

## Consultas Útiles

### PromQL (Prometheus)

```promql
# Tasa de requests HTTP por servicio
sum by (application) (rate(http_server_requests_seconds_count[5m]))

# Latencia p95 de requests HTTP
histogram_quantile(0.95, sum by (application, le) (rate(http_server_requests_seconds_bucket[5m])))

# Uso de memoria heap JVM
sum by (application) (jvm_memory_used_bytes{area="heap"})

# Threads JVM
sum by (application) (jvm_threads_live_threads)
```

### LogQL (Loki)

```logql
# Todos los logs de un servicio
{app="ms-productos"}

# Logs de error
{app="ms-productos"} |= "ERROR"

# Logs con un traceId específico
{app="ms-productos"} |= "traceId" |= "abc123"

# Tasa de errores
rate({app="ms-productos"} |= "ERROR" [5m])
```

### TraceQL (Tempo)

```traceql
# Buscar trazas de un servicio
{ .service.name = "ms-productos" }

# Trazas con duración mayor a 1s
{ duration > 1s }

# Trazas con errores
{ status = error }
```

## Configuración de Microservicios

Cada microservicio está configurado con:

1. **Dependencias**:
   - `micrometer-registry-prometheus` - Exportar métricas a Prometheus
   - `micrometer-tracing-bridge-otel` - Soporte de trazas OpenTelemetry
   - `opentelemetry-exporter-otlp` - Exportar trazas a Tempo
   - `loki-logback-appender` - Enviar logs a Loki

2. **Configuración en application.properties/yml**:
   ```properties
   # Actuator endpoints
   management.endpoints.web.exposure.include=health,info,metrics,prometheus
   management.endpoint.prometheus.enabled=true

   # Tracing
   management.tracing.sampling.probability=1.0
   management.otlp.tracing.endpoint=http://tempo:4318/v1/traces

   # Logging con traceId y spanId
   logging.pattern.level=%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
   ```

3. **logback-spring.xml**: Configurado para enviar logs a Loki con traceId y spanId

## Troubleshooting

### Los servicios no aparecen en Prometheus
- Verificar que el servicio está corriendo: `docker-compose ps`
- Verificar el endpoint de métricas: `curl http://localhost:8081/actuator/prometheus`
- Revisar la configuración en `monitoring/prometheus.yml`

### Error HTTP 401 en servicios con Spring Security
Si Prometheus muestra error 401 o "unsupported Content-Type" para oauth-server, oauth-client o resource-server, asegúrate de que sus `SecurityConfig` permita acceso público a Actuator:

```java
.requestMatchers("/actuator/health", "/actuator/prometheus", "/actuator/info").permitAll()
```

### No se ven logs en Loki
- Verificar que Promtail está corriendo: `docker-compose logs promtail`
- Verificar que los servicios están enviando logs
- Revisar la configuración de logback-spring.xml

### No se ven trazas en Tempo
- Verificar que el endpoint OTLP está accesible desde los servicios
- Verificar la configuración de tracing en application.properties
- Hacer requests a los servicios para generar trazas

## Archivos de Configuración

- `monitoring/prometheus.yml` - Configuración de Prometheus
- `monitoring/loki-config.yml` - Configuración de Loki
- `monitoring/promtail-config.yml` - Configuración de Promtail
- `monitoring/tempo-config.yml` - Configuración de Tempo
- `monitoring/grafana-datasources.yml` - Datasources de Grafana
- `monitoring/logback-spring.xml` - Template de configuración de Logback
- `monitoring/observability-config.yml` - Template de configuración de observabilidad

## Dashboards

Los dashboards están ubicados en `monitoring/grafana-dashboards/`:
- `spring-boot-dashboard.json` - Dashboard general de Spring Boot con métricas de JVM y HTTP

Puedes importar dashboards adicionales desde [Grafana Dashboard Repository](https://grafana.com/grafana/dashboards/).

## Producción

Para producción, considera:

1. **Reducir el sampling de trazas**: Cambiar `management.tracing.sampling.probability=0.1` (10%)
2. **Configurar retención de datos**:
   - Prometheus: Agregar `--storage.tsdb.retention.time=15d`
   - Loki: Configurar retention en `loki-config.yml`
   - Tempo: Configurar `block_retention` en `tempo-config.yml`
3. **Asegurar las interfaces**: Agregar autenticación y HTTPS
4. **Configurar alertas**: Usar Alertmanager con Prometheus
5. **Escalar según necesidad**: Usar replicas de Loki, Tempo para alta disponibilidad
