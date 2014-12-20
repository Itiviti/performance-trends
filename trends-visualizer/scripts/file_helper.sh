#!/bin/sh


function handlePossibleError {
  errorCode=$1
  if [ $errorCode -ne 0 ]; then
    echo "Existing because of error: $errorCode"
   exit
  fi
}

function downloadFile {
  remoteLocation=$1
  downloadLocation=$2
  if [ ! -f $downloadLocation ]
    then
    echo "Downloading $remoteLocation to $downloadLocation"
    curl -o $downloadLocation $remoteLocation
    handlePossibleError $?
  else
    echo "File $downloadLocation found, no need for downloading it."
  fi
}

function unzipArchive {
  inputFile=$1
  outputDir=$2
  echo "Unzipping $inputFile to $outputDir"
  unzip -o $inputFile -d $outputDir
  handlePossibleError $?
}

function getFileNameWithoutZipExtension {
  fileName=$1
  result=$2
  fileNameWithoutExtension=${fileName%.zip}
  # not possible to return values from shell script method, thus returning value in second argument: http://www.linuxjournal.com/content/return-values-bash-functions
  eval $result="'$fileNameWithoutExtension'"
}