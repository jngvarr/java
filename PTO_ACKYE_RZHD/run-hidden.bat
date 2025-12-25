@echo off
setlocal

REM === Настройки ===
set JAR_NAME=target\PTO_ACKYE_RZHD-0.0.1-SNAPSHOT.jar
set JAVA_EXE=java
set LOG_FILE=bot.log

REM === Проверка, работает ли процесс Java с нашим JAR ===
for /f "tokens=2 delims=," %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO CSV /NH ^| findstr /I "%JAR_NAME%"') do (
    echo Найден запущенный бот (PID=%%a). Останавливаю...
    taskkill /F /PID %%a >nul 2>&1
    timeout /t 2 >nul
)

REM === Переключение консоли на UTF-8 ===
chcp 65001 >nul

REM === Запуск бота в фоне с логом ===
echo Запускаю бота в фоне...
start /min "" %JAVA_EXE% -Dfile.encoding=UTF-8 -jar %JAR_NAME% > %LOG_FILE% 2>&1

endlocal
