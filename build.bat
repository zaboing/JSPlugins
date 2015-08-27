@echo off

set BUKKIT_JAR=jar/bukkit.jar
set OUT_DIR=bin
set BUILD_DIR=build
set BUILD_NAME=JSPlugins.jar


if not exist %BUKKIT_JAR% (
	echo %BUKKIT_JAR% does not exist. Please provide a valid bukkit jar.
	goto :eof
)

if not exist %OUT_DIR% (
	echo Creating %OUT_DIR%...
	mkdir %OUT_DIR%
)

if not exist %BUILD_DIR% (
	echo Creating %BUILD_DIR%...
	mkdir %BUILD_DIR%
)


call :collect
call :compile

if not %errorlevel%==0 (
	echo Aborting due to errors while compiling
) else (
	echo No errors while compiling
	call :package
)

goto :eof

:collect

set dirs=
echo Looking for source files...
for /r %%i in (*.java) DO call :append %%i

goto :eof

:append

set dirs=%dirs% %1

goto :eof

:compile

echo Compiling source files...
javac -cp %BUKKIT_JAR% -d %OUT_DIR% %dirs%

goto :eof

:package

echo Packaging...
set OLD_DIR=%CD%
chdir %OUT_DIR%
jar cfM %OLD_DIR%/%BUILD_DIR%/%BUILD_NAME% ./*
chdir %OLD_DIR%
jar uf %BUILD_DIR%/%BUILD_NAME% plugin.yml

goto :eof