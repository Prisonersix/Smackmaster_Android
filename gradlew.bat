@ECHO OFF
SET DIR=%~dp0
SET APP_BASE_NAME=%~n0
SET APP_HOME=%DIR%

IF NOT DEFINED JAVA_HOME GOTO findJavaFromPath
SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
IF EXIST "%JAVA_EXE%" GOTO init
ECHO ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
GOTO end

:findJavaFromPath
SET JAVA_EXE=java.exe
WHERE java >NUL 2>NUL
IF %ERRORLEVEL% NEQ 0 (
  ECHO ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
  GOTO end
)

:init
SET CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar;%APP_HOME%\gradle\wrapper\gradle-wrapper-shared.jar
"%JAVA_EXE%" -Dorg.gradle.appname=%APP_BASE_NAME% -classpath %CLASSPATH% org.gradle.wrapper.GradleWrapperMain %*

:end
