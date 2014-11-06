package com.ullink.guice.duration.interceptor;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class AnnotationBasedProfilingTest {

    @Inject
    private SampleBean sampleBean;

    @Before
    public void setup() {

        Injector injector = Guice.createInjector(new GuiceModule());
        injector.injectMembers(this);
    }

    @Test
    public void testProfiling() throws Exception {
        for (int i = 0; i < 50; i++) {
            sampleBean.sampleMethod();
        }
    }
}
