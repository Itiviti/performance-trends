package com.ullink.instrumentation;

import com.ullink.duration.logging.FastLogger;
import com.ullink.performance.log.fomat.PerformanceTrendLogFormatter;
import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DurationTransformer implements ClassFileTransformer
{
    private static final String LOGGER_START         = "com.ullink.duration.logging.FastLogger.getInstance().log(";
    private static final String LOGGER_END           = ");";
    private static final String ESCAPED_QUOTES       = "\"";
    private static final String ESCAPED_SEPARATOR = ESCAPED_QUOTES + PerformanceTrendLogFormatter.LOG_SECTION_SEPARATOR + ESCAPED_QUOTES;
    private static final String LOG_MESSAGE_TEMPLATE = "+" + ESCAPED_QUOTES + PerformanceTrendLogFormatter.LOG_MESSAGE_FORMAT + ESCAPED_QUOTES;

    /**
     * e.g. EDMA: com.ullink.ulbridge.plugins
     * e.g. only main EDMA entry class and classes from the same package: com.ullink.ulbridge.plugins.edma
     * e.g. SMART: com.ullink.ulbridge2.modules.bee
     * e.g. both EDMA and SMART: com.ullink.ulbridge
     */
    private static final String HARD_CODED_PACKAGE_TO_PROFILE = "com.ullink";
    private static final String START_TIME_VAR_NAME = "PT_$tArTtImE";

    public byte[] transform(ClassLoader loader, String className,
        Class classBeingRedefined, ProtectionDomain protectionDomain,
        byte[] classFileBuffer) throws IllegalClassFormatException
    {

        byte[] instrumentedBytes = null;

        /* Skipping inner classes and also avoid instrumenting FastLogger itself (that would lead to stack overflow) */
        if (!className.contains(FastLogger.class.getSimpleName()) && !className.contains("$"))
        {
            try
            {
                instrumentedBytes = getInstrumentedBytes(classFileBuffer);
            }
            catch (Exception e)
            {
                System.err.println("Exception while transforming class (using thus original bytecode): " + className + ", exception is: " + e);
            }
        }
        if (instrumentedBytes != null)
        {
            return instrumentedBytes;
        }
        else
        {
            return classFileBuffer;
        }
    }

    private byte[] getInstrumentedBytes(byte[] originalClassBytes) throws IOException, CannotCompileException
    {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(originalClassBytes));
        CtMethod[] methods = ctClass.getDeclaredMethods();
        boolean isInstrumented = false;
        for (CtMethod method : methods)
        {
            int methodModifiers = method.getModifiers();
            if (Modifier.isPublic(methodModifiers) && !Modifier.isNative(methodModifiers) && !Modifier.isAbstract(methodModifiers))
            {
                CtClass instrumentedClass = method.getDeclaringClass();
                String packageName = instrumentedClass.getPackageName();
                String className = instrumentedClass.getSimpleName();
                String methodName = method.getName();
                /* TODO use method-selector utilities instead of this method! */
                if (isInstrumentationEnabledForMethod(packageName, className, methodName))
                {
                    method.addLocalVariable(START_TIME_VAR_NAME, CtClass.longType);
                    method.insertBefore(START_TIME_VAR_NAME + " = System.nanoTime();");
                    String profilerLogging = createDurationLogLine(packageName, className, methodName);
                    method.insertAfter(profilerLogging);
                    isInstrumented = true;
                }
            }
        }
        if (isInstrumented)
        {
            byte[] instrumentedClassBytes = ctClass.toBytecode();
            ctClass.detach();
            return instrumentedClassBytes;
        }
        else
        {
            return originalClassBytes;
        }
    }

    private boolean isInstrumentationEnabledForMethod(String packageName, String className, String methodName)
    {
        return packageName.startsWith(HARD_CODED_PACKAGE_TO_PROFILE);
    }

    private String createDurationLogLine(String packageName, String className, String methodName)
    {
        /* TODO: refactor this whole thing using String formatter. Take into consideration tha the same log-formatter will also be used by interceptors! */
        /*@Language("JAVA")*/
        return String.format(LOGGER_START +
            "System.currentTimeMillis() + " + ESCAPED_SEPARATOR +
            LOG_MESSAGE_TEMPLATE +
            " + Thread.currentThread().getName() + " + ESCAPED_SEPARATOR +
            " + java.util.concurrent.TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - " + START_TIME_VAR_NAME + ") " +
            LOGGER_END,
            packageName, className, methodName);
    }
}