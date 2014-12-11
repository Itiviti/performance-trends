# Setting up the development environment

* Clone the project
* Run `gradlew clean build` from the project root directory
* Make sure your IDE has the company proxy configured properly (it will need to download a Gradle archive from the internet)
* Import the project in IDEA / Eclipse starting from the existing Gradle model. In IDEA select option "Use default gradle wrapper"

# Attaching the java profiler agent to any (Ullink) project to gather statistics while the tests run

## For Ant projects use:

```
ant test -Dtest.jvmarg="-javaagent:d:\\Data\\Code\\performance-trends\\profiler-agent\\build\\libs\\profiler-agent-1.0-SNAPSHOT.jar=d:\\Data\\method-selection.properties,d:\\logs,SMART"
```

Example linux version:

```
ant test -Dtest.jvmarg="-javaagent:/data/code/performance-trends/profiler-agent/build/libs/profiler-agent-1.0-SNAPSHOT.jar=/data/method-selection.properties,/data/logs,SMART"
```

Note: first agent arg is the path to method selector properties file, the second in the desired output location (optional, default to java temp dir), the 3rd is a tag (optional)


## For Gradle there is no possibility to pass the test JVM args from command line, so you'll have to modify your project's Gradle build file by adding:

```
if (isProfilingEnabled()) {
    test {
        jvmArgs '-javaagent:d:\\Data\\Code\\performance-trends\\profiler-agent\\build\\libs\\profiler-agent-1.0-SNAPSHOT.jar=d:\\Data\\method-selection.properties,d:\\logs,EDMA'
    }
}
```

and defining method:

```
def isProfilingEnabled(){
    project.hasProperty('doProfile') && project.doProfile instanceof String
}
```

and then running the build like in the below example:

 `gradlew clean build -PdoProfile`


 Example linux version:

```
    if (isProfilingEnabled()) {
        test {
            jvmArgs '-javaagent:/data/code/performance-trends/profiler-agent//build/libs/profiler-agent-1.0-SNAPSHOT.jar=/data/method-selection.properties,/data/logs,EDMA'
        }
    }
```


# Where to look for the log files?

 Logs are generated in the java temp dir's `durations/` subdirectory (we will make it configurable)


# How can I merge the multiple log files in a single one?

 Just use this linux command (also works in a Git bash from Windows):

 rm -rf *.index && find . -type f -name '\*.data' -exec grep -a '`' {} \; > merged.log && rm -rf *.data

 and then manually copy the resulted *.log file in the logstash input folder (...performance-trends-visualizer/loginput/)

 An automated option is to adapt the input/output folders from `performance-trends/fast-logger/src/main/sh/data_file_compacter.sh`
 and just run it every time you need to compact data files. This script also copies the resulted merged file in the logstash input directory.
 There is even an IDEA plugin for shell script which allow running it from IDEA. On windows you can use the Git bash.

# Markdown?

This file is a markdown file. For formatting tips check [Markdown cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)