# Setting up the performance trends visualizer environment:

1) make sure you have java installed (environment was tested with JDK8, but it might work with JDK7 also, but it does NOT work with JDK6 or lower!)

2) on Windows: - make sure you copied this directory to C:\ (so that the root folder of the cloned repo will be C:\performance-trends-visualizer on your disk)
   on Linux: - make sure you copied this directory to /apps (so that the root folder of the cloned repo will be /apps/performance-trends-visualizer on your disk)

3) run start.bat (Windows) or ./start.sh (Linux) from the root directory (performance-trends-visualizer)

4) open Kibana in your browser: http://localhost:8080

5) create the lucene index by running manually running the below from a Git bash (windows) or standard bash (Linux): <br />
curl -XPUT http://localhost:9200/performance_trends_index -d '
{
 "mappings" : {
  "_default_" : {
   "properties" : {
    "pt_package" : {"type": "string", "index" : "not_analyzed" },
    "pt_class" : {"type": "string", "index" : "not_analyzed" },
    "pt_method" : {"type": "string", "index" : "not_analyzed" },
    "pt_method_args" : { "type" : "string", "index" : "not_analyzed" },
	"pt_thread" : { "type" : "string", "index" : "not_analyzed" },
	"pt_duration_micros" : { "type" : "integer" },
	"pt_date_epoch" : { "type" : "date"}
   }
  }
 }
}
';

6) add files (with the *.data extension) to:
   C:\performance-trends-visualizer\loginput\ (on windows)
   /apps/performance-trends-visualizer/loginput/ (on windows)
   and they will appear in Kibana

# ELK config and utils

1) Custom files added to logstash config:

a) logstash-1.4.2\patterns\performance-trends-patterns
b) logstash-1.4.2\lib\logstash\config\performance-trends-visualizer-win.conf
   logstash-1.4.2\lib\logstash\config\performance-trends-visualizer-linux.conf

2) adding stuff manually via HTTP (not necessary only if we want to test it without the logstash import):

curl -XPUT 'http://localhost:9200/performance_trends_index/performance-trend/1' -d '{
    "pt_class" : "info.bluefloyd.profiler.test.guice.TestBean",
    "pt_method" : "helloMethod"
	"pt_duration_micros" : 9000
}'

3) In case you want to modify the structure of the log this is the recommended reading:
GROK debugger: http://grokdebug.herokuapp.com/
Regex syntax: http://www.geocities.jp/kosako3/oniguruma/doc/RE.txt
Lucene query syntax: http://www.lucenetutorial.com/lucene-query-syntax.html