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

import org.apache.ti.util.*;

import java.util.List;
import java.util.ArrayList;

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
        VelocityTemplateProcessor proc = new VelocityTemplateProcessor();
        proc.init();
        
        XDocletParser parser = new XDocletParser();
        parser.setTemplateProcessor(proc);
        
        ProcessTags pt = new ProcessTags();
        pt.setXdocletParser(parser);

        List outputs = new ArrayList();
        outputs.add(new OutputType("org/apache/ti/config/xdocletToXWork.vm", "xwork.xml", OutputType.ONCE));
        outputs.add(new OutputType("org/apache/ti/config/xdocletToValidation.vm", "$c-$a-validation.xml", OutputType.PER_ACTION));
        
        try {
            pt.process(srcdir, "Controller.java", destdir, outputs);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
