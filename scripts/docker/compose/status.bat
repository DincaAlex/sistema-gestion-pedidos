@echo off
echo ================================================
echo Docker Compose Services Status
echo ================================================
echo.

cd /d "%~dp0..\..\..\"
docker-compose ps

echo.
echo ================================================
