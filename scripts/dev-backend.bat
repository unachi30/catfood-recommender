@echo off
setlocal
cd /d "%~dp0..\backend"

echo Freeing port 8080 if occupied...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
  taskkill /F /PID %%a >nul 2>&1
)

call mvnw.cmd spring-boot:run
