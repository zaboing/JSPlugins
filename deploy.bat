@echo off

set BUILD_DIR=build
set BUILD_NAME=JSPlugins.jar
set PLUGIN_DIR=..\..\Server\minecraft\Spigot 1.8\plugins

if not exist "%PLUGIN_DIR%" (
	echo Directory %PLUGIN_DIR% not found. Aborting.
	goto :eof
)

copy "%BUILD_DIR%\%BUILD_NAME%" "%PLUGIN_DIR%\%BUILD_NAME%" /Y