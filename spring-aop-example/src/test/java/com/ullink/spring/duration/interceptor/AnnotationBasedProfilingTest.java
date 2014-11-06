package com.ullink.spring.duration.interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfiguration.class})
public class AnnotationBasedProfilingTest {

    @Autowired
    private SampleBean sampleBean;

    @Test
    public void testProfiling() {
        for (int i = 0; i < 50; i++) {
            sampleBean.sampleMethod();
        }
    }
}