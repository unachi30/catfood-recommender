@echo off
setlocal

echo Stopping dev servers on ports 8080 and 5173...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
  taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do (
  taskkill /F /PID %%a >nul 2>&1
)

echo Done.
if /i not "%~1"=="nopause" pause
