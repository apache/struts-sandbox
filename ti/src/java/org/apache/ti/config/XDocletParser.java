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
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;

import xjavadoc.XClass;
import xjavadoc.XMethod;
import xjavadoc.XJavaDoc;
import xjavadoc.filesystem.ReaderFile;

/**
 *  Processes xdoclet-style tags and uses a velocity template to generate
 *  content.  This class is not thread-safe.
 */
public class XDocletParser {

    private VelocityEngine velocity = null;
    private Map templateCache = new HashMap();
    private Map parameters;
    private static final Log log = LogFactory.getLog(XDocletParser.class);

    public void init() {
        velocity = new VelocityEngine();

        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("velocity.properties"));
            velocity.init(props);
        } catch (Exception ex) {
            log.error("Unable to intialize velocity", ex);
        }

    }

    public void setParameters(Map map) {
        this.parameters = map;
    }

    public Map getParameters() {
        return parameters;
    }

    public void generate(String name, Reader reader, List outputs, File destDir) {
        XJavaDoc jdoc = new XJavaDoc();
        ReaderFile jdocFile = new ReaderFile(reader);


        String className = name.replace('/', '.');
        className = className.replace('\\', '.');
        className = className.substring(0, className.indexOf(".java"));

        jdoc.addAbstractFile(className, jdocFile);
        XClass xclass = jdoc.getXClass(className);
        
        Map contextMap = new HashMap();
        if (parameters != null) {
            contextMap.putAll(parameters);
        }


        OutputType output;
        Writer writer = null;
        for (Iterator i = outputs.iterator(); i.hasNext(); ) {
            output = (OutputType) i.next();
            Template template = null;
            try {
                template = velocity.getTemplate(output.getTemplate());
            } catch (Exception ex) {
                log.error("Unable to locate or parse template: "+output.getTemplate(), ex);
                continue;
            }    
            
            if (!output.getPerAction()) {
                writer = output.getWriter(destDir, name, null);
                VelocityContext context = new VelocityContext(contextMap);
                context.put("xclass", xclass);
                context.put("javaFile", name);

                writeOutput(template, writer, context);
            } else {
                List methods = xclass.getMethods();
                XMethod m;
                for (Iterator it = methods.iterator(); it.hasNext(); ) {
                    m = (XMethod) it.next();
                    if (m.getDoc().hasTag("ti.action")) {
                        writer = output.getWriter(destDir, name, m.getName());
                        VelocityContext context = new VelocityContext(contextMap);
                        context.put("xclass", xclass);
                        context.put("xmethod", m);
                        context.put("javaFile", name);

                        writeOutput(template, writer, context);
                    }
                }    
            }    
        }
    }

    protected void writeOutput(Template template, Writer writer, VelocityContext ctx) {
        try {
            template.merge(ctx, writer);
        } catch (Exception ex) {
            log.error("Unable to generate output", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close(); 
                } catch (IOException ex) {}
            }
        }    
    }
}
