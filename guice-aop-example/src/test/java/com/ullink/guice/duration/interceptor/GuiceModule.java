package com.ullink.guice.duration.interceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.ullink.method.selector.annotation.ProfileExecution;
import com.ullink.aop.interceptor.MethodDurationInterceptor;

import static com.google.inject.matcher.Matchers.subclassesOf;

public class GuiceModule extends AbstractModule {

    public void configure() {
        bindInterceptor(
                subclassesOf(SampleBean.class),
                Matchers.annotatedWith(ProfileExecution.class),
                new MethodDurationInterceptor());
    }
}