package org.apache.ti.legacy;

import junit.framework.*;
import java.io.*;
import java.util.*;
import org.apache.commons.beanutils.*;
import com.opensymphony.xwork.ObjectFactory;
import ognl.*;

/**  Description of the Class */
public class TestScopedModelDrivenInterceptor extends TestCase {

    protected ScopedModelDrivenInterceptor inter = null;
    
    public TestScopedModelDrivenInterceptor(String name) throws Exception {
        super(name);
    }


    public static void main(String args[]) {
        junit.textui.TestRunner.run(TestScopedModelDrivenInterceptor.class);
    }

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
    
        inter = new ScopedModelDrivenInterceptor();
    }




    public void testResolveModel() throws Exception {
        ObjectFactory factory = ObjectFactory.getObjectFactory();
        Object obj = inter.resolveModel(factory, null, "java.lang.String", "request", null);
        assertNotNull(obj);
        assertTrue(obj instanceof String);

        HashMap session = new HashMap();
        obj = inter.resolveModel(factory, session, "java.lang.String", "request", "foo");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        assertTrue(obj == session.get("foo"));

        obj = inter.resolveModel(factory, session, "java.lang.String", "request", "foo");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        assertTrue(obj == session.get("foo"));
    }
}

