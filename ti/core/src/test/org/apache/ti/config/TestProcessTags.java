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
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.io.*;
import java.util.*;
import org.apache.velocity.*;

import org.apache.ti.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for the <code>org.apache.ti.config.ProcessTags</code> class.
 *
 * @version $Rev$ $Date$
 */
public class TestProcessTags extends BaseTest {
    
    File src;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestProcessTags(String theName) {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs) {
        junit.awtui.TestRunner.main(
            new String[] { TestProcessTags.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestProcessTags.class);
    }
    
    public void setUp() throws Exception {
        src = makeDir("strutsti-src");
    }
    
    public void tearDown() {
        deleteDir(src);
    }
        

    public void testCrawl() throws Exception {
        final HashSet shouldFind = new HashSet();
        shouldFind.add("Controller.java");
        shouldFind.add("foo/Controller.java");
        
       
        
        File rootCtr = new File(src, "Controller.java");
        rootCtr.createNewFile();
        File sub = new File(src, "foo");
        sub.mkdirs();
        File subCtr = new File(sub, "Controller.java");
        subCtr.createNewFile();
        
        ProcessTags processor = new ProcessTags();
        ArrayList sources = new ArrayList();
        processor.crawl(src, "Controller.java", src, null, sources);
        assertTrue("Not all files found:"+sources, new HashSet(sources).equals(shouldFind));
    }
    
}
