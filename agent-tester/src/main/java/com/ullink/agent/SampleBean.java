package com.ullink.agent;

import java.util.logging.Logger;

public class SampleBean
{
    private static final Logger LOGGER = Logger.getLogger(SampleBean.class.getName());

    public void sampleMethod()
    {
        LOGGER.info("Doing something which is measured with the agent");
    }
}