@echo off
FOR /F "tokens=1,2 delims==" %%G IN (c:\trends-visualizer\.appdirs) DO (set %%G=%%H)

SET currentFilePath=%~dp0
SET currentDirPath=%currentFilePath:~0,-1%
echo %currentDirPath%

SET elasticSearchBin=%currentDirPath%/%ELASTICSEARCH%/bin
SET logstashHome=%currentDirPath%/%LOGSTASH%
SET tomcatBin=%currentDirPath%/%TOMCAT%/bin
SET logimporterHome=%currentDirPath%/logimporter

cd %elasticSearchBin%
start elasticsearch.bat

sh -c "%logimporterHome%/create_index.sh"

cd %logstashHome%
SET logstashHomeWithReversedSlash=%logstashHome:\=/%
start bin\logstash.bat agent -f %logstashHomeWithReversedSlash%/lib/logstash/config/trends-visualizer.conf

cd %tomcatBin%
catalina.bat run
