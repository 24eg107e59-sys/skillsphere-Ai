@echo off
echo ==============================================
echo  Starting SkillSphere AI Frontend (React Vite)
echo ==============================================
echo Checking if port 5173 is already in use...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :5173 ^| findstr LISTENING') do (
    echo Freeing port 5173 from process %%a...
    taskkill /F /PID %%a >nul 2>&1
)
cd /d "%~dp0frontend"
npm.cmd run dev
pause
