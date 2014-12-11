package com.ullink.method.selector.manager;
public interface MethodFilterManager
{
    boolean isPackageAllowed(String packageName);
   
    boolean isClasssAllowed(String packageName, String className);
    
    boolean isMethodAllowed(String packageName, String className, String metodName);
    
}
