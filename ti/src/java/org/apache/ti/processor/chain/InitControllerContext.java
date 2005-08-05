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
package org.apache.ti.processor.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.processor.ControllerContext;


/**
 *  Intializes the ControllerContext for this request
 */
public class InitControllerContext implements Command {
    
    protected ControllerContext context;
    protected static final Log log = LogFactory.getLog(InitControllerContext.class);
   
    public void setControllerContext(ControllerContext ctx) {
        this.context = ctx;
    }
    
    public boolean execute(Context origctx) {
        ControllerContext.setControllerContext(context);
        return false;
    }
}
