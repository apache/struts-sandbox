/*
 * $Id: ProcessTagsInDevMode.java 230578 2005-08-06 20:21:45Z mrdon $
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
package org.apache.ti.processor.chain;

import java.io.*;
import java.util.*;

import org.apache.ti.processor.*;
import org.apache.ti.config.*;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;

import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Initializes XWork by replacing default factories
 */
public class ProcessTagsInDevMode implements Command {

    private static final Log log = LogFactory.getLog(ProcessTagsInDevMode.class);

    protected boolean devMode = false;
    private ProcessTags processTags;
    private List outputs;
    private File src;
    private File classes;
    private String controllerSourceName = "Controller.java";

    public void setProcessTags(ProcessTags pt) {
        this.processTags = pt;
        outputs = new ArrayList();
        outputs.add(new OutputType("org/apache/ti/config/xdocletToXWork.vm", "xwork.xml", false));
        outputs.add(new OutputType("org/apache/ti/config/xdocletToValidation.vm", "$c-$a-validation.xml", true));
    }    

    public void setControllerSourceName(String name) {
        this.controllerSourceName = name;
    }    
    
    public void setClassesPath(String dest) {
        classes = new File(dest);
    }    
   
    public void setSrcPath(String src) {
        if (src != null && src.length() > 0) {
            File file = new File(src);
            if (file.exists()) {
                this.src = file;
            } else {
                log.error("Specified source directory, "+src+" doesn't exist");
            }
        }    
    }
 
    public void setDevMode(boolean mode) {
        this.devMode = mode;
    }    

   
    public boolean execute(Context ctx) throws Exception {
        if (devMode) {
            processTags.process(src, controllerSourceName, classes, outputs);
        }
        
        return false;
    }    
}
