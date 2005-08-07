/*
 * $Id: TestXDocletXWork.java 230394 2005-08-05 04:13:44Z martinc $ 
 *
 * Copyright 2002-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ti.config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.*;
import java.util.*;
import org.apache.velocity.*;


import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * Unit tests for the <code>org.apache.ti.config.XDocletParser</code> class.
 *
 * @version $Rev: 230394 $ $Date: 2005-08-04 21:13:44 -0700 (Thu, 04 Aug 2005) $
 */
public class TestXDocletXWork extends XDocletTestBase {
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestXDocletXWork(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(
            new String[] { TestXDocletXWork.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestXDocletXWork.class);
    }
   
    public void testForwards() throws Exception {
        Document doc = runTemplate("Controller.jsrc");
        assertXPath(doc, "/xwork/package[@name='default']");
        
        // Test simple forward
        assertXPath(doc, "/xwork/package/action[@name='index']");
        assertXPath(doc, "/xwork/package/action[@name='index']/result[param = 'index.jsp']");
        
        // Test declared forwards
        assertXPath(doc, "/xwork/package/action[@name='doLogin']");
        assertXPath(doc, "/xwork/package/action[@name='doLogin']/result[@name='success' and param='index']");
        assertXPath(doc, "/xwork/package/action[@name='doLogin']/result[@name='lost' and param='lostPassword.jsp']");
        assertXPath(doc, "/xwork/package/action[@name='doLogin']/result[@name='error' and param='login']");
        
    }
   
    public void testInPackage() throws Exception {
        Document doc = runTemplate("foo/Controller.jsrc");
        assertXPath(doc, "/xwork/package[@name='foo' and @namespace='/foo']");
    }
        
    
    protected Document runTemplate(String path) throws Exception {
        return runTemplate(path, "org/apache/ti/config/xdocletToXWork.vm");
    }

}
