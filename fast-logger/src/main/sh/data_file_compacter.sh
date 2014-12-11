#!/bin/sh

# Linux paths
INPUT_DIR=/data/logs/durations
OUTPUT_DIR=/apps/performance-trends-visualizer/loginput

# Windows compatible paths (to make the script work in Git bash)
#INPUT_DIR=/D/logs/durations
#OUTPUT_DIR=/C/performance-trends-visualizer/loginput

ORIGINAL_FILE_EXTENSION='*.data'
numberOfInputFiles=`find $INPUT_DIR -type f -name $ORIGINAL_FILE_EXTENSION |wc -l`

if [ $numberOfInputFiles -gt 0 ]; then
  START_TIME=$SECONDS
  echo "Found  $numberOfInputFiles files matching $ORIGINAL_FILE_EXTENSION to merge! Now starting to merge."
  rm -rf $INPUT_DIR/*.index
  outputFileName=merged-`date +%Y-%m-%d-%H%M%S`.log
  find $INPUT_DIR -type f -name $ORIGINAL_FILE_EXTENSION -exec grep -a '`' {} \; > $INPUT_DIR/$outputFileName

  echo "Output file is ready and it starts like this:"
  head -n 1 $INPUT_DIR/$outputFileName
  echo "And it ends like this:"
  tail -n 1 $INPUT_DIR/$outputFileName

  echo "Moving file to logstah input folder: $OUTPUT_DIR/$outputFileName"

  rm -rf $INPUT_DIR/$ORIGINAL_FILE_EXTENSION
  mv $INPUT_DIR/$outputFileName  $OUTPUT_DIR/$outputFileName
  ELAPSED_TIME=$(($SECONDS - $START_TIME))
  echo "Compating files took: $ELAPSED_TIME seconds"

else
  echo "No $ORIGINAL_FILE_EXTENSION files found to merge!"
fi
