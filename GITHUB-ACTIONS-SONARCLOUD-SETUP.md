# GitHub Actions + SonarCloud Setup Guide

## 📋 Índice
1. [Configuración de SonarCloud](#configuración-de-sonarcloud)
2. [Configuración de GitHub Secrets](#configuración-de-github-secrets)
3. [Estructura de Workflows](#estructura-de-workflows)
4. [Configuración por Servicio](#configuración-por-servicio)
5. [Ejecución y Verificación](#ejecución-y-verificación)

---

## 🔧 Configuración de SonarCloud

### Paso 1: Crear Cuenta en SonarCloud

1. Ve a [SonarCloud.io](https://sonarcloud.io)
2. Haz login con tu cuenta de GitHub
3. Autoriza a SonarCloud a acceder a tus repositorios

### Paso 2: Crear Organización

1. En SonarCloud, haz clic en **"+"** → **Analyze new project**
2. Crea una nueva organización o usa una existente
3. Anota el nombre de tu organización (ejemplo: `tu-organizacion`)

### Paso 3: Importar Repositorio

1. Selecciona tu repositorio `sistema-gestion-pedidos`
2. SonarCloud detectará automáticamente que es un proyecto multi-módulo

### Paso 4: Crear Proyectos para cada Microservicio

Necesitas crear un proyecto en SonarCloud para cada microservicio:

- `sistema-gestion-pedidos_ms-productos`
- `sistema-gestion-pedidos_ms-productos-v2`
- `sistema-gestion-pedidos_ms-productos-v3`
- `sistema-gestion-pedidos_ms-productos-writer`
- `sistema-gestion-pedidos_ms-pedidos`
- `sistema-gestion-pedidos_gateway-service`

### Paso 5: Obtener Token de SonarCloud

1. Ve a **My Account** → **Security**
2. Genera un nuevo token
3. Cópialo (lo necesitarás para GitHub Secrets)

---

## 🔐 Configuración de GitHub Secrets

### Añadir SONAR_TOKEN a GitHub

1. Ve a tu repositorio en GitHub
2. **Settings** → **Secrets and variables** → **Actions**
3. Haz clic en **New repository secret**
4. Nombre: `SONAR_TOKEN`
5. Valor: Pega el token de SonarCloud
6. Haz clic en **Add secret**

**Nota:** `GITHUB_TOKEN` ya está disponible automáticamente en GitHub Actions.

---

## 📁 Estructura de Workflows

### `.github/workflows/ci.yml`

**Propósito:** Build y tests de todos los microservicios

**Se ejecuta en:**
- Push a `main` o `develop`
- Pull Requests a `main` o `develop`

**Características:**
- ✅ Build paralelo de todos los servicios
- ✅ Ejecución de tests
- ✅ Caché de Gradle para mejor performance
- ✅ Upload de artifacts (JARs)
- ✅ Docker build (solo en PRs)

### `.github/workflows/sonarcloud.yml`

**Propósito:** Análisis de calidad de código con SonarCloud

**Se ejecuta en:**
- Push a `main` o `develop`
- Pull Requests a `main` o `develop`

**Características:**
- ✅ Análisis paralelo por servicio
- ✅ Cobertura de código con JaCoCo
- ✅ Detección de code smells
- ✅ Análisis de seguridad
- ✅ Detección de duplicación de código

---

## ⚙️ Configuración por Servicio

### Actualizar Organización en Archivos

**1. Archivos `sonar-project.properties`**

En cada archivo (ms-productos-v3, ms-productos-v2, ms-pedidos):
```properties
sonar.organization=TU-ORGANIZACION  # ← Reemplazar
```

**2. Archivo `.github/workflows/sonarcloud.yml`**
```yaml
-Dsonar.organization=TU-ORGANIZACION  # ← Reemplazar (línea 57)
```

### Aplicar Configuración a Todos los Servicios

Para aplicar la configuración de Sonar y JaCoCo a otros servicios, copia estas secciones del `build.gradle` de ms-productos-v3:

```gradle
plugins {
    // ... otros plugins
    id 'jacoco'
    id 'org.sonarqube' version '4.4.1.3373'
}

// ... al final del archivo

tasks.named('test') {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacoco {
    toolVersion = "0.8.11"
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

sonar {
    properties {
        property "sonar.projectKey", "sistema-gestion-pedidos_NOMBRE-SERVICIO"
        property "sonar.organization", "tu-organizacion"
        property "sonar.host.url", "https://sonarcloud.io"
        property "sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml"
    }
}
```

---

## 🚀 Ejecución y Verificación

### Trigger Manual de Workflows

```bash
# Commit y push de cambios
git add .
git commit -m "Add GitHub Actions and SonarCloud integration"
git push origin main
```

### Verificar Ejecución en GitHub

1. Ve a tu repositorio en GitHub
2. **Actions** tab
3. Verás dos workflows:
   - ✅ **CI - Build and Test**
   - ✅ **SonarCloud Analysis**

### Ver Resultados en SonarCloud

1. Ve a [SonarCloud.io](https://sonarcloud.io)
2. Selecciona tu organización
3. Verás todos los proyectos analizados
4. Haz clic en cada uno para ver:
   - 📊 Cobertura de código
   - 🐛 Bugs detectados
   - 🔒 Vulnerabilidades de seguridad
   - 💡 Code smells
   - 📈 Historial de análisis

---

## 📊 Badges para README

Añade estos badges a tu `README.md`:

```markdown
## Quality Status

### MS Productos V3
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_ms-productos-v3&metric=alert_status)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_ms-productos-v3)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_ms-productos-v3&metric=coverage)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_ms-productos-v3)

### MS Productos V2
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_ms-productos-v2&metric=alert_status)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_ms-productos-v2)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_ms-productos-v2&metric=coverage)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_ms-productos-v2)

### MS Pedidos
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_ms-pedidos&metric=alert_status)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_ms-pedidos)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_ms-pedidos&metric=coverage)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_ms-pedidos)

### Gateway Service
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_gateway-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_gateway-service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sistema-gestion-pedidos_gateway-service&metric=coverage)](https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos_gateway-service)
```

---

## 🔍 Comandos Locales

### Ejecutar análisis local de SonarCloud

```bash
# Para ms-productos-v3
cd ms-productos-v3
./gradlew clean build jacocoTestReport sonar \
  -Dsonar.token=TU_SONAR_TOKEN

# Ver reporte de cobertura local
./gradlew jacocoTestReport
# Abre: build/reports/jacoco/test/html/index.html
```

### Ejecutar tests con cobertura

```bash
./gradlew test jacocoTestReport
```

---

## ⚠️ Troubleshooting

### Error: "Organization not found"

**Solución:** Verifica que el nombre de la organización en los archivos sea exacto (case-sensitive).

### Error: "Project not found"

**Solución:**
1. Verifica que el `sonar.projectKey` sea correcto
2. Asegúrate de haber creado el proyecto en SonarCloud

### Tests fallan en GitHub Actions

**Solución:**
- Revisa los logs en GitHub Actions
- Ejecuta tests localmente: `./gradlew test`
- Verifica dependencias en `build.gradle`

### No aparece cobertura en SonarCloud

**Solución:**
1. Verifica que JaCoCo esté configurado correctamente
2. Asegúrate de que los tests se ejecuten: `./gradlew test jacocoTestReport`
3. Verifica la ruta del reporte XML en configuración de Sonar

---

## 🎯 Próximos Pasos

1. **Quality Gates:** Configura quality gates en SonarCloud
2. **PR Decoration:** Habilita comentarios automáticos en PRs
3. **Slack/Discord Notifications:** Añade notificaciones de builds
4. **Deploy Workflows:** Añade workflows para despliegue automático
5. **Security Scanning:** Integra Snyk o Dependabot

---

## 📚 Recursos

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Gradle SonarQube Plugin](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-gradle/)

---

**Fecha de creación:** 2025-11-21
**Última actualización:** 2025-11-21
