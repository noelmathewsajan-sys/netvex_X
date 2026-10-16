@echo off
title NETVEX - Internet Billing Management System
echo Launching NETVEX - Internet Billing Management System...
java -cp "dist\InternetBillingManagementSystem.jar;lib\mysql-connector-j-8.3.0.jar;lib\mssql-jdbc-12.6.1.jre11.jar;lib\h2-2.2.224.jar" internetbilling.Main
if errorlevel 1 (
    echo.
    echo Running from build classes...
    java -cp "build\classes;lib\mysql-connector-j-8.3.0.jar;lib\mssql-jdbc-12.6.1.jre11.jar;lib\h2-2.2.224.jar" internetbilling.Main
)
pause
