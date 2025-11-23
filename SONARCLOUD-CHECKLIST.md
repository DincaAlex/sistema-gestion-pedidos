# SonarCloud Setup Checklist

## ✅ Archivos Configurados

### 1. `sonar-project.properties`
- ✅ Organización: `protoboy`
- ✅ Project Key: `sistema-gestion-pedidos`
- ✅ Source directories configurados
- ✅ Test directories configurados (sin ms-productos-writer)
- ✅ Java binaries configurados

### 2. `.github/workflows/sonarcloud-unified.yml`
- ✅ Action actualizado a `sonarqube-scan-action@v5`
- ✅ Build de proyectos Gradle
- ✅ Build de proyectos Maven
- ✅ Verificación de directorios antes del scan

### 3. `.github/workflows/ci.yml`
- ✅ Separado en jobs de Gradle y Maven
- ✅ Cada tipo usa su build tool correcto

## 📋 Pasos para Completar la Configuración

### Paso 1: Verificar SonarCloud Project
1. Ve a https://sonarcloud.io
2. Login con tu cuenta de GitHub
3. Verifica que exista el proyecto: `sistema-gestion-pedidos`
   - Si no existe, créalo:
     - Click en "+" → "Analyze new project"
     - Selecciona tu repositorio
     - Organization: `protoboy`
     - Project key: `sistema-gestion-pedidos`

### Paso 2: Verificar SONAR_TOKEN en GitHub
1. En SonarCloud:
   - My Account → Security → Generate Token
   - Name: `GitHub Actions`
   - Type: `User Token` o `Project Analysis Token`
   - Copia el token

2. En GitHub:
   - Ve a tu repositorio
   - Settings → Secrets and variables → Actions
   - Verifica que existe `SONAR_TOKEN`
   - Si no existe o necesitas actualizarlo:
     - Click "New repository secret"
     - Name: `SONAR_TOKEN`
     - Value: (pega el token de SonarCloud)

### Paso 3: Verificar Configuración del Proyecto
En SonarCloud, para tu proyecto `sistema-gestion-pedidos`:
1. Administration → Analysis Method
2. Debe estar en "With GitHub Actions"
3. Desactiva "Automatic Analysis" si está activado

### Paso 4: Commit y Push
```bash
git add .
git commit -m "Configure SonarCloud unified analysis and fix CI workflows"
git push origin main
```

### Paso 5: Verificar Ejecución
1. Ve a tu repositorio en GitHub
2. Actions tab
3. Verifica que corran:
   - ✅ CI - Build and Test
   - ✅ SonarCloud Unified Analysis
4. Revisa los logs si hay errores

### Paso 6: Ver Resultados en SonarCloud
1. Ve a https://sonarcloud.io/dashboard?id=sistema-gestion-pedidos
2. Espera a que el análisis complete
3. Revisa:
   - Bugs
   - Vulnerabilities
   - Code Smells
   - Coverage (si tienes tests)
   - Duplications

## 🔧 Troubleshooting

### Error: "Project not found"
**Solución**: Verifica que el proyecto existe en SonarCloud y que el projectKey coincide exactamente.

### Error: "Unauthorized" o "401"
**Solución**:
- Regenera el SONAR_TOKEN en SonarCloud
- Actualiza el secreto en GitHub

### Error: "The folder 'xxx' does not exist"
**Solución**: Ya corregido - `ms-productos-writer` fue removido de test directories.

### Build falla antes de SonarCloud
**Solución**: Ya corregido - CI ahora separa Gradle y Maven builds.

### "This action is deprecated"
**Solución**: Ya corregido - ahora usa `sonarqube-scan-action@v5`.

### Quality Gate fails
**Causa**: Código con muchos bugs/code smells
**Solución**:
1. Revisa el dashboard de SonarCloud
2. Corrige los issues críticos
3. O ajusta el Quality Gate en SonarCloud si los estándares son muy estrictos

## 📊 Qué Esperar

### Primera Ejecución
- Puede tardar 5-10 minutos
- Construye ~11 microservicios
- Analiza ~15,000+ líneas de código
- Genera reporte completo

### Métricas Esperadas
- **Files**: ~250-300
- **Lines of Code**: ~15,000+
- **Coverage**: Depende de tus tests
- **Duplications**: ~2-5% (normal en microservicios)

### Quality Gates
Por defecto, SonarCloud verifica:
- ✅ No new bugs
- ✅ No new vulnerabilities
- ✅ No new security hotspots
- ✅ Coverage on new code > 80%
- ✅ Duplications on new code < 3%

## 🎯 Próximos Pasos (Opcional)

### 1. Agregar Badge al README
En SonarCloud:
1. Project → Information → Get project badges
2. Copia el markdown
3. Agrégalo a tu README.md

### 2. Configurar Coverage (Jacoco)
Ver instrucciones en `SONARCLOUD-SETUP.md`

### 3. Custom Quality Gates
En SonarCloud:
1. Quality Gates → Create
2. Ajusta las condiciones según tus necesidades
3. Asigna al proyecto

### 4. Pull Request Decoration
Automático con GitHub Actions - verás comentarios de SonarCloud en tus PRs.

## 📚 Documentación Adicional
- Guía completa: `SONARCLOUD-SETUP.md`
- Build script: `build-all-for-sonar.bat`
- SonarCloud Docs: https://docs.sonarcloud.io/
