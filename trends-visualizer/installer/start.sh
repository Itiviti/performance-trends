#!/bin/sh
cd /apps/performance-trends-visualizer/elasticsearch-1.4.1/bin/
x-terminal-emulator -e ./elasticsearch

cd /apps/performance-trends-visualizer/logstash-1.4.2/bin/
x-terminal-emulator -e ./logstash agent -f /apps/performance-trends-visualizer/logstash-1.4.2/lib/logstash/config/performance-trends-visualizer-linux.conf

cd /apps/performance-trends-visualizer/apache-tomcat-8.0.15/bin/
./catalina.sh run