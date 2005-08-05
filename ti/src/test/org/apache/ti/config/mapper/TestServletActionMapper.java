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

package org.apache.ti.config.mapper;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for the <code>org.apache.ti.config.ServletActionMapper</code> class.
 *
 * @version $Rev$ $Date$
 */
public class TestServletActionMapper extends TestCase {
    
    List mappings;
    ServletActionMapper mapper;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestServletActionMapper(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(
            new String[] { TestServletActionMapper.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestServletActionMapper.class);
    }
    
    public void setUp() {
        mappings = new ArrayList();
        mappings.add("*.do");
        mappings.add("/foo/*");
        
        mapper = new ServletActionMapper();
    }
    
    public void testGetPrefixMapping() throws Exception {
        ActionMapping am = mapper.getMapping("/foo/ns/bar", mappings);
        assertNotNull(am);
        assertTrue("action:"+am.getName(), "bar".equals(am.getName()));
        assertTrue("ns".equals(am.getNamespace()));
        assertTrue("/foo/*".equals(am.getExternalMapping()));
    }
    
    public void testGetExtMapping() throws Exception {
        ActionMapping am = mapper.getMapping("/ns/bar.do", mappings);
        assertNotNull(am);
        assertTrue("bar".equals(am.getName()));
        assertTrue("ns".equals(am.getNamespace()));
        assertTrue("*.do".equals(am.getExternalMapping()));
    }
    
    public void testGetNoNSMapping() throws Exception {
        ActionMapping am = mapper.getMapping("/bar.do", mappings);
        assertNotNull(am);
        assertTrue("bar".equals(am.getName()));
        assertTrue("".equals(am.getNamespace()));
        assertTrue("*.do".equals(am.getExternalMapping()));
    }
    
    public void testGetNoMapping() throws Exception {
        ActionMapping am = mapper.getMapping("/ns/bar.doasd", mappings);
        assertNull(am);
    }
    
    public void testGetPrefixUri() throws Exception {
        ActionMapping am = new ActionMapping("bar", "ns", "/foo/*", null);
        String uri = mapper.getUriFromActionMapping(am);
        assertTrue("Invalid uri is "+uri, "/foo/ns/bar".equals(uri));
    }
    
    public void testGetExtUri() throws Exception {
        ActionMapping am = new ActionMapping("bar", "ns", "*.do", null);
        String uri = mapper.getUriFromActionMapping(am);
        assertTrue("Invalid uri is "+uri, "/ns/bar.do".equals(uri));
    }
    
}
