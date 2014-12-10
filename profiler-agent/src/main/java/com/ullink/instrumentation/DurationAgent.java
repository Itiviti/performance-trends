package com.ullink.instrumentation;

import java.lang.instrument.Instrumentation;

public class DurationAgent
{

    public static void premain(String agentArgs, Instrumentation inst)
    {
        System.out.println("Executing premain...");

        /**
         * Agent args can be passed in like this: -javaagent:d:\libs\profiler-agent-1.0-SNAPSHOT.jar=d:\Data\method-selection.properties,d:\Data\logs,EDMA
         * For now let's go with a simple convention:
         * - first arg is the path to the properties file in which the method selector is configured (mandatory)
         * - second arg is the location where the duration logs are generated (optional, default location being the java temp dir's durations folder)
         * - third arg is a tag we can later use to filter the measurements (e.g. filter results per EDMA, per SMART, per MONITORING etc)
         */
        if (agentArgs != null)
        {
            String[] args = agentArgs.split(",");
            System.out.println("Agent args are: ");
            for (String arg : args)
            {
                System.out.println(arg);
            }
        }
        inst.addTransformer(new DurationTransformer());
    }
}