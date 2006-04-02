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
package org.apache.struts.flow.core;

/**
 *  Simple static factory for getting interface implementations. Should be
 *  replaced later by something more pluggable.
 *
 *@author    <a href="mailto:davjon@sas.com">David M Johnson</a>
 */
public class Factory {
    
    private static Logger logger = new Logger();
    private static ContinuationsManager continuationsManager = null;

    /**
     *  Sets the logger 
     *
     *@param  log  The new logger value
     */
    public static void setLogger(Logger log) {
        logger = log;
    }


    /**
     *  Gets the logger
     *
     *@return    The logger value
     */
    public static Logger getLogger() {
        return logger;
    }


    /**
     *  Gets the continuationsManager 
     *
     *@return    The continuationsManager value
     */
    public static ContinuationsManager getContinuationsManager() {
        if (continuationsManager == null) {
            try {
                continuationsManager = new ContinuationsManagerImpl();
            } catch (Exception e) {
                throw new RuntimeException("ERROR initializing ContinationsManager", e);
            }
        }
        return continuationsManager;
    }
    
}

