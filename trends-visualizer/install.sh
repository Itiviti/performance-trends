#!/bin/sh

# TODOs
# - check SHA1/MD5 for the zip archives before deciding whether to download or not
# - unzip only if not yet unzipped (not sure yet how to check)
# - make the compact_file.sh script aware of the install location.
# - start elasticsearch and create the schema using CURL after it's already running (check with ping when it's already up) or check for other ways to create the schema).
# - import kibana dashboard (not sure sure how to do it, but it looks like it's stored in the index, so curl might work)

INSTALL_START_TIME=$SECONDS

source install.conf
source scripts/file_helper.sh
SCRIPT_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

ZIP_SUFFIX='.zip'
DOWNLOAD_SUBDIR_NAME='download-temp'
DOWNLOAD_TEMP_DIR="$INSTALL_DIR/$DOWNLOAD_SUBDIR_NAME"

ELASTICSEARCH_LOCAL_ARCHIVE_NAME="${ELASTICSEARCH_REMOTE_ARCHIVE_URL##*/}"
LOGSTASH_LOCAL_ARCHIVE_NAME="${LOGSTASH_REMOTE_ARCHIVE_URL##*/}"
KIBANA_LOCAL_ARCHIVE_NAME="${KIBANA_REMOTE_ARCHIVE_URL##*/}"
TOMCAT_LOCAL_ARCHIVE_NAME="${TOMCAT_REMOTE_ARCHIVE_URL##*/}"

getFileNameWithoutZipExtension $ELASTICSEARCH_LOCAL_ARCHIVE_NAME ELASTICSEARCH_FOLDER_NAME
getFileNameWithoutZipExtension $LOGSTASH_LOCAL_ARCHIVE_NAME LOGSTASH_FOLDER_NAME
getFileNameWithoutZipExtension $KIBANA_LOCAL_ARCHIVE_NAME KIBANA_FOLDER_NAME
getFileNameWithoutZipExtension $TOMCAT_LOCAL_ARCHIVE_NAME TOMCAT_FOLDER_NAME

TOMCAT_WEBAPPS_FOLDER="$INSTALL_DIR/$TOMCAT_FOLDER_NAME/webapps"

echo "Visualizer will be installed to $INSTALL_DIR"
mkdir -p $INSTALL_DIR
handlePossibleError $?

echo "Files will be downloaded to $DOWNLOAD_TEMP_DIR"
mkdir -p $DOWNLOAD_TEMP_DIR
handlePossibleError $?

downloadFile $ELASTICSEARCH_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$ELASTICSEARCH_LOCAL_ARCHIVE_NAME"
downloadFile $LOGSTASH_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$LOGSTASH_LOCAL_ARCHIVE_NAME"
downloadFile $KIBANA_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$KIBANA_LOCAL_ARCHIVE_NAME"
downloadFile $TOMCAT_REMOTE_ARCHIVE_URL "$DOWNLOAD_TEMP_DIR/$TOMCAT_LOCAL_ARCHIVE_NAME"

DOWNLOAD_DURATION=$(($SECONDS - $INSTALL_START_TIME))

#unzipArchive "$DOWNLOAD_TEMP_DIR/$ELASTICSEARCH_LOCAL_ARCHIVE_NAME" $INSTALL_DIR
#unzipArchive "$DOWNLOAD_TEMP_DIR/$LOGSTASH_LOCAL_ARCHIVE_NAME" $INSTALL_DIR
#unzipArchive "$DOWNLOAD_TEMP_DIR/$TOMCAT_LOCAL_ARCHIVE_NAME" $INSTALL_DIR
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

echo 'Copying linux starter script'
cp "$SCRIPT_DIR/scripts/start.sh" "$INSTALL_DIR/"
handlePossibleError $?

echo 'Copying windows starter script'
cp "$SCRIPT_DIR/scripts/start.bat" "$INSTALL_DIR/"
handlePossibleError $?

echo 'Copying elasticsearch config'
cp -R "$SCRIPT_DIR/configuration/elasticsearch/"* "$INSTALL_DIR/$ELASTICSEARCH_FOLDER_NAME/"
handlePossibleError $?

echo 'Copying kibana config'
cp "$SCRIPT_DIR/configuration/kibana/config.js" "$tomcatWebAppsRoot/"
handlePossibleError $?

echo 'Copying file compacter shell script'
importScriptTemplateSourcePath="$SCRIPT_DIR/scripts/import_logs.sh.template"
importScriptDestPath="$INSTALL_DIR/import_logs.sh"
importScriptTemplateDestPath="$importScriptDestPath.template"
cp $importScriptTemplateSourcePath $importScriptTemplateDestPath

handlePossibleError $?
echo 'Updating file compacter shell script with real folders'
sed "s/{COMPACTER_INPUT_FOLDER_PLACEHOLDER}/$(echo "$AGENT_DATA_FILE_DIR" | sed -e 's/\\/\\\\/g' -e 's/\//\\\//g' -e 's/&/\\\&/g')/g" $importScriptTemplateDestPath > $importScriptDestPath
handlePossibleError $?
chmod +x $importScriptDestPath
rm -rf importScriptTemplateDestPath

echo 'Copying logstash config'
cp -R "$SCRIPT_DIR/configuration/logstash/"* "$INSTALL_DIR/$LOGSTASH_FOLDER_NAME/"
handlePossibleError $?

# replace placeholder in logstash configuration with the configured input pattern
echo 'Updating logstash config with the path pattern value taken from install.conf'
logstashConfFilePath="$INSTALL_DIR/$LOGSTASH_FOLDER_NAME/lib/logstash/config/trends-visualizer.conf"
logstashConfFileTemplatePath="$logstashConfFilePath.template"
sed "s/{LOGSTASH_INPUT_PATH_PLACEHOLDER}/$(echo "$LOGSTASH_INPUT_PATH_PATTERN" | sed -e 's/\\/\\\\/g' -e 's/\//\\\//g' -e 's/&/\\\&/g')/g" $logstashConfFileTemplatePath > $logstashConfFilePath
handlePossibleError $?
rm -rf $logstashConfFileTemplatePath

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
