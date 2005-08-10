/*
 * $Id: XDocletTestBase.java 230394 2005-08-05 04:13:44Z martinc $ 
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
import java.io.*;
import java.util.*;
import org.apache.velocity.*;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * Unit tests for the <code>org.apache.ti.config.XDocletvalidations</code> class.
 *
 * @version $Rev: 230394 $ $Date: 2005-08-04 21:13:44 -0700 (Thu, 04 Aug 2005) $
 */
public class XDocletTestBase extends TestCase {
        
    private XDocletParser p; 
    
    public void setUp() {
        p = new XDocletParser();
        p.init();
    }


    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public XDocletTestBase(String theName) {
        super(theName);
    }
      
    
    protected void assertXPath(Document doc, String xpath) throws Exception {
        XPath xp = XPath.newInstance(xpath);
        assertNotNull(xp.selectSingleNode(doc));
    }
    
    protected Document runTemplate(String path, String template) throws Exception {
        return runTemplate(path, template, false, null);
    }    
    
    protected Document runTemplate(String path, String template, boolean perAction, String actionName) throws Exception {
        StringOutputType output = new StringOutputType(template, "foo", perAction);
        ArrayList list = new ArrayList();
        list.add(output);
        
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(path));
        path = path.replaceAll("jsrc", "java");
        p.generate(path, reader, list, null);
        
        String out = null;
        if (perAction) {
            out = output.getString(actionName);
            //System.out.println("output:"+out); 
        } else {
            out = output.getString();
            //System.out.println("output:"+out);
        }    
        
        Document doc = new SAXBuilder().build(new StringReader(out));
        return doc;
    }

}
