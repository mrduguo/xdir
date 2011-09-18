@echo off
@REM SET JAVA_HOME=C:\software\bin\java\ibm\sdk-5

if exist "%JAVA_HOME%\bin\java.exe" goto OkJHome

echo ERROR: JAVA_HOME not found in your environment.

exit /B 1

:OkJHome

SET APP_HOME=%~dp0\..


"%JAVA_HOME%\bin\java" -classpath  "%~dp0\..\boot\org.apache.felix.framework-2.0.4.jar;%~dp0\..\boot\xdir-osgi-api-0.9.0-SNAPSHOT.jar;%~dp0\..\boot\xdir-osgi-bootstrap-0.9.0-SNAPSHOT.jar;" org.xdir.platform.osgi.bootstrap.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

