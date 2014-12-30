#!/bin/bash

# remaining TODOs
# - check SHA1/MD5 for the zip archives before deciding whether to re-download or not
# - unzip only if not yet unzipped (not sure yet how to check, but would speed up things)
# - start elasticsearch and create the schema using CURL after it's already running (check with ping when it's already up) or check for other ways to create the schema).
# - HTTP/HTTPS proxy support (which can be enabled form the install.conf file)
# - import kibana dashboard (not sure sure how to do it, but it looks like it's stored in the index, so curl might work)
# - kibana, elasticsearch config placeholders for being able to refer them with host names not only on localhost
# - make log importer script be able to run as a daemon (tail data files and loop forever, in a multi-tail style, but with a dynamic number of files)

INSTALL_START_TIME=$SECONDS

source install.conf
source scripts/file_helper.sh
SCRIPT_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

ZIP_SUFFIX='.zip'
TEMPDIR_NAME='temp'
DOWNLOAD_TEMP_DIR="$INSTALL_DIR/$TEMPDIR_NAME"

ELASTICSEARCH_LOCAL_ARCHIVE_NAME="${ELASTICSEARCH_REMOTE_ARCHIVE_URL##*/}"
LOGSTASH_LOCAL_ARCHIVE_NAME="${LOGSTASH_REMOTE_ARCHIVE_URL##*/}"
KIBANA_LOCAL_ARCHIVE_NAME="${KIBANA_REMOTE_ARCHIVE_URL##*/}"
TOMCAT_LOCAL_ARCHIVE_NAME="${TOMCAT_REMOTE_ARCHIVE_URL##*/}"

getFileNameWithoutZipExtension $ELASTICSEARCH_LOCAL_ARCHIVE_NAME ELASTICSEARCH_FOLDER_NAME
getFileNameWithoutZipExtension $LOGSTASH_LOCAL_ARCHIVE_NAME LOGSTASH_FOLDER_NAME
getFileNameWithoutZipExtension $KIBANA_LOCAL_ARCHIVE_NAME KIBANA_FOLDER_NAME
getFileNameWithoutZipExtension $TOMCAT_LOCAL_ARCHIVE_NAME TOMCAT_FOLDER_NAME

TOMCAT_WEBAPPS_FOLDER="$INSTALL_DIR/$TOMCAT_FOLDER_NAME/webapps"
TOMCAT_BIN_FOLDER="$INSTALL_DIR/$TOMCAT_FOLDER_NAME/bin"

echo "Visualizer will be installed to $INSTALL_DIR"
mkdir -p $INSTALL_DIR
handlePossibleError $?

echo "Temporary files will be downloaded to $DOWNLOAD_TEMP_DIR"
mkdir -p $DOWNLOAD_TEMP_DIR
handlePossibleError $?

downloadFile $ELASTICSEARCH_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$ELASTICSEARCH_LOCAL_ARCHIVE_NAME"
downloadFile $LOGSTASH_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$LOGSTASH_LOCAL_ARCHIVE_NAME"
downloadFile $KIBANA_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$KIBANA_LOCAL_ARCHIVE_NAME"
downloadFile $TOMCAT_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$TOMCAT_LOCAL_ARCHIVE_NAME"

DOWNLOAD_DURATION=$(($SECONDS - $INSTALL_START_TIME))

unzipArchive "$DOWNLOAD_TEMP_DIR/$ELASTICSEARCH_LOCAL_ARCHIVE_NAME" $INSTALL_DIR
unzipArchive "$DOWNLOAD_TEMP_DIR/$LOGSTASH_LOCAL_ARCHIVE_NAME" $INSTALL_DIR
unzipArchive "$DOWNLOAD_TEMP_DIR/$TOMCAT_LOCAL_ARCHIVE_NAME" $INSTALL_DIR
unzipArchive "$DOWNLOAD_TEMP_DIR/$KIBANA_LOCAL_ARCHIVE_NAME" $DOWNLOAD_TEMP_DIR

tomcatWebAppsFolderMatcher="$TOMCAT_WEBAPPS_FOLDER/*"
echo "Removing Tomcat's default webapps: $tomcatWebAppsFolderMatcher"
rm -rf $tomcatWebAppsFolderMatcher
handlePossibleError $?

