@echo off
echo ==============================================
echo  Starting SkillSphere AI Backend (Spring Boot)
echo ==============================================
echo Checking if port 8080 is already in use...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    echo Freeing port 8080 from process %%a...
    taskkill /F /PID %%a >nul 2>&1
)
cd /d "%~dp0backend\backend"
mvn spring-boot:run
pause
