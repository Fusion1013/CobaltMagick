@echo off

REM Build plugin
call mvn clean package

REM Restart container
docker restart "1.21.8_paper_dev"