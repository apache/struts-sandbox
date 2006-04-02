/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow.sugar;

import java.io.*;
import java.util.*;
import org.mozilla.javascript.*;

/**
 *  Driver class for all the Javascript-based unit tests
 */
public class JavascriptSugarTest {
    
    /** 
     *  Driver main method for running tests
     *
     * @param args Array of command line arguments.  First argument should be a
     *             path to the directory where "assert.js" and unit test Javascript
     *             scripts (*Test.js) can be found.
     */
    public static void main(String args[]) throws Exception {
        
        File baseDir = new File(args[0]);
                
        String assertCode = readAll(new File(baseDir, "assert.js"));
        File[] kids = baseDir.listFiles();
        for (int x=0; x<kids.length; x++) {
            if (kids[x].getName().endsWith("Test.js")) {
                // Creates and enters a Context. The Context stores information
                // about the execution environment of a script.
                Context cx = Context.enter();
                try {
                    cx.setWrapFactory(new SugarWrapFactory());
                    // Initialize the standard objects (Object, Function, etc.)
                    // This must be done before scripts can be executed. Returns
                    // a scope object that we use in later calls.
                    Scriptable scope = cx.initStandardObjects();
        
                    // Now evaluate the string we've colected.
                    String code = readAll(kids[x]);
                    StringReader reader = new StringReader(code + "\n" + assertCode + "\nrunTests('"+kids[x].getName()+"');");
                    Object result = cx.evaluateReader(scope, reader, kids[x].getName(), 1, null);
        
                    // Convert the result to a string and print it.
                    //System.err.println(cx.toString(result));
                } finally {
                    // Exit from the context.
                    Context.exit();
                }   
            }
        }
    }
    
    private static String readAll(File file) throws IOException {
        StringBuffer sb = new StringBuffer();
        FileReader reader = new FileReader(file);
        char[] buffer = new char[4096];
        int len = 0;
        while ((len = reader.read(buffer, 0, buffer.length)) > -1) {
            sb.append(buffer, 0, len);
        }
        return sb.toString();
    }

}

