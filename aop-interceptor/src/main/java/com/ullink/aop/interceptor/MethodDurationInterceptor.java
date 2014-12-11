package com.ullink.aop.interceptor;

import com.ullink.duration.logging.FastLogger;
import com.ullink.performance.log.fomat.PerformanceTrendLogFormatter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodDurationInterceptor implements MethodInterceptor {

    private static final FastLogger LOGGER = FastLogger.getInstance();
    private final String tag;

    public MethodDurationInterceptor() {
        tag = System.getProperty("duration.profiler.tag");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startNanoSeconds = System.nanoTime();
        try {
            return invocation.proceed();
        } finally {
            long durationNanos = System.nanoTime() - startNanoSeconds;
            Method method = invocation.getMethod();
            String packageName = method.getDeclaringClass().getPackage().getName();
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            //TODO try to add later
//            String methodArguments = Arrays.toString(invocation.getArguments());
            String methodParameterTypes = method.getParameterTypes().toString();
            String threadName = Thread.currentThread().getName();

            String logMessage = PerformanceTrendLogFormatter.getLogLine(String.valueOf(startNanoSeconds), packageName, className, methodName, threadName, String.valueOf(durationNanos), methodParameterTypes, tag);
            LOGGER.log(logMessage);
        }
    }
}