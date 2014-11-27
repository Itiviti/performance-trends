package com.ullink.instrumentation;

import com.ullink.duration.logging.FastLogger;
import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DurationTransformer implements ClassFileTransformer
{
    private static final Logger LOGGER = Logger.getLogger(DurationTransformer.class.getName());

    public byte[] transform(ClassLoader loader, String className,
        Class classBeingRedefined, ProtectionDomain protectionDomain,
        byte[] classFileBuffer) throws IllegalClassFormatException
    {
        byte[] instrumentedBytes = null;

        /* TODO use class selector utilities instead of hard-coding. Second condition avoids stackoverflow / infinite loop! */
        String profiledPackage = "com/ullink/ulbridge"; // e.g. "com/ullink/ulbridge/plugins";
        if (className.contains(profiledPackage) && !className.contains(FastLogger.class.getSimpleName()) && !className.contains("$"))
        {
            try
            {
                //LOGGER.info("Instrumenting class: " + className); // TODO only log this while experimenting!
                instrumentedBytes = getInstrumentedBytes(classFileBuffer);
                //LOGGER.info("Successfully instrumented class: " + className); // TODO only log this while experimenting!
            }
            catch (Throwable e)
            {
                LOGGER.log(Level.WARNING, "Exception: " + e);
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
            /* TODO use method selector utilities instead of hard-coding. */
            if (Modifier.isPublic(methodModifiers) && !Modifier.isNative(methodModifiers) && !Modifier.isAbstract(methodModifiers))
            {
                //LOGGER.info("Instrumenting method: " + method.getLongName()); // TODO only log this while experimenting!
                method.addLocalVariable("startTime", CtClass.longType);
                method.insertBefore("startTime = System.nanoTime();");
                String profilerLogging = createDurationLogLine(method);
                method.insertAfter(profilerLogging);
                //LOGGER.info("Instrumentation complete for: " + method.getLongName()); // TODO only log this while experimenting!
            }
        }
        instrumentedBytes = ctClass.toBytecode();
        ctClass.detach();
        return instrumentedBytes;
    }

    private String createDurationLogLine(CtMethod method)
    {
        String methodDetails = " for method " + method.getLongName();
        return "com.ullink.duration.logging.FastLogger.getInstance().log(\"### Duration nanos: \"+ (System.nanoTime() - startTime) " + "+ \"" + methodDetails + "\"" + ");";
    }
}