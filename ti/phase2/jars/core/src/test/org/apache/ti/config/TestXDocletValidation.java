/*
 * $Id: TestXDocletValidation.java 230394 2005-08-05 04:13:44Z martinc $ 
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jdom.Document;


/**
 * Unit tests for the <code>org.apache.ti.config.XDocletParser</code> class.
 *
 * @version $Rev: 230394 $ $Date: 2005-08-04 21:13:44 -0700 (Thu, 04 Aug 2005) $
 */
public class TestXDocletValidation extends XDocletTestBase {
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestXDocletValidation(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(
            new String[] { TestXDocletValidation.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestXDocletValidation.class);
    }
   
    public void testValidation() throws Exception {
        Document doc = runTemplate("Controller.jsrc", "doLogin");
        assertXPath(doc, "/validators/field[@name='name']");
        assertXPath(doc, "/validators/field[@name='name']/field-validator[@type='required']");
        assertXPath(doc, "/validators/field[@name='name']/field-validator[@type='required']/message='Name is required'");
    }
   
    
    protected Document runTemplate(String path, String action) throws Exception {
        return runTemplate(path, "org/apache/ti/config/xdocletToValidation.vm", OutputType.PER_ACTION, action);
    }

}
