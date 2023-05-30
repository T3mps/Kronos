@echo off
setlocal enabledelayedexpansion

REM Check if the script is run with admin rights
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo This script needs to be run as administrator.
    pause
    exit /b
)

echo.
echo #############################################################
echo #                                                           #
echo #                     Kronos Game Engine                    #
echo #                                                           #
echo #                     Installation Script                   #
echo #                                                           #
echo #############################################################
echo.
echo This script will setup the needed environment variables for
echo Kronos Game Engine to run.
echo.
echo Press any key to continue...
pause >nul

echo Setting up environment variables...
set "KRONOS=%APPDATA%\Kronos"
setx KRONOS "%KRONOS%"
if %errorLevel% neq 0 (
    echo Failed to set up environment variables. Please ensure this script is run as administrator.
    pause
    exit /b
)   
echo Successfully set up environment variables.

echo.

echo Appending Kronos build directory to Path variable...
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v "Path" /t REG_EXPAND_SZ /d "%PATH%;%KRONOS%\resources\build" /f
if %errorLevel% neq 0 (
    echo Failed to update Path variable. Please ensure this script is run as administrator.
) else (
    echo Successfully appended Kronos build directory to Path variable.
)

echo Cleaning up...
endlocal

echo.

echo Installation complete. Press any key to exit...
pause >nul
exit /b
```