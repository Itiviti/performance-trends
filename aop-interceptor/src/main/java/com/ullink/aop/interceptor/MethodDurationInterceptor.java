package com.ullink.aop.interceptor;

import com.ullink.logging.FastLogger;
import org.aopalliance.intercept.MethodInterceptor;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

public class MethodDurationInterceptor implements MethodInterceptor {

    private static final String LOG_MESSAGE_FORMAT = "## Execution took %dµs for %s.%s using arguments %s";
    private static final FastLogger LOGGER = FastLogger.getInstance();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startNanoSeconds = System.nanoTime();
        try {
            return invocation.proceed();
        } finally {
            long microSecondDuration = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startNanoSeconds);

            Method method = invocation.getMethod();
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            String methodArguments = Arrays.toString(invocation.getArguments());
            String logMessage = String.format(LOG_MESSAGE_FORMAT, microSecondDuration, className, methodName, methodArguments);
            LOGGER.log(logMessage);

        }
    }
}