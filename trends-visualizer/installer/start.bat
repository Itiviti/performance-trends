cd elasticsearch-1.4.1\
start /min bin\elasticsearch.bat

cd ..\logstash-1.4.2\
start /min bin\logstash.bat agent -f c:/performance-trends-visualizer/logstash-1.4.2/lib/logstash/config/performance-trends-visualizer-win.conf

cd ..\apache-tomcat-8.0.15\
bin\startup.bat