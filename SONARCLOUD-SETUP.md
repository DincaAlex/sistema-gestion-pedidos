# Configuración de SonarCloud para Repositorio Multi-Módulo

Este repositorio contiene múltiples microservicios con diferentes sistemas de build (Maven y Gradle). Esta guía explica cómo configurar SonarCloud para analizar todo el proyecto.

## Estructura del Proyecto

### Proyectos Maven
- `ms-config-server/`
- `oauth-server/`
- `oauth-client/`
- `resource-server/`
- `ms-pedidos/`

### Proyectos Gradle
- `registry-service/`
- `ms-productos/`
- `ms-productos-v2/`
- `ms-productos-v3/`
- `ms-productos-writer/`
- `gateway-service/`

## Opciones de Configuración

### Opción 1: Análisis Unificado (Recomendado)

Un solo proyecto en SonarCloud que analiza todos los módulos.

#### Ventajas:
- Vista unificada de la calidad del código
- Más fácil de gestionar
- Métricas globales del proyecto
- Un solo Quality Gate

#### Configuración:

1. **Crear proyecto en SonarCloud**:
   - Ve a https://sonarcloud.io
   - Conecta tu cuenta de GitHub
   - Click en "+" → "Analyze new project"
   - Selecciona tu repositorio
   - Anota tu `Organization Key` y `Project Key`

2. **Actualizar `sonar-project.properties`**:
   ```properties
   sonar.organization=YOUR_SONARCLOUD_ORG
   sonar.projectKey=YOUR_PROJECT_KEY
   ```

3. **Configurar secreto en GitHub**:
   - Ve a tu repositorio en GitHub
   - Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Name: `SONAR_TOKEN`
   - Value: (obtén el token desde SonarCloud: My Account → Security → Generate Token)

4. **Usar el workflow unificado**:
   - El archivo `.github/workflows/sonarcloud-unified.yml` está listo
   - Construye todos los módulos y ejecuta un análisis global

### Opción 2: Análisis por Microservicio

Proyectos separados en SonarCloud para cada microservicio.

#### Ventajas:
- Análisis independiente por servicio
- Quality Gates personalizados por servicio
- Métricas específicas por microservicio

#### Configuración:

1. **Crear un proyecto en SonarCloud para cada servicio**
2. **Usar el workflow existente**: `.github/workflows/sonarcloud.yml`
3. **Actualizar los `sonar_key` en el workflow** con tus claves reales

## Workflows Disponibles

### `sonarcloud-unified.yml` (Recomendado)
- Análisis completo del repositorio
- Construye todos los módulos (Maven + Gradle)
- Un solo proyecto en SonarCloud
- Se ejecuta en push/PR a main y develop

### `sonarcloud.yml` (Actual)
- Análisis por servicio individual
- Matrix strategy para múltiples servicios
- Requiere múltiples proyectos en SonarCloud

## Configuración Adicional

### Agregar Reportes de Cobertura (Opcional)

#### Para proyectos Gradle:

1. Agregar JaCoCo en `build.gradle`:
```gradle
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.11"
}

test {
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}
```

2. Descomentar en `sonar-project.properties`:
```properties
sonar.coverage.jacoco.xmlReportPaths=\
  registry-service/build/reports/jacoco/test/jacocoTestReport.xml,\
  ms-productos/build/reports/jacoco/test/jacocoTestReport.xml,\
  ...
```

#### Para proyectos Maven:

1. Agregar JaCoCo en `pom.xml`:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Ejecución Local

### Con sonar-scanner (Opción 1):

1. Instalar sonar-scanner:
   - https://docs.sonarcloud.io/advanced-setup/ci-based-analysis/sonarscanner-cli/

2. Ejecutar:
   ```bash
   # Construir todos los proyectos primero
   ./build-all-projects.sh

   # Ejecutar análisis
   sonar-scanner
   ```

### Con Gradle (Opción 2):

Para cada proyecto Gradle individualmente:
```bash
cd ms-productos
./gradlew build jacocoTestReport sonar \
  -Dsonar.projectKey=YOUR_KEY \
  -Dsonar.organization=YOUR_ORG \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=YOUR_TOKEN
```

### Con Maven (Opción 3):

Para cada proyecto Maven individualmente:
```bash
cd ms-pedidos
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=YOUR_KEY \
  -Dsonar.organization=YOUR_ORG \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=YOUR_TOKEN
```

## Script Helper para Build Completo

Crear `build-all-for-sonar.sh`:

```bash
#!/bin/bash
set -e

echo "Building all Gradle projects..."
for dir in registry-service ms-productos ms-productos-v2 ms-productos-v3 ms-productos-writer gateway-service; do
  if [ -d "$dir" ] && [ -f "$dir/gradlew" ]; then
    echo "Building $dir..."
    cd "$dir"
    chmod +x gradlew
    ./gradlew clean build jacocoTestReport -x test
    cd ..
  fi
done

echo "Building all Maven projects..."
for dir in ms-config-server oauth-server oauth-client resource-server ms-pedidos; do
  if [ -d "$dir" ] && [ -f "$dir/pom.xml" ]; then
    echo "Building $dir..."
    cd "$dir"
    mvn clean compile -DskipTests
    cd ..
  fi
done

echo "All projects built successfully!"
echo "Now run: sonar-scanner"
```

## Verificación

Después de configurar:

1. Haz un commit y push a la rama `main` o `develop`
2. Ve a la pestaña "Actions" en GitHub
3. Verifica que el workflow se ejecute correctamente
4. Revisa los resultados en https://sonarcloud.io

## Troubleshooting

### Error: "Project not found"
- Verifica que `sonar.projectKey` coincida con el proyecto en SonarCloud

### Error: "Unauthorized"
- Verifica que el `SONAR_TOKEN` esté configurado correctamente en GitHub Secrets

### Error: "No sources found"
- Verifica que los proyectos estén construidos antes del análisis
- Revisa las rutas en `sonar-project.properties`

### Análisis se salta archivos
- Revisa las exclusiones en `sonar-project.properties`
- Verifica que `sonar.java.binaries` apunte a las clases compiladas

## Recursos

- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [GitHub Actions for SonarCloud](https://github.com/SonarSource/sonarcloud-github-action)
- [Multi-module projects](https://docs.sonarcloud.io/advanced-setup/analysis-scope/)
