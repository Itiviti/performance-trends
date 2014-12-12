# Setting up the development environment

* Clone the project
* Run `gradlew clean build` from the project root directory
* Make sure your IDE has the company proxy configured properly (it will need to download a Gradle archive from the internet)
* Import the project in IDEA / Eclipse starting from the existing Gradle model. In IDEA select option "Use default gradle wrapper"

# Attaching the java profiler agent to any (Ullink) project to gather statistics while the tests run

## Supported Java agent args explained

When attaching the Java agent you can specify 3 args after the `=` sign, like in the below example:

`-javaagent:d:\\Data\\Code\\performance-trends\\profiler-agent\\build\\libs\\profiler-agent-1.0-SNAPSHOT.jar=d:\\Data\\method-selection.properties,d:\\logs,SMART`

The 1st agent arg is the path to method selector file (mandatory)
The 2nd agent arg in the desired output location (optional, it defaults to java temp dir)
The 3rd agent arg is a tag you can use like a project/product name which you attached the profiler to (optional, defaults to NOTAG)

## Test it in Ant projects:

```
ant test -Dtest.jvmarg="-javaagent:d:\\Data\\Code\\performance-trends\\profiler-agent\\build\\libs\\profiler-agent-1.0-SNAPSHOT.jar=d:\\Data\\method-selection.properties,d:\\logs,SMART"
```

Example linux version:

```
ant test -Dtest.jvmarg="-javaagent:/data/code/performance-trends/profiler-agent/build/libs/profiler-agent-1.0-SNAPSHOT.jar=/data/method-selection.properties,/data/logs,SMART"
```


## Test it in Gradle projects

In Gradle there is no possibility to pass the test JVM args from command line, so you'll have to modify your project's Gradle build file by adding:

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

 By default logs are generated as *.data in the java temp dir's `durations/` subdirectory, but you can change this default using a java agent arg (see section on Java agent args).
 These are memory mapped binary files which contain text and have a size of 32MB (at least).
 Each process and thread has it's own file. On linux the disk space is lazily occupied, while on Windows 32MB is occupied from the start.
 After profiling ran you need o compact the files (remove the nulls / empty lines from it). See the next section on the easiest way to do this.


# How can I merge the multiple log files in a single one?

 Start shell scipt `performance-trends/fast-logger/src/main/sh/data_file_compacter.sh` like this:

 ./data_file_compacter.sh

 from a linux console or Git bash.
 This script also copies the resulted compacted/merged *.log file in the logstash input directory.
 Compacting might need a few minutes (depending on the quantity of logged lines available in the *.data files).

# How to filter watched methods?

Create a csv file with the following structure:
package-name;class;method;visibility

Example:
com.ullink.performance-trends;;;true                                    - makes  package visible
com.ullink.performance-trends.hidden;;;false                            - hides a package
com.ullink.performance-trends.visible;HiddenClass;;false                - hides a class from a package
com.ullink.performance-trends.visible;VisibleClass;hiddenMethod;false   - hides a method from a visible class
*;WildCardHiddenClass;;false                                            - hides all occurrences of the given class
*;*;wildCardHiddenMethod;false                                          - hides all occurrences of the given method

 
# Markdown?

This file is a markdown file. For formatting tips check [Markdown cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)