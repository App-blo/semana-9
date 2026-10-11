@echo off
cd /d "%~dp0"
java -jar BibliotecaEscolar.jar
if errorlevel 1 (
    echo.
    echo No se pudo iniciar. Revisa que Java este instalado y que MySQL este encendido.
    pause
)
