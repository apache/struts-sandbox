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

    public void testGenerateSimple() {
        XDocletParser p = new XDocletParser();
        p.setTemplateName("org/apache/ti/config/test.vm");
        p.init();
        
        String src = "public class Test {}";
        StringReader reader = new StringReader(src);
        StringWriter writer = new StringWriter();
        p.generate("Test.java", reader, writer);
        
        String out = writer.toString();
        assertNotNull(out);
        assertTrue("incorrect output: '"+out+"'", "File is 'Test.java'".equals(out));
    }
    
    public void testGenerateTag() {
        XDocletParser p = new XDocletParser();
        p.setTemplateName("org/apache/ti/config/testTag.vm");
        p.init();
        
        String src = "package foo;\n/** \n * things\n *  @foo bar\n */\npublic class Test{}";
        StringReader reader = new StringReader(src);
        StringWriter writer = new StringWriter();
        p.generate("Test.java", reader, writer);
        
        String out = writer.toString();
        assertNotNull(out);
        assertTrue("incorrect output: '"+out+"'", "Tag is 'bar'".equals(out));
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
        
    
    protected void assertXPath(Document doc, String xpath) throws Exception {
        XPath xp = XPath.newInstance(xpath);
        assertNotNull(xp.selectSingleNode(doc));
    }
    
    protected Document runTemplate(String path) throws Exception {
        XDocletParser p = new XDocletParser();
        p.setTemplateName("org/apache/ti/config/xdocletToXWork.vm");
        p.init();
        
        StringWriter writer = new StringWriter();
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(path));
        path = path.replaceAll("jsrc", "java");
        p.generate(path, reader, writer);
        System.out.println("xwork: \n"+writer.toString());
        Document doc = new SAXBuilder().build(new StringReader(writer.toString()));
        return doc;
    }

}
