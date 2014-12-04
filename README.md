# Setting up the development environment

* Clone the project
* Compile it from command line by running "gradlew clean assemble" from the project root directory
* Make sure your IDE has the company proxy configured properly (it will need to download a Gradle archive from the internet)
* Import the project in IDEA / Eclipse starting from the existing Gradle model. In IDEA select option "Use default gradle wrapper".

Note: we have a basic working Java agent also, you can test it by running the below command from the project root directory:
 java -cp agent-tester/build/libs/agent-tester-1.0-SNAPSHOT.jar -javaagent:profiler-agent/build/libs/profiler-agent-1.0-SNAPSHOT.jar com.ullink.agent.AgentTester

# Attaching the java profiler agent to any Ullink project (to gather statistics while the tests run)

## For Ant project use e.g.

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

 and the running the build like in the below example:

 `gradlew clean build -PdoProfile`

 Where to look for logs:

 Logs are generated in the java temp dir's `durations/` subdirectory.


# Markdown?

This file is a markdown file. For editing/formatting options in this README you can find tips in [Markdown cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)