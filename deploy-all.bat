@echo off
setlocal
cd /d "%~dp0"

echo 開發模式：同時啟動前端 + 後端
start "catfood-backend" cmd /k "cd /d "%~dp0" && call scripts\dev-backend.bat"
timeout /t 3 /nobreak >nul
start "catfood-frontend" cmd /k "cd /d "%~dp0frontend" && npm run dev"
echo.
echo 後端: http://localhost:8080
echo 前端: http://localhost:5173
echo Swagger: http://localhost:8080/swagger-ui.html
