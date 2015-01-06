package com.ullink.agent;

import org.junit.Test;

public class AgentTest {

    @Test
    public void testAgent() {
        for (int counter = 0; counter < 100; counter++) {
            new SampleBean().sampleMethod1();
            new SampleBean().sampleMethod2("test arg2", 2);
        }
    }
}
