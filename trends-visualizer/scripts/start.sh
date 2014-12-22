#!/bin/sh

APP_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
source $APP_DIR/.appdirs

ELASTICSEARCH_BIN="$APP_DIR/$ELASTICSEARCH/bin"
LOGSTASH_BIN="$APP_DIR/$LOGSTASH/bin"
TOMCAT_BIN="$APP_DIR/$TOMCAT/bin"

cd $ELASTICSEARCH_BIN
x-terminal-emulator -e ./elasticsearch

cd $LOGSTASH_BIN
x-terminal-emulator -e ./logstash agent -f "$APP_DIR/$LOGSTASH/lib/logstash/config/trends-visualizer.conf"

cd $TOMCAT_BIN
./catalina.sh run