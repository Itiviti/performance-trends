package com.ullink.instrumentation;

import com.ullink.duration.logging.FastLogger;
import com.ullink.performance.log.fomat.PerformanceTrendLogFormatter;
import javassist.*;
import javassist.bytecode.MethodInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DurationTransformer implements ClassFileTransformer
{

    private static final String LOGGER_START = "com.ullink.duration.logging.FastLogger.getInstance().log(";
    private static final String LOGGER_END = ");";
    private static final String ESCAPED_QUOTES = "\"";
    private static final String LOG_MESSAGE_TEMPLATE = ESCAPED_QUOTES + PerformanceTrendLogFormatter.LOG_MESSAGE_FORMAT + ESCAPED_QUOTES;

    public byte[] transform(ClassLoader loader, String className,
        Class classBeingRedefined, ProtectionDomain protectionDomain,
        byte[] classFileBuffer) throws IllegalClassFormatException
    {
        byte[] instrumentedBytes = null;

        /* TODO use class selector utilities instead of hard-coding. Second condition avoids stackoverflow / infinite loop! */
        // e.g. EDMA: "com/ullink/ulbridge/plugins";
        // e.g. SMART: "com/ullink/ulbridge2/modules/bee";
        // e.g. both EDMA and SMART: "com/ullink/ulbridge"
        // e.g. only main EDMA entry class: com/ullink/ulbridge/plugins/edma/EnhancedDMA
        String profiledPackage = "ullink";
        if (className.contains(profiledPackage) && !className.contains(FastLogger.class.getSimpleName()) && !className.contains("$"))
        {
            try
            {
                //System.out.println("Instrumenting class: " + className); // TODO only log this while experimenting!
                instrumentedBytes = getInstrumentedBytes(classFileBuffer);
                //System.out.println("Successfully instrumented class: " + className); // TODO only log this while experimenting!
            } catch (Exception e)
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

    private byte[] getInstrumentedBytes(byte[] originalBytes) throws IOException, CannotCompileException
    {
        byte[] instrumentedBytes = null;
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(
            originalBytes));
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for (CtMethod method : methods)
        {
            int methodModifiers = method.getModifiers();
            /* TODO use method-selector utilities instead of hard-coding. */
            if (Modifier.isPublic(methodModifiers) && !Modifier.isNative(methodModifiers) && !Modifier.isAbstract(methodModifiers))
            {
                //System.out.println("Instrumenting method: " + method.getLongName()); // TODO only log this while experimenting!
                method.addLocalVariable("startTime", CtClass.longType);
                method.insertBefore("startTime = System.nanoTime();");
                String profilerLogging = createDurationLogLine(method);
                method.insertAfter(profilerLogging);
                //System.out.println("Instrumentation complete for: " + method.getLongName()); // TODO only log this while experimenting!
            }
        }
        instrumentedBytes = ctClass.toBytecode();
        ctClass.detach();
        return instrumentedBytes;
    }

    private String createDurationLogLine(CtMethod method) {
        /* TODO: use log-formatter utilities (use the same log format in both agent and interceptors) */

        CtClass instrumentedClass = method.getDeclaringClass();
        String packageName = instrumentedClass.getPackageName();
        String className = instrumentedClass.getSimpleName();
        String methodName = method.getName();
        String threadName = Thread.currentThread().getName(); // TODO: use actual thread name not the transformer's!
        /* TODO:  off course this won't stay like this, just tested the integration with logstash */
        String loggerLine = String.format(LOGGER_START + "System.currentTimeMillis() + " + ESCAPED_QUOTES + PerformanceTrendLogFormatter.LOG_SECTION_SEPARATOR + ESCAPED_QUOTES + LOG_MESSAGE_TEMPLATE, packageName, className, methodName, threadName) + " + (System.nanoTime() - startTime) " + LOGGER_END;
        return loggerLine;
    }
}
