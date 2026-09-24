@echo off
echo ====================================================================
echo   Smart Campus Student Management and Academic Information System
echo ====================================================================
echo.

where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Apache Maven was not found in your PATH.
    echo Please ensure Maven or your Java IDE (VS Code, IntelliJ, Eclipse) is installed.
    pause
    exit /b 1
)

echo Starting Spring Boot application...
echo Web Portal: http://localhost:8080
echo H2 Console: http://localhost:8080/h2-console
echo.
mvn spring-boot:run
pause
