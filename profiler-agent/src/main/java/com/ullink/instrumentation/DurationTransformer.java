package com.ullink.instrumentation;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DurationTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className,
                            Class classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;
        if (className.contains("ullink")) { // TODO use method selector utilities instead of hardcoding
            System.out.println("Instrumenting......");
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(
                        classfileBuffer));
                CtMethod[] methods = ctClass.getDeclaredMethods();
                for (CtMethod method : methods) {
                    // TODO use fast-logger and a more unique field name + details as in MethodDurationInterceptor
                    String methodDetails = " for method " + method.getLongName();
                    method.addLocalVariable("startTime", CtClass.longType);
                    method.insertBefore("startTime = System.nanoTime();");
                    String profilerLogging = "System.out.println(\"### Duration nanos: \"+ (System.nanoTime() - startTime) " + "+ \"" +methodDetails + "\"" + ");";
                    method.insertAfter(profilerLogging);
                }
                byteCode = ctClass.toBytecode();
                ctClass.detach();
                System.out.println("Instrumentation complete.");
            } catch (Throwable ex) {
                System.out.println("Exception: " + ex);
                ex.printStackTrace();
            }
        }
        return byteCode;
    }
}