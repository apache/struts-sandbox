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

import java.util.Properties;
import java.io.*;
import org.apache.struts.flow.core.FlowException;

public class FlowConfiguration extends Properties {
    
    private Properties props;
    
    private static final FlowConfiguration self = new FlowConfiguration();
    
    public static FlowConfiguration getInstance() {
        return self;
    }
    
    private FlowConfiguration() {
        super();
        
        try {
            InputStream in = getClass().getResourceAsStream("/flow-defaults.properties");
            load(in);
            
            in = getClass().getResourceAsStream("/flow.properties");
            if (in != null) {
                load(in);
            }
        } catch (IOException ex) {
            throw new FlowException("Unable to load properties", ex);
        }
    }
    
}

