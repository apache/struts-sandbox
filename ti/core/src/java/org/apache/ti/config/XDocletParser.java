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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xjavadoc.XClass;
import xjavadoc.XMethod;
import xjavadoc.XJavaDoc;
import xjavadoc.filesystem.ReaderFile;
import xjavadoc.filesystem.XJavadocFile;

import org.apache.ti.util.*;

/**
 *  Processes xdoclet-style tags and uses a velocity template to generate
 *  content.  This class is not thread-safe.
 */
public class XDocletParser {

    private Map parameters;
    private TemplateProcessor processor;
    private static final Log log = LogFactory.getLog(XDocletParser.class);

    public void setTemplateProcessor(TemplateProcessor p) {
        this.processor = p;
    }
    
    public void setParameters(Map map) {
        this.parameters = map;
    }

    public Map getParameters() {
        return parameters;
    }

    protected String getClassName(String uri) {
        String className = uri.replace('/', '.');
        className = className.replace('\\', '.');
        className = className.substring(0, className.indexOf(".java"));
        return className;
    }    


    public void generate(List sources, File srcRoot, File destRoot, List outputs) {
        XJavaDoc jdoc = new XJavaDoc();
        String source, className;
        XJavadocFile file;
        for (Iterator i = sources.iterator(); i.hasNext(); ) {
            source = (String)i.next();
            file = new XJavadocFile(new File(srcRoot, source));
            className = getClassName(source);
            jdoc.addAbstractFile(className, file);
        }  
 
        Map context = new HashMap();
        if (parameters != null) {
            context.putAll(parameters);
        }
        
        OutputType output;
        XClass xclass;
        for (Iterator o = outputs.iterator(); o.hasNext(); ) {
            output = (OutputType)o.next();
            if (output.getFrequency() == output.ONCE) {
                generateOnce(sources, destRoot, jdoc, output, context);
            } else {
                for (Iterator i = sources.iterator(); i.hasNext(); ) {
                    source = (String)i.next();
                    xclass = jdoc.getXClass(getClassName(source));
                   
                    if (output.getFrequency() == output.PER_CONTROLLER) {
                        generatePerController(source, destRoot, xclass, output, context);
                    } else if (output.getFrequency() == output.PER_ACTION) {
                        generatePerAction(source, destRoot, xclass, output, context);
                    }
                }    
            }
        }    
    }

    protected void generateOnce(List sources, File destRoot, XJavaDoc jdoc, 
                                OutputType output, Map context) {
        String source, className;
        XClass xclass;
        Map xclasses = new HashMap();
        for (Iterator i = sources.iterator(); i.hasNext(); ) {
            source = (String)i.next();
            className = getClassName(source);
            xclass = jdoc.getXClass(className);
            xclasses.put(source, xclass);
        }    
        Writer writer = output.getWriter(destRoot, null, null);
        context.put("xclasses", xclasses);
        processor.process(output.getTemplate(), context, writer);
    }



    protected void generatePerController(String source, File destRoot, XClass xclass, 
                                         OutputType output, Map context) {
        Writer writer = output.getWriter(destRoot, source, null);
        context.put("xclass", xclass);
        context.put("javaFile", source);
        processor.process(output.getTemplate(), context, writer);
    }


    protected void generatePerAction(String source, File destRoot, XClass xclass, 
                                     OutputType output, Map context) {
        List methods = xclass.getMethods();
        XMethod m;
        Writer writer;
        for (Iterator it = methods.iterator(); it.hasNext(); ) {
            m = (XMethod) it.next();
            if (m.getDoc().hasTag("ti.action")) {
                writer = output.getWriter(destRoot, source, m.getName());
                context.put("xclass", xclass);
                context.put("xmethod", m);
                context.put("javaFile", source);

                processor.process(output.getTemplate(), context, writer);
            }    
        }
    }
}
