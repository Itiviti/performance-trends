#!/bin/sh

# TODOs
# - also check SHA1/MD5 for the zip archives before deciding whether to download or not
# - process the logstash conf files to contain the right paths (just take a configured path from the install.conf for this purpose)
# - make the compact_file.sh script aware of the install location.
# - start elasticsearch and create the schema using CURL after it's already running (check with ping when it's already up) or check for other ways to create the schema).
# - import kibana dashboard (not sure sure how to do it, but it looks like it's stored in the index, so curl might work)

source install.conf
source scripts/file_helper.sh

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

cp scripts/start.sh "$INSTALL_DIR/"
handlePossibleError $?
cp scripts/start.bat "$INSTALL_DIR/"
handlePossibleError $?

cp configuration/kibana/config.js "$tomcatWebAppsRoot/"
handlePossibleError $?

cp -R configuration/logstash/* "$INSTALL_DIR/$LOGSTASH_FOLDER_NAME/"
handlePossibleError $?

# save home directories so that the generic start script can start all apps, no matter their version
homeDirsFile=$INSTALL_DIR/.homedirs
echo "ELASTICSEARCH=$ELASTICSEARCH_FOLDER_NAME" > "$homeDirsFile"
echo "LOGSTASH=$LOGSTASH_FOLDER_NAME" >> "$homeDirsFile"
echo "TOMCAT=$TOMCAT_FOLDER_NAME" >> "$homeDirsFile"

# cleanup downloaded stuff
#rm -rf "$DOWNLOAD_TEMP_DIR/"