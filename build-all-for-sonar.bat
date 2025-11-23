@echo off
echo ========================================
echo Building All Projects for SonarCloud
echo ========================================

REM Build Gradle projects
echo.
echo [1/11] Building registry-service...
if exist "registry-service\gradlew.bat" (
    cd registry-service
    call gradlew.bat clean build -x test
    cd ..
) else (
    echo Skipping registry-service - gradlew.bat not found
)

echo.
echo [2/11] Building ms-productos...
if exist "ms-productos\gradlew.bat" (
    cd ms-productos
    call gradlew.bat clean build -x test
    cd ..
) else (
    echo Skipping ms-productos - gradlew.bat not found
)

echo.
echo [3/11] Building ms-productos-v2...
if exist "ms-productos-v2\gradlew.bat" (
    cd ms-productos-v2
    call gradlew.bat clean build -x test
    cd ..
) else (
    echo Skipping ms-productos-v2 - gradlew.bat not found
)

echo.
echo [4/11] Building ms-productos-v3...
if exist "ms-productos-v3\gradlew.bat" (
    cd ms-productos-v3
    call gradlew.bat clean build -x test
    cd ..
) else (
    echo Skipping ms-productos-v3 - gradlew.bat not found
)

echo.
echo [5/11] Building ms-productos-writer...
if exist "ms-productos-writer\gradlew.bat" (
    cd ms-productos-writer
    call gradlew.bat clean build -x test
    cd ..
) else (
    echo Skipping ms-productos-writer - gradlew.bat not found
)

echo.
echo [6/11] Building gateway-service...
if exist "gateway-service\gradlew.bat" (
    cd gateway-service
    call gradlew.bat clean build -x test
    cd ..
) else (
    echo Skipping gateway-service - gradlew.bat not found
)

REM Build Maven projects
echo.
echo [7/11] Building ms-config-server...
if exist "ms-config-server\pom.xml" (
    cd ms-config-server
    call mvn clean compile -DskipTests
    cd ..
) else (
    echo Skipping ms-config-server - pom.xml not found
)

echo.
echo [8/11] Building oauth-server...
if exist "oauth-server\pom.xml" (
    cd oauth-server
    call mvn clean compile -DskipTests
    cd ..
) else (
    echo Skipping oauth-server - pom.xml not found
)

echo.
echo [9/11] Building oauth-client...
if exist "oauth-client\pom.xml" (
    cd oauth-client
    call mvn clean compile -DskipTests
    cd ..
) else (
    echo Skipping oauth-client - pom.xml not found
)

echo.
echo [10/11] Building resource-server...
if exist "resource-server\pom.xml" (
    cd resource-server
    call mvn clean compile -DskipTests
    cd ..
) else (
    echo Skipping resource-server - pom.xml not found
)

echo.
echo [11/11] Building ms-pedidos...
if exist "ms-pedidos\pom.xml" (
    cd ms-pedidos
    call mvn clean compile -DskipTests
    cd ..
) else (
    echo Skipping ms-pedidos - pom.xml not found
)

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Configure sonar-project.properties with your organization and project key
echo 2. Run: sonar-scanner (if installed locally)
echo 3. Or push to GitHub to trigger the workflow
echo.
pause
