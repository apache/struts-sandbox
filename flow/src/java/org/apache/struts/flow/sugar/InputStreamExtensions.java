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
 * Adds various functions to java.io.InputStream
 * @targetClass java.io.InputStream
 */
public class InputStreamExtensions {


    /**
     *  Gets the contents of the stream as a String.
     *
     *  @funcParams 
     *  @funcReturn String
     *  @example text = inStream.getText()
     */
    public static ExtensionFunction getText(final InputStream in) {
        return new ExtensionFunction() {    
            public Object execute(Context cx, Scriptable scope, Scriptable thisObj, java.lang.Object[] args) 
                    throws IOException {
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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
}
