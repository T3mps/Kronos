@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION
TITLE build.bat

:: PROJECT
SET project_dir=%~dp0

:: SRC
SET src_dir=%project_dir%\src\

:: BIN
SET bin_dir=%project_dir%\bin

:: CLASSPATH
SET classpath=%project_dir%\vendor
SET classpath=%classpath%;%project_dir%\vendor\gson-2.10.jar
SET classpath=%classpath%;%project_dir%\vendor\imgui-app-1.86.7.jar
SET classpath=%classpath%;%project_dir%\vendor\imgui-bin_dirding-1.86.7.jar
SET classpath=%classpath%;%project_dir%\vendor\imgui-lwjgl3-1.86.7.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-glfw.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-lz4.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-lz4-natives-windows.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-natives-windows.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-openal.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-openal-natives-windows.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-opengl.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-opengl-natives-windows.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-stb.jar
SET classpath=%classpath%;%project_dir%\vendor\lwjgl-stb-natives-windows.jar
SET classpath=%classpath%;%project_dir%\vendor\vtd-xml_2.13_4.jar

:: BUILD
SET build=%project_dir%\build

:: JAR
SET jar_name=Kronos.jar

IF NOT EXIST %bin_dir% MKDIR %bin_dir%

for /r %src_dir% %%f in (*.java) do (
    set java_files=!java_files! %%f;
    echo %%f
)
javac -d %bin_dir% -cp %classpath% !java_files!

@REM jar cvf %build%\%jar_name% -C %bin_dir% .

pause