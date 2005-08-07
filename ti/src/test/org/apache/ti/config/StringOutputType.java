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

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;

import xjavadoc.XClass;
import xjavadoc.XJavaDoc;
import xjavadoc.filesystem.ReaderFile;

/**
 *  Processes xdoclet-style tags and uses a velocity template to generate
 *  content.
 */
public class StringOutputType extends OutputType {

    private Map writers = new HashMap();

    public StringOutputType(String template, String filePattern, boolean perAction) {
        super(template, filePattern, perAction);
    }

    public Writer getWriter(File dest, String path, String actionName) {
        StringWriter writer = new StringWriter();
        if (actionName != null) {
            writers.put(actionName, writer);
        } else {
            writers.put(path, writer);
        }    
        return writer;
    }

    public String getString() {
        return ((StringWriter)writers.values().iterator().next()).toString();
    }    

    public String getString(String key) {
        return ((StringWriter)writers.get(key)).toString();
    }    

    public Map getWriters() {
        return writers;
    }    
    
}
