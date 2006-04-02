/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow;

import org.apache.struts.flow.core.*;
import org.apache.commons.chain.web.WebContext;

import java.util.*;

/**
 *  Access to Struts and Servlet resources
 *
 * @jsname struts
 */
public class Params extends HashMap implements WebContextAware {
    
    public static final String EXTRA_PARAMETERS = "extraParameters";
    
    protected static final Logger logger = Factory.getLogger();
    
    public void init(WebContext webctx) {
        putAll(webctx.getParam());
        Map extra = (Map)webctx.get(EXTRA_PARAMETERS);
        if (extra != null) {
            putAll(extra);
        }
    }
    
    public void cleanup() {
        clear();
    }
}

