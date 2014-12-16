#!/bin/sh

# TODO:

# 1. have a configurable install dir (use current dir as fallback if not specfied).
# 2. download elasticsearch, logstash, tomcat & kibana using curl and unzip them to the destination install dir. Use configurable URLs (in order to make upgrades easy!).
# 3. copy exploded kibana war in Tomcat root, copy config files (regex pattern file and *.conf files processd to contain valid paths) to logstash.
# 4. start elasticsearch and create the schema using CURL after it's already running (check with ping when it's already up) or check for other ways to create the schema).
# 5. import kibana dashboard (not sure sure how to do it, but it looks like it's stored in the index, so curl might work)