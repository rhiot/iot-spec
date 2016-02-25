@echo off
set ACTIVEMQ_HOME="/opt/activemq"
set ACTIVEMQ_BASE="/opt/iot-spec/example/broker"

set PARAM=%1
:getParam
shift
if "%1"=="" goto end
set PARAM=%PARAM% %1
goto getParam
:end

%ACTIVEMQ_HOME%/bin/activemq %PARAM%