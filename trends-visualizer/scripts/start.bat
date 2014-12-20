@echo off
FOR /F "tokens=1,2 delims==" %%G IN (c:\trends-visualizer\.homedirs) DO (set %%G=%%H)

SET currentFilePath=%~dp0
SET currentDirPath=%currentFilePath:~0,-1%
echo %currentDirPath%

SET elasticSearchHome=%currentDirPath%/%ELASTICSEARCH%
SET logstashHome=%currentDirPath%/%LOGSTASH%
SET tomcatHome=%currentDirPath%/%TOMCAT%

cd %elasticSearchHome%
start /min bin\elasticsearch.bat

cd %logstashHome%
SET logstashHomeWithReversedSlash=%logstashHome:\=/%
start /min bin\logstash.bat agent -f %logstashHomeWithReversedSlash%/lib/logstash/config/performance-trends-visualizer-win.conf

cd %tomcatHome%/bin
catalina.bat run
