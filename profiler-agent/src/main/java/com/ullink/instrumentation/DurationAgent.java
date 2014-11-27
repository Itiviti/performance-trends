package com.ullink.instrumentation;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class DurationAgent {
    private static final Logger LOGGER = Logger.getLogger(DurationAgent.class.getName());

    public static void premain(String agentArgs, Instrumentation inst)
    {
        LOGGER.info("Executing premain...");
        inst.addTransformer(new DurationTransformer());
    }
}