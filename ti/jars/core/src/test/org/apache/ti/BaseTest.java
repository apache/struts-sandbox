/*
 * $Id: BaseTest.java 230619 2005-08-07 02:04:31Z mrdon $ 
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

package org.apache.ti;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * Unit tests for the <code>org.apache.ti.config.ProcessTags</code> class.
 *
 * @version $Rev: 230619 $ $Date: 2005-08-06 19:04:31 -0700 (Sat, 06 Aug 2005) $
 */
public abstract class BaseTest extends TestCase {
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public BaseTest(String theName) {
        super(theName);
    }

 
    protected static File makeDir(String name) throws Exception {
        File root = File.createTempFile("strutsti", "").getParentFile();
        File src = new File(root, name);
        src.mkdirs();
        return src;
    }
    
    protected static void setText(File file, String text) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(text);
        writer.close();
    }
    
    protected static void setText(File file, InputStream in) throws IOException {
        
        byte[] b = new byte[4096];
        int len = 0;
        FileOutputStream fout = new FileOutputStream(file);
        while ((len = in.read(b)) > 0) {
            fout.write(b, 0, len);
        }
        in.close();
        fout.close();
    }
    
    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    protected static boolean deleteDir(File dir) {
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
