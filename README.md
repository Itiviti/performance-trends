# What this project is about?
To understand the what / why / how please read the [introduction from our wiki.](https://github.com/Ullink/performance-trends/wiki)

# Setting up the development environment

* Clone the project
* In case you are behind an HTTP/HTTPS proxy set the environment variable: `GRADLE_OPTS` to `"-Dhttps.proxyHost=proxy -Dhttps.proxyPort=xxxx -Dhttp.proxyHost=proxy -Dhttp.proxyPort=xxxx"`
* Run `gradlew clean assemble` from the project root directory. Note: you could run `gradlew clean test` will generate some sample log files in the java io temp dir.
* Import the project in IDEA / Eclipse starting from the existing Gradle model. In IDEA select option "Use default Gradle wrapper"

# Attaching the Java profiler agent to an application to gather statistics

## Supported Java agent args explained

When attaching the Java agent you can specify 3 args after the `=` sign, like in the below example:

`-javaagent:d:\\Data\\Code\\performance-trends\\profiler-agent\\build\\libs\\profiler-agent-1.0-SNAPSHOT.jar=d:\\Data\\method-selection.csv,d:\\logs,SAMPLETAG`

The 1st agent arg is the path to method selector file (optional, by default it will match all public methods from all classes)
The 2nd agent arg in the desired output location (optional, it defaults to java temp dir)
The 3rd agent arg is a tag you can use like a project/product name which you attached the profiler to (optional, defaults to NOTAG)

# Where to look for the log files?

 By default logs are generated as *.data in the java temp dir's `durations/` subdirectory, but you can change this default using a java agent arg (see section on Java agent args).
 These are memory mapped binary files which contain text and have a size of 32MB (at least).
 Each process and thread has it's own file. On linux the disk space is lazily occupied, while on Windows 32MB is occupied from the start.
 After profiling ran you need o compact the files (remove the nulls / empty lines from it). See the next section on the easiest way to do this.


# How can I merge the multiple log files in a single one?

 Start shell scipt `trends-visualizer/installer/compact_files.sh` like this:

 `./compact_files.sh`

 from a linux console or Git bash.
 This script also copies the resulted compacted/merged *.log file in the logstash input directory.
  Compacting might need a few minutes (depending on the quantity of logged lines available in the *.data files).

# How to filter watched methods?

Create a csv-style file with the following structure:

`package-name;class;method;visibility`

Example:

com.ullink.performance-trends;;;true                                    - makes  package visible <br/>
com.ullink.performance-trends.hidden;;;false                            - hides a package <br/>
com.ullink.performance-trends.visible;HiddenClass;;false                - hides a class from a package <br/>
com.ullink.performance-trends.visible;VisibleClass;hiddenMethod;false   - hides a method from a visible class <br/>
*;WildCardHiddenClass;;false                                            - hides all occurrences of the given class <br/>
*;*;wildCardHiddenMethod;false                                          - hides all occurrences of the given method <br/>

and pass it as the first agent argument.

# Visualizing the results in Kibana

TODO: finish the installer and then add instructions here!
 
# Markdown?
For formatting tips check [Markdown cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)
