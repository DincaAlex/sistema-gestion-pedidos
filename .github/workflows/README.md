# GitHub Actions Workflows

Este directorio contiene los workflows de CI/CD para el proyecto sistema-gestion-pedidos.

## 📁 Workflows Disponibles

### 1. `ci.yml` - Continuous Integration

**Trigger:**
- Push a `main` o `develop`
- Pull Requests a `main` o `develop`

**Jobs:**
- **build-microservices**: Build y tests de todos los microservicios en paralelo
  - ms-productos, ms-productos-v2, ms-productos-v3
  - ms-productos-writer, ms-pedidos
  - gateway-service, registry-service
  - ms-config-server, oauth-server, oauth-client

- **docker-build**: Build de imágenes Docker (solo en PRs)

**Características:**
- ✅ Caché de Gradle
- ✅ Tests automáticos
- ✅ Upload de artifacts (JARs)
- ✅ Matrix strategy para builds paralelos

### 2. `sonarcloud.yml` - Code Quality Analysis

**Trigger:**
- Push a `main` o `develop`
- Pull Requests a `main` o `develop`

**Jobs:**
- **sonarcloud-analysis**: Análisis de calidad por servicio
  - Cobertura de código con JaCoCo
  - Detección de bugs y vulnerabilidades
  - Code smells y duplicación
  - Métricas de complejidad

- **sonarcloud-quality-gate**: Verificación de quality gate

**Características:**
- ✅ Análisis paralelo por microservicio
- ✅ Caché de SonarCloud y Gradle
- ✅ Reportes de cobertura XML
- ✅ Exclusión de código generado

## 🚀 Uso

Los workflows se ejecutan automáticamente en cada push/PR. Para ejecutar manualmente:

1. Ve a **Actions** en GitHub
2. Selecciona el workflow
3. Click en **Run workflow**

## 📊 Badges

Puedes usar estos badges en tu README principal:

```markdown
[![CI](https://github.com/tu-usuario/sistema-gestion-pedidos/actions/workflows/ci.yml/badge.svg)](https://github.com/tu-usuario/sistema-gestion-pedidos/actions/workflows/ci.yml)
[![SonarCloud](https://github.com/tu-usuario/sistema-gestion-pedidos/actions/workflows/sonarcloud.yml/badge.svg)](https://github.com/tu-usuario/sistema-gestion-pedidos/actions/workflows/sonarcloud.yml)
```

## 🔧 Configuración Necesaria

### Secrets Requeridos

- `SONAR_TOKEN`: Token de SonarCloud (configurar en Settings → Secrets)
- `GITHUB_TOKEN`: Disponible automáticamente

### Archivos de Configuración

Cada microservicio debe tener:
- `sonar-project.properties`
- Plugin de Sonar y JaCoCo en `build.gradle`

Ver `GITHUB-ACTIONS-SONARCLOUD-SETUP.md` para guía completa.

## 📈 Monitoreo

- **GitHub Actions**: Ver logs y resultados en la pestaña Actions
- **SonarCloud**: [https://sonarcloud.io](https://sonarcloud.io)

---

Para más información, consulta `GITHUB-ACTIONS-SONARCLOUD-SETUP.md` en la raíz del proyecto.
