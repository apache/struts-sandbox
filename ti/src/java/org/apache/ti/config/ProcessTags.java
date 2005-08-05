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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Crawls a directory, processing all found Java source files.
 */
public class ProcessTags {

    private XDocletParser xdocletParser;
    
    private static final Log log = LogFactory.getLog(ProcessTags.class);
    private static final String SEP = File.separator;
    
    public void setXDocletParser(XDocletParser parser) {
        this.xdocletParser = parser;
    }
    
    public void process(File src, String srcName, File dest, String destName) throws IOException {
        crawl(src, srcName, dest, destName, new ArrayList());   
    }
    
    protected void crawl(File src, String srcName, File dest, String destName, List stack) throws IOException {
        File[] kids = src.listFiles();
        boolean controllerFound = false;
        for (int x=0; x<kids.length; x++) {
            if (kids[x].isDirectory()) {
                stack.add(kids[x].getName());
                crawl(kids[x], srcName, dest, destName, stack);
                stack.remove(stack.size() - 1);
            } else if (!controllerFound && srcName.equals(kids[x].getName())) {
                StringBuffer path = new StringBuffer();
                for (Iterator i = stack.iterator(); i.hasNext(); ) {
                    path.append(i.next()).append(SEP);
                }
                File destDir = new File(dest, path.toString());
                destDir.mkdirs();
                File destFile = new File(destDir, destName);
                String filePath = path.toString() + kids[x].getName();
                
                log.info("Generating "+destFile);
                FileWriter writer =  new FileWriter(destFile);
                try {
                    xdocletParser.generate(filePath, new FileReader(kids[x]), writer);
                } finally {
                    try {
                        writer.close();
                    } catch (IOException ex) {}
                }
                controllerFound = true;
            }
        }
    }
}    
