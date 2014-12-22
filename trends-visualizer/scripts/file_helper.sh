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
  echo "Unzipping $inputFile to $outputDir, this might take a while..."
  unzip -q -o $inputFile -d $outputDir
  handlePossibleError $?
  echo "Unzipping $inputFile finished."
}

function getFileNameWithoutZipExtension {
  fileName=$1
  result=$2
  fileNameWithoutExtension=${fileName%.zip}
  # not possible to return values from shell script method, thus returning value in second argument: http://www.linuxjournal.com/content/return-values-bash-functions
  eval $result="'$fileNameWithoutExtension'"
}

function replacePlaceHolderInFile {
  filePath=$1
  placeHolder=$2
  replaceValue=$3
  tempFilePath="$filePath.temp"
  # sed in place replace does not work on windows because of windows security stuff, that's why we use a temp file
  cp $filePath $filePath.temp
  sed "s/$placeHolder/$(echo "$replaceValue" | sed -e 's/\\/\\\\/g' -e 's/\//\\\//g' -e 's/&/\\\&/g')/g" $tempFilePath > $filePath
  returnValue=$?
  rm -rf $tempFilePath
  return $returnValue
}