tomcatWebAppsRoot="$TOMCAT_WEBAPPS_FOLDER/ROOT"
mkdir -p $TOMCAT_WEBAPPS_FOLDER
handlePossibleError $?
echo "Moving exploded Kibana to Tomcat's root folder: $tomcatWebAppsRoot"
mv "$DOWNLOAD_TEMP_DIR/$KIBANA_FOLDER_NAME" $tomcatWebAppsRoot
handlePossibleError $?
rm -rf "$DOWNLOAD_TEMP_DIR/$KIBANA_FOLDER_NAME"

chmod +x "$TOMCAT_BIN_FOLDER/"*.sh

echo 'Copying linux starter script'
cp "$SCRIPT_DIR/scripts/start.sh" "$INSTALL_DIR/"
handlePossibleError $?
chmod +x $INSTALL_DIR/start.sh

echo 'Copying windows starter script'
cp "$SCRIPT_DIR/scripts/start.bat" "$INSTALL_DIR/"
handlePossibleError $?

echo 'Copying elasticsearch config'
cp -R "$SCRIPT_DIR/configuration/elasticsearch/"* "$INSTALL_DIR/$ELASTICSEARCH_FOLDER_NAME/"
handlePossibleError $?

echo 'Copying kibana configuration and dashboard'
cp -R "$SCRIPT_DIR/configuration/kibana/"* "$tomcatWebAppsRoot/"
handlePossibleError $?

echo 'Copying logimporter script'
logImporterFolderName='logimporter'
importScriptFilename='import.sh'
cp -R "$SCRIPT_DIR/configuration/$logImporterFolderName" "$INSTALL_DIR/"
handlePossibleError $?

echo 'Replacing placeholders in log importer script'
importScriptDestPath="$INSTALL_DIR/$logImporterFolderName/$importScriptFilename"
replacePlaceHolderInFile $importScriptDestPath '{COMPACTER_INPUT_FOLDER_PLACEHOLDER}' $AGENT_DATA_FILE_DIR
handlePossibleError $?
replacePlaceHolderInFile $importScriptDestPath '{COMPACTER_OUTPUT_FOLDER_PLACEHOLDER}' $LOGSTASH_INPUT_DIRECTORY
handlePossibleError $?

chmod +x "$INSTALL_DIR/$logImporterFolderName/"*.sh

echo 'Copying logstash config'
cp -R "$SCRIPT_DIR/configuration/logstash/"* "$INSTALL_DIR/$LOGSTASH_FOLDER_NAME/"
handlePossibleError $?

echo 'Replacing placeholders in logstash conf file'
logstashConfFilePath="$INSTALL_DIR/$LOGSTASH_FOLDER_NAME/lib/logstash/config/trends-visualizer.conf"
logstashInputPathPattern="$LOGSTASH_INPUT_DIRECTORY/*.log"
replacePlaceHolderInFile $logstashConfFilePath '{LOGSTASH_INPUT_PATH_PLACEHOLDER}' "$logstashInputPathPattern"
handlePossibleError $?

mkdir -p "$INSTALL_DIR/loginput"

# save home directories so that the generic start script can start all apps, no matter their version
echo 'Saving app folder names'
homeDirsFile=$INSTALL_DIR/.appdirs
echo "ELASTICSEARCH=$ELASTICSEARCH_FOLDER_NAME" > "$homeDirsFile"
echo "LOGSTASH=$LOGSTASH_FOLDER_NAME" >> "$homeDirsFile"
echo "TOMCAT=$TOMCAT_FOLDER_NAME" >> "$homeDirsFile"

echo 'Cleaning up temporary installation files'
#rm -rf "$DOWNLOAD_TEMP_DIR/"

INSTALLATION_DURATION=$(($SECONDS - $INSTALL_START_TIME))
echo "Installation finished! It took $INSTALLATION_DURATION seconds in total, from which $DOWNLOAD_DURATION seconds were spent downloading."

echo "To start up the visualizer run: $INSTALL_DIR/start.sh (on Linux) or $INSTALL_DIR/start.bat (on Windows)."
echo "The performance dashboard will be available at: http://localhost:8080/index.html#/dashboard/file/performance-dashboard.json"