#!/bin/bash

SCRIPT_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

until [ "`curl -i --silent --connect-timeout 1 -I http://localhost:9200 | grep '200 OK'`" != "" ];
do
  echo 'Waiting for ElasticSearch to start...'
  sleep 3
done

echo 'ElasticSearch is up and running!'
echo 'Checking if performance-trends index exists'
if [ "`curl -i --silent -XHEAD 'http://localhost:9200/performance_trends_index' | grep '200 OK'`" = "" ]
  then
   echo 'Performance-trends index does not exist, creating it now.'
   payload=`cat $SCRIPT_DIR/performance-trends.json`
   createIndexResult=`curl -i --silent -XPUT http://localhost:9200/performance_trends_index -d "$payload" | grep '200 OK'`
   echo "Index creation result is: $createIndexResult"
  else
   echo 'Performance-trends index already exists.'
fi
