package com.ullink.method.selector.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.ullink.medhod.selector.manager.MethodFilterManager;
import com.ullink.medhod.selector.manager.file.CSVFilterManagerBuilder;

public class TestDefaultMethodFilterManager
{
    private MethodFilterManager methodFilterManager;

    @Before
    public void setUp() throws FileNotFoundException, IOException
    {
        URL csvURl = TestDefaultMethodFilterManager.class.getResource("resources/rules.csv");
        this.methodFilterManager = CSVFilterManagerBuilder.fromFileName(csvURl.getFile()).build();
    }

    @Test
    public void accceptPackages()
    {
        Assert.assertTrue(this.methodFilterManager.isPackageAllowed("A.B.C"));
        Assert.assertTrue(this.methodFilterManager.isPackageAllowed("A.B.C.D"));
        Assert.assertFalse(this.methodFilterManager.isPackageAllowed("A.B.C.E"));
    }
    
    @Test
    public void accceptSubPackages()
    {
        Assert.assertTrue(this.methodFilterManager.isPackageAllowed("A.B.C.D.X"));
        Assert.assertFalse(this.methodFilterManager.isPackageAllowed("A.B.C.E.X"));
    }
    
    public void acceptClasses(){
        
    }
}
