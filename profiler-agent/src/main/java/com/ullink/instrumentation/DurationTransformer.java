package com.ullink.instrumentation;

import com.ullink.duration.logging.FastLogger;
import com.ullink.method.selector.manager.MethodFilterManager;
import com.ullink.method.selector.manager.file.CSVFilterManagerBuilder;
import com.ullink.performance.log.fomat.PerformanceTrendLogFormatter;
import javassist.*;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import static com.ullink.performance.log.fomat.PerformanceTrendLogFormatter.*;

public class DurationTransformer implements ClassFileTransformer
{
    /**
     * e.g. EDMA: com.ullink.ulbridge.plugins
     * e.g. only main EDMA entry class and classes from the same package: com.ullink.ulbridge.plugins.edma
     * e.g. SMART: com.ullink.ulbridge2.modules.bee
     * e.g. both EDMA and SMART: com.ullink.ulbridge
     */
    private static final String START_TIME_VAR_NAME           = "PT_$tArTtImE";
    private static final String CURRENT_TIME_MILLIS           = ESCAPED_QUOTES + " + System.currentTimeMillis() + " + ESCAPED_QUOTES;
    private static final String THREAD_NAME                   = ESCAPED_QUOTES + " + Thread.currentThread().getName() + " + ESCAPED_QUOTES;
    private static final String DURATION                      = ESCAPED_QUOTES + " + java.util.concurrent.TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - " + START_TIME_VAR_NAME + ") + " + ESCAPED_QUOTES;
    private final String        tag;

    private MethodFilterManager methodFilterManager;

    public DurationTransformer(String csvFilePath, String tag) throws FileNotFoundException, IOException
    {
        if (tag != null)
        {
            this.tag = tag;
        }
        else
        {
            this.tag = "NOTAG";
        }
        if (csvFilePath != null)
        {
            this.methodFilterManager = CSVFilterManagerBuilder.fromFileName(csvFilePath).build();
        }
        else
        {
            this.methodFilterManager = null;
        }
    }

    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException
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

    private byte[] getInstrumentedBytes(byte[] originalClassBytes) throws IOException, CannotCompileException, NotFoundException
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
                String methodParams = "";
                /* TODO use method-selector utilities instead of this method! */
                if (isInstrumentationEnabledForMethod(packageName, className, methodName))
                {
                    method.addLocalVariable(START_TIME_VAR_NAME, CtClass.longType);
                    method.insertBefore(START_TIME_VAR_NAME + " = System.nanoTime();");
                    for (CtClass param : method.getParameterTypes())
                    {
                        methodParams += param.getName() + ",";
                    }
                    methodParams = methodParams.substring(0,methodParams.length()-1);
                    String profilerLogging = PerformanceTrendLogFormatter.getLogLine(CURRENT_TIME_MILLIS, packageName, className, methodName, THREAD_NAME, DURATION, methodParams, tag);
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
        if (methodFilterManager == null)
        {
            return true;
        }
        return methodFilterManager.isMethodAllowed(packageName, className, methodName);
    }

}
