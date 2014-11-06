package com.ullink.spring.duration.interceptor;

import com.ullink.method.selector.annotation.ProfileExecution;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleBean {
    private static final Log LOGGER = LogFactory.getLog(SampleBean.class);

    @ProfileExecution
    public void sampleMethod() {
        // this is method for which we save execution times
    }
}