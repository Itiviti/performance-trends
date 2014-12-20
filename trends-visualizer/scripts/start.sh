#!/bin/sh

source .homedirs

BIN_DIR=`pwd`

echo $ELASTICSEARCH
echo $LOGSTASH
echo $TOMCAT

cd "$BIN_DIR/$ELASTICSEARCH/bin/"
#x-terminal-emulator -e ./elasticsearch

cd "$BIN_DIR/$LOGSTASH/bin/"
#x-terminal-emulator -e ./logstash agent -f $LOGSTASH_HOME/lib/logstash/config/performance-trends-visualizer-linux.conf

cd "$BIN_DIR/$TOMCAT/bin/"
./catalina.sh run