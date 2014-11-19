# Setting up the development environment

* Clone the project
* Compile it from command line by running "gradlew clean assemble" from the project root directory
* Make sure your IDE has the company proxy configured properly (it will need to download a Gradle archive from the internet)
* Import the project in IDEA / Eclipse starting from the existing Gradle model. In IDEA select option "Use default gradle wrapper".

Note: we have a basic working Java agent also, you can test it by running the below command from the project root directory:
 java -cp agent-tester/build/libs/agent-tester-1.0-SNAPSHOT.jar -javaagent:profiler-agent/build/libs/profiler-agent-1.0-SNAPSHOT.jar com.ullink.agent.AgentTester

Note: for editing/formatting options in this README you can find tips in [Markdown cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)