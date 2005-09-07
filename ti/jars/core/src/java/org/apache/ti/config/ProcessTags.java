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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Crawls a directory, processing all found Java source files.
 */
public class ProcessTags {

    private XDocletParser xdocletParser;

    private static final Log log = LogFactory.getLog(ProcessTags.class);
    
    /**
     * @todo Where is this being (or will it be) used?
     */ 
//    private static final String SEP = File.separator;

    /**
     * Set the parser
     * @param parser
     */
    public void setXdocletParser(XDocletParser parser) {
        this.xdocletParser = parser;
    }

    /**
     * Process tags
     * @param src
     * @param srcName
     * @param dest
     * @param outputs
     * @throws IOException
     */
    public void process(File src, String srcName, File dest, List outputs) throws IOException {
        ArrayList sources = new ArrayList();
        crawl(src, srcName, src, outputs, sources);
        xdocletParser.generate(sources, src, dest, outputs);
    }

    protected void crawl(File src, String srcName, File srcRoot, List outputs, List sources) throws IOException {
        File[] kids = src.listFiles();
        boolean controllerFound = false;
        for (int x = 0; x < kids.length; x++) {
            if (kids[x].isDirectory()) {
                //stack.add(kids[x].getName());
                crawl(kids[x], srcName, srcRoot, outputs, sources);
                //stack.remove(stack.size() - 1);
            } else if (!controllerFound && srcName.equals(kids[x].getName())) {
                URI srcUri = kids[x].toURI();
                URI fileUri = srcRoot.toURI();
                URI result = fileUri.relativize(srcUri);
                log.info("Adding source "+result);
                sources.add(result.toString());
                
                //xdocletParser.generate(filePath, new FileReader(kids[x]), outputs, destDir);
                controllerFound = true;
            }
        }
    }
}
