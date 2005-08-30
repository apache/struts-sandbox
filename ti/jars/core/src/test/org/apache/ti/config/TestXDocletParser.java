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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.ti.util.VelocityTemplateProcessor;

/**
 * Unit tests for the <code>org.apache.ti.config.XDocletParser</code> class.
 *
 * @version $Rev$ $Date$
 */
public class TestXDocletParser extends XDocletTestBase {
    
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
        VelocityTemplateProcessor proc = new VelocityTemplateProcessor();
        proc.init();
        p.setTemplateProcessor(proc);
        
        StringOutputType out = new StringOutputType("org/apache/ti/config/test.vm");
        List outputs = new ArrayList();
        outputs.add(out);

        String txt = "public class Test {}";
        setText(new File(src, "Test.java"), txt);
        ArrayList sources = new ArrayList();
        sources.add("Test.java");
        p.generate(sources, src, null, outputs);
        
        String ut = out.getString();
        assertNotNull(ut);
        assertTrue("incorrect output: '"+ut+"'", "File is 'Test.java'".equals(ut));
    }
    
    public void testGenerateTag() throws Exception {
        XDocletParser p = new XDocletParser();
        VelocityTemplateProcessor proc = new VelocityTemplateProcessor();
        proc.init();
        p.setTemplateProcessor(proc);
        
        StringOutputType out = new StringOutputType("org/apache/ti/config/testTag.vm");
        List outputs = new ArrayList();
        outputs.add(out);

        String srctxt = "package foo;\n/** \n * things\n *  @foo bar\n */\npublic class Test{}";
        setText(new File(src, "Test.java"), srctxt);
        ArrayList sources = new ArrayList();
        sources.add("Test.java");
        p.generate(sources, src, null, outputs);
        
        String ut = out.getString();
        assertNotNull(ut);
        assertTrue("incorrect output: '"+ut+"'", "Tag is 'bar'".equals(ut));
    }
}
