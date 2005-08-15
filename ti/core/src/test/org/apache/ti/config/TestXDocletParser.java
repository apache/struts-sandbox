/*
 * $Id$ 
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
 * @version $Rev$ $Date$
 */
public class TestXDocletParser extends TestCase {
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestXDocletParser(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(
            new String[] { TestXDocletParser.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestXDocletParser.class);
    }

    public void testGenerateSimple() throws Exception {
        XDocletParser p = new XDocletParser();
        p.init();
        
        StringOutputType out = new StringOutputType("org/apache/ti/config/test.vm", "foo.xml", false);
        List outputs = new ArrayList();
        outputs.add(out);

        String src = "public class Test {}";
        StringReader reader = new StringReader(src);
        
        p.generate("Test.java", reader, outputs, new File("foo"));
        
        String ut = out.getString();
        assertNotNull(ut);
        assertTrue("incorrect output: '"+ut+"'", "File is 'Test.java'".equals(ut));
    }
    
    public void testGenerateTag() throws Exception {
        XDocletParser p = new XDocletParser();
        p.init();
        
        StringOutputType out = new StringOutputType("org/apache/ti/config/testTag.vm", "foo.xml", false);
        List outputs = new ArrayList();
        outputs.add(out);

        String src = "package foo;\n/** \n * things\n *  @foo bar\n */\npublic class Test{}";
        StringReader reader = new StringReader(src);
        p.generate("Test.java", reader, outputs, new File("foo"));
        
        String ut = out.getString();
        assertNotNull(ut);
        assertTrue("incorrect output: '"+ut+"'", "Tag is 'bar'".equals(ut));
    }
}
