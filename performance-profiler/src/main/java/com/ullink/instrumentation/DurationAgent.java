package com.ullink.instrumentation;

import com.ullink.duration.logging.FastLogger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class DurationAgent
{

    public static void premain(String agentArgs, Instrumentation inst)
    {
        System.out.println("Ullink profiler agent attached!");
        String tag = null;
        String csvFilePath = null;
        if (agentArgs != null)
        {
            String[] args = agentArgs.split(",");
            
            if(args.length >=1)
            {
                csvFilePath=args[0];
            }
            if (args.length >= 2) {
                String durationLogsPath = args[1];
                FastLogger.initLogPath(durationLogsPath);
                System.out.println("Log output directory is: " + durationLogsPath);
            }
            if(args.length >= 3)
            {
                tag = args[2];
            }
            

        }

        try
        {
            inst.addTransformer(new DurationTransformer(csvFilePath, tag));
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e);
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
    }
}