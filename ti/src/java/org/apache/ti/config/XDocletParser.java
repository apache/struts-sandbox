/*
 * $Id$
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

import java.io.Reader;
import java.io.Writer;
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
public class XDocletParser {

    private String templateName = "org/apache/ti/config/xdocletToXWork.vm";
    private Template template;
    private Map parameters;
    private static final Log log = LogFactory.getLog(XDocletParser.class);
    
    public void init() {
        VelocityEngine velocity = new VelocityEngine();
        
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("velocity.properties"));
            velocity.init(props);
            template = velocity.getTemplate(templateName);
        } catch (ResourceNotFoundException ex) {
            log.error("Unable to locate template to process javadoc tags", ex);
        }catch (Exception ex) {
            log.error("Unable to intialize velocity", ex);
        }    
        
    }
    
    public void setParameters(Map map) {
        this.parameters = map;
    }
    
    public Map getParameters() {
        return parameters;
    }
        
    public void setTemplateName(String name) {
        this.templateName = name;
    }
    
    public void generate(String name, Reader reader, Writer writer) {
        XJavaDoc jdoc = new XJavaDoc();
        ReaderFile file = new ReaderFile(reader);
        
        
        String className = name.replace('/', '.');
        className = className.replace('\\', '.');
        className = className.substring(0, className.indexOf(".java"));
        
        jdoc.addAbstractFile(className, file);
        XClass xclass = jdoc.getXClass(className);
        
        Map contextMap = new HashMap();
        if (parameters != null) {
            contextMap.putAll(parameters);
        }    
        VelocityContext context = new VelocityContext(contextMap);
        context.put("xclass", xclass);
        context.put("javaFile", name);
        
        try {
            template.merge(context, writer);
        } catch (Exception ex) {
            log.error("Unable to generate javadoc output", ex);
        }    
    }
}   
