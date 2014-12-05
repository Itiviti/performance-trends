# Setting up the development environment

* Clone the project
* Run `gradlew clean build` from the project root directory
* Make sure your IDE has the company proxy configured properly (it will need to download a Gradle archive from the internet)
* Import the project in IDEA / Eclipse starting from the existing Gradle model. In IDEA select option "Use default gradle wrapper"

# Attaching the java profiler agent to any (Ullink) project to gather statistics while the tests run

## For Ant projects use:

```
ant test -Dtest.jvmarg=-javaagent:d:\Data\Code\performance-trends\profiler-agent\build\libs\profiler-agent-1.0-SNAPSHOT.jar
```

 (if you need more args search development\buildtools\ant\antref\build.xml for jvmArgs)


## For Gradle there is no possibility to pass the test JVM args from command line, so you'll have to modify your project's Gradle build file by adding:

```
if (isProfilingEnabled()) {
    test {
        jvmArgs '-javaagent:d:\\Data\\Code\\performance-trends\\profiler-agent\\build\\libs\\profiler-agent-1.0-SNAPSHOT.jar'
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

# Where to look for the log files?

 Logs are generated in the java temp dir's `durations/` subdirectory (we will make it configurable)


# How can I merge the multiple log files in a single one?

 Just use this linux command (also works a Git bash in windows):

 `rm -rf *.index && find . -type f -name '*.data' -exec grep -a Duration {} \; > merged.log && rm -rf *.data`

# Markdown?

This file is a markdown file. For formatting tips check [Markdown cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)