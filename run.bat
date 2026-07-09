@echo off
title Smart Blood Donation Network Launcher
echo =============================================
echo    Smart Blood Donation Network - Launcher   
echo =============================================
echo.

:: Create bin directory if it doesn't exist
if not exist bin (
    mkdir bin
)

echo Compiling Java files...
javac -d bin src/App.java src/model/*.java src/service/*.java src/ui/*.java src/ui/components/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation Successful! Running application...
    echo.
    java -cp bin App
) else (
    echo Compilation Failed. Please review errors.
    pause
)
