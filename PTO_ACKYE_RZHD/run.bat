@echo off
REM Устанавливаем кодировку UTF-8 для консоли
chcp 65001 > nul

REM Запуск Spring Boot бота
java -Dfile.encoding=UTF-8 -jar target\PTO_ACKYE_RZHD-0.0.1-SNAPSHOT.jar

pause
