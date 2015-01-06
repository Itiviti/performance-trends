package com.ullink.agent;

public class AgentTester
{

    public static void main(String[] args)
    {
        for (int counter = 0; counter < 100; counter++)
        {
            new SampleBean().sampleMethod1();
            new SampleBean().sampleMethod2("test arg1", 1);
        }
    }
}
