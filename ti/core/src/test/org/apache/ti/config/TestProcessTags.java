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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for the <code>org.apache.ti.config.ProcessTags</code> class.
 *
 * @version $Rev$ $Date$
 */
public class TestProcessTags extends TestCase {
    
    File src, dest;
    
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
        File root = File.createTempFile("strutsti", "").getParentFile();
        src = new File(root, "strutsti-src");
        src.mkdirs();
        dest = new File(root, "strutsti-dest");
        dest.mkdirs();
    }
    
    public void tearDown() {
        deleteDir(src);
        deleteDir(dest);
    }
        

    public void testCrawl() throws Exception {
        final HashSet shouldFind = new HashSet();
        shouldFind.add("Controller.java");
        shouldFind.add("foo/Controller.java");
        shouldFind.add("foo\\Controller.java");
        
        XDocletParser mock = new XDocletParser() {
            public void generate(String name, Reader reader, File dest, List outputs) {
                if (!shouldFind.contains(name)) {
                    fail("Invalid controller file "+name);
                }
            }
        };
       
        /*
        File rootCtr = new File(src, "Controller.java");
        rootCtr.createNewFile();
        File sub = new File(src, "foo");
        sub.mkdirs();
        File subCtr = new File(sub, "Controller.java");
        subCtr.createNewFile();
        
        ProcessTags processer = new ProcessTags();
        processer.setXDocletParser(mock);
        
        processer.process(src, "Controller.java", dest, "xwork.xml");
        assertTrue(new File(dest, "xwork.xml").exists());
        assertTrue(new File(dest, "foo/xwork.xml").exists());
        */
    }
    
    
    
    
    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }

}
