@echo off
echo ================================================
echo Stopping and Removing All Containers
echo ================================================

cd /d "%~dp0..\..\..\"
docker-compose down

if %errorlevel% neq 0 (
    echo [ERROR] Failed to remove containers
    exit /b 1
)

echo.
echo ================================================
echo All Containers Removed Successfully
echo ================================================
