package org.apache.ti.legacy;

import junit.framework.*;
import java.io.*;
import java.util.*;
import org.apache.commons.beanutils.*;
import com.opensymphony.xwork.ActionSupport;
import ognl.*;
import org.apache.struts.action.*;
import org.apache.struts.config.*;

/**  Description of the Class */
public class TestStrutsFactory extends TestCase {

    protected StrutsFactory factory = null;
    
    public TestStrutsFactory(String name) throws Exception {
        super(name);
    }


    public static void main(String args[]) {
        junit.textui.TestRunner.run(TestStrutsFactory.class);
    }

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
    
        factory = new StrutsFactory();
    }




    public void testConvertErrors() throws Exception {

        ActionMessage err1 = new ActionMessage("error1");
        ActionMessage err2 = new ActionMessage("error2", new Integer(1));
        ActionErrors errors = new ActionErrors();
        errors.add(errors.GLOBAL_MESSAGE, err1);
        errors.add("foo", err2);

        ActionSupport action = new ActionSupport();
        factory.convertErrors(errors, action);

        assertTrue(1 == action.getActionErrors().size());
        assertTrue(1 == action.getFieldErrors().size());
    }
}

