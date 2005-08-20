/*
 * $Id: SourceResolver.java 230400 2005-08-05 05:13:54Z martinc $
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
package org.apache.ti.util;

import java.io.IOException;
import java.util.*;
import java.io.*;

import org.apache.commons.chain.web.WebContext;
import org.apache.velocity.*;
import org.apache.velocity.app.VelocityEngine;
import org.apache.commons.logging.*;

/**
 *  Resovles resources
 */
public class VelocityTemplateProcessor implements TemplateProcessor {
    
    private static final Log log = LogFactory.getLog(VelocityTemplateProcessor.class);
    
    private VelocityEngine velocity = null;
    private Map templateCache = new HashMap();
    
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
    
    public String process(String path, Map context) {
        StringWriter writer = new StringWriter();
        process(path, context, writer);
        return writer.toString();
    }
    
    public void process(String path, Map contextMap, Writer writer) {
        VelocityContext context = new VelocityContext(contextMap);
        
        Template template = null;;
        try {
            template = velocity.getTemplate(path);
        } catch (Exception ex) {
            log.error("Unable to locate or parse template: "+path, ex);
            return;
        }
        try {
            template.merge(context, writer);
        } catch (Exception ex) {
            log.error("Unable to generate output", ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }

}
