/*
 * $Id: XDocletParser.java 230400 2005-08-05 05:13:54Z martinc $
 *
 * Copyright 2005 The Apache Software Foundation.
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Processes xdoclet-style tags and uses a velocity template to generate
 *  content.
 */
public class OutputType {

	/**
	 * FIX ME
	 */
    public static final int PER_ACTION = 0;
    
    /**
     * FIX ME
     */
    public static final int PER_CONTROLLER = 1;
    
    /**
     * FIX ME
     */
    public static final int ONCE = 3;

    private String filePattern;
    private int frequency;
    private String template;

    private static final Log log = LogFactory.getLog(OutputType.class);

    /**
     * FIX ME
     * @param template
     * @param filePattern
     * @param frequency
     */
    public OutputType(String template, String filePattern, int frequency) {
        this.template = template;
        this.filePattern = filePattern;
        this.frequency = frequency;
    }

    /**
     * Get the frequency
     * @return Return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Get the template
     * @return Return the template
     */
    public String getTemplate() {
        return template;
    }    

    /**
     * FIX ME
     * @param dest
     * @param path
     * @param actionName
     * @return FIX ME
     */
    public Writer getWriter(File dest, String path, String actionName) {
        
        FileWriter writer = null;
        String name = filePattern;
        if (frequency != ONCE) {
        
            // Strip the extension
            path = path.substring(0, path.lastIndexOf('.'));
            
            // Determine the root path w/o the class name
            String rootPath = path.substring(0, path.lastIndexOf('/') + 1);
            
            // Replace the class name
            int i = filePattern.indexOf("$c");
            if (i > -1) {
                String className = path.substring(path.lastIndexOf('/') + 1);
                name = name.substring(0, i) + className + name.substring(i+2);
            }
    
            // Replace the action name
            i = name.indexOf("$a");
            if (i > -1) {
                name = name.substring(0, i) + actionName + name.substring(i+2);
            }
            dest = new File(dest, rootPath);
        }    
        try {
            writer = new FileWriter(new File(dest, name));
        } catch (IOException ex) {
            log.error("Unable to create output file "+name, ex);
        }
        return writer;
    }    
}
