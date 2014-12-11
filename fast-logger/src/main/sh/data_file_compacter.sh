#!/bin/sh
INPUT_DIR=/data/logs/durations
OUTPUT_DIR=/apps/performance-trends-visualizer/loginput

rm -rf *.index
outputFileName=merged-`date --iso-8601=seconds`.log
find $INPUT_DIR -type f -name '*.data' -exec grep -a '`' {} \; > $INPUT_DIR/$outputFileName

echo 'Output file ready, it start like this: '
head $INPUT_DIR/$outputFileName
echo 'Ends like this: '
tail $INPUT_DIR/$outputFileName

echo "Moving file to logstah input folder: $OUTPUT_DIR/$outputFileName"

rm -rf *.data
mv $INPUT_DIR/$outputFileName  $OUTPUT_DIR/$outputFileName