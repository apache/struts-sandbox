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

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Ant task that wraps ProcessTags
 */
public class ProcessTagsTask {

    private ProcessTags processTags;
    private File srcdir;
    private File destdir;
    
    private static final Log log = LogFactory.getLog(ProcessTagsTask.class);
    
    public void setSrcdir(File file) {
        this.srcdir = file;
    }
    
    public void setDestdir(File file) {
        this.destdir = file;
    }
    
    public void execute() {
        XDocletParser parser = new XDocletParser();
        parser.init();
        ProcessTags pt = new ProcessTags();
        pt.setXDocletParser(parser);
        
        try {
            pt.process(srcdir, "Controller.java", destdir, "xwork.xml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}    
