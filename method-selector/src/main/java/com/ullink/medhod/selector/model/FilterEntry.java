package com.ullink.medhod.selector.model;

public class FilterEntry
{
    private final String packageName;  
    private final String className;
    private final String methodName;
    private final boolean visible;
    


    public FilterEntry(String packageName, String className, String methodName, boolean visible)
    {
        super();
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.visible = visible;
    }

    public String getPackageName()
    {
        return packageName;
    }
   
    public String getClassName()
    {
        return className;
    }
    
    public String getMethodName()
    {
        return methodName;
    }
    
   public boolean isVisible()
   {
       return visible;
   }
}
