package com.ullink.guice.duration.interceptor;

import com.ullink.method.selector.annotation.ProfileExecution;

public class SampleBean {

    @ProfileExecution
    public void sampleMethod() {
        // this is method for which we save execution times
    }
}