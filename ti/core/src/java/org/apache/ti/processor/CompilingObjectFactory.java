/*
 * $Id: ControllerContext.java 230400 2005-08-05 05:13:54Z martinc $
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
package org.apache.ti.processor;

import org.apache.ti.config.*;
import java.util.*;
import java.io.*;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.Action;

import java.io.File;

import org.apache.commons.jci.CompilingClassLoader;
import org.apache.commons.jci.compilers.JavaCompiler;

import org.apache.commons.logging.*;


/**
 * Builds actions from the config.  If an Action is not created, it is assumed to be a Controller.
 */
public class CompilingObjectFactory extends ObjectFactory {

    private static final Log log = LogFactory.getLog(CompilingObjectFactory.class);
    private CompilingClassLoader cl;
    private JavaCompiler compiler;

    public void setJavaCompiler(JavaCompiler jc) {
        this.compiler = jc;
    }    
    

    public void setSrcPath(String src) {
        if (src != null && src.length() > 0) {
            File file = new File(src);
            if (file.exists()) {
                cl = new CompilingClassLoader(
                        Thread.currentThread().getContextClassLoader(), 
                        file,
                        compiler);
            } else {
                log.error("Specified source directory, "+src+" doesn't exist");
            }
        }    
    }
    
    public Class getClassInstance(String className) throws ClassNotFoundException {
        log.info("Looking up class "+className+" in compiling classloader");
       
        Class cls = cl.loadClass(className);
        return cl.loadClass(className);
    }    
}
