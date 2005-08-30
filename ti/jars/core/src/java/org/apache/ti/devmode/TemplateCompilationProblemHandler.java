/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.ti.devmode;

import org.apache.commons.jci.problems.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.util.*;




/**
 */
public class TemplateCompilationProblemHandler implements CompilationProblemHandler {
    
    private final static Log log = LogFactory.getLog(TemplateCompilationProblemHandler.class);

    private int errors;
    private int warnings;
    private List problems = new ArrayList();
    private TemplateProcessor processor;
    private String template = "org/apache/ti/devmode/compilationProblems.vm";
    
    public void setTemplateProcessor(TemplateProcessor p) {
        this.processor = p;
    }
    
    public void handle( final CompilationProblem pProblem ) {
        problems.add(pProblem);
        if (pProblem.isError()) {
            errors++;
        } else {
            warnings++;
        }
        
        log.debug(pProblem);
    }
    
    public String getResultPage() {
        Map ctx = new HashMap();
        ctx.put("problems", problems);
        ctx.put("errorCount", new Integer(errors));
        ctx.put("warningCount", new Integer(warnings));
        String page = processor.process(template, ctx);
        return page;
    }
    
    
    public int getErrorCount() {
        return errors;
    }
    public int getWarningCount() {
        return warnings;
    }
    
    public void clear() {
        log.info(getResultPage());
        errors = 0;
        warnings = 0;
        problems.clear();
    }
}
