#!/bin/bash

INPUT_DIR={COMPACTER_INPUT_FOLDER_PLACEHOLDER}
OUTPUT_DIR={COMPACTER_OUTPUT_FOLDER_PLACEHOLDER}

ORIGINAL_FILE_EXTENSION='*.data'
numberOfInputFiles=`find $INPUT_DIR -type f -name $ORIGINAL_FILE_EXTENSION |wc -l`

if [ $numberOfInputFiles -gt 0 ]; then
  START_TIME=$SECONDS
  echo "Found $numberOfInputFiles files matching $ORIGINAL_FILE_EXTENSION to compact! Now starting to compact/merge file(s)."
  rm -rf $INPUT_DIR/*.index
  outputFileName=merged-`date +%Y-%m-%d-%H%M%S`.log
  find $INPUT_DIR -type f -name $ORIGINAL_FILE_EXTENSION -exec grep -a '`' {} \; > $INPUT_DIR/$outputFileName

  ELAPSED_TIME=$(($SECONDS - $START_TIME))
  echo -e "\nCompacting files took: $ELAPSED_TIME seconds"

  echo -e "\nOutput file is ready and it starts like this:"
  head -n 1 $INPUT_DIR/$outputFileName
  echo -e "\nAnd it ends like this:"
  tail -n 1 $INPUT_DIR/$outputFileName

  lineCount=`cat $INPUT_DIR/$outputFileName | wc -l`
  fileSize=`du -h $INPUT_DIR/$outputFileName | cut -f1`
  echo  "The resulted file has $lineCount lines and a size of $fileSize"
  echo -e "\nMoving file to output folder: $OUTPUT_DIR/$outputFileName"

  rm -rf $INPUT_DIR/$ORIGINAL_FILE_EXTENSION
  mv $INPUT_DIR/$outputFileName  $OUTPUT_DIR/$outputFileName
  echo -e "\nDone."

else
  echo "No $ORIGINAL_FILE_EXTENSION files found to merge!"
fi
