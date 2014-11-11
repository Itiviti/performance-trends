package com.ullink.aop.interceptor;

import com.ullink.logging.FastLogger;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodDurationInterceptor implements MethodInterceptor {

    private static final String LOG_MESSAGE_FORMAT = "## Execution took %d nanos for %s.%s using arguments %s";
    private static final FastLogger LOGGER = FastLogger.getInstance();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startNanoSeconds = System.nanoTime();
        try {
            return invocation.proceed();
        } finally {
            long durationNanos = System.nanoTime() - startNanoSeconds;
            Method method = invocation.getMethod();
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            String methodArguments = Arrays.toString(invocation.getArguments());
            String logMessage = String.format(LOG_MESSAGE_FORMAT, durationNanos, className, methodName, methodArguments);
            LOGGER.log(logMessage);

        }
    }
}