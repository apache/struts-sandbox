/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.struts.flow.sugar;

import org.mozilla.javascript.*;

import java.io.*;
import java.util.*;

/**
 * Adds various functions to java.io.File
 * @targetClass java.io.File
 */
public class FileExtensions {


     /**
     *  Appends text to the file.
     *
     *  @funcParams String text
     *  @funcReturn java.io.File
     *  @example file.append("added text")
     */
    public static ExtensionFunction append(final File file) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) 
                    throws IOException {
                String text = args[0].toString();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.write(text);
                writer.close();
                return file;
            }
        };
    }
    
     /**
     *  Gets the contents of the file as a String.
     *
     *  @funcParams 
     *  @funcReturn String
     *  @example text = file.getText()
     */
    public static ExtensionFunction getText(final File file) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) 
                    throws IOException {
                
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuffer answer = new StringBuffer();
                // reading the content of the file within a char buffer allow to keep the correct line endings
                char[] charBuffer = new char[4096];
                int nbCharRead = 0;
                while ((nbCharRead = reader.read(charBuffer)) != -1) {
                    // appends buffer
                    answer.append(charBuffer, 0, nbCharRead);
                }
                reader.close();
                return answer.toString();
            }
        };
    }
    
     /**
     *  Passes each line to the provided function.  The file is opened, and 
     *  interpreted as a text file using the default encoding.  Each line
     *  is read and passed to the provided function.
     *
     *  @funcParams Function func
     *  @funcReturn void
     *  @example file.eachLine(function(line) { print(line) })
     */
    public static ExtensionFunction eachLine(final File file) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) 
                    throws IOException {
                
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Function func = (Function)args[0];
                Object[] params = new Object[1];
                String line = null;
                while ((line = reader.readLine()) != null) {
                    params[0] = line;
                    func.call(cx, scope, thisObj, params);
                }
                reader.close();
                return null;
            }
        };
    }
    
     /**
     *  Collects the contents of the file as an array of lines.  The file is opened, and 
     *  interpreted as a text file using the default encoding.  
     *
     *  @funcParams 
     *  @funcReturn String[]
     *  @example linesArray = file.getLines()
     */
    public static ExtensionFunction getLines(final File file) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) 
                    throws IOException {
                
                BufferedReader reader = new BufferedReader(new FileReader(file));
                ArrayList list = new ArrayList();
                String line = null;
                
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                reader.close();
                
                Object[] lines = new Object[list.size()];
                for (int x=0; x<lines.length; x++) {
                    lines[x] = cx.javaToJS(list.get(x), scope);
                }
                return cx.newArray(scope, lines);
            }
        };
    }
    
    /**
     *  Removes a file.  Used to get around the problem of the reserved word 'delete'.
     *
     *  @funcParams 
     *  @funcReturn boolean
     *  @example isRemoved = file.remove()
     */
    public static ExtensionFunction remove(final File file) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) 
                    throws IOException {
                
                return new Boolean(file.delete());
            }
        };
    }
    
}
