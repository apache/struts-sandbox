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

import org.apache.commons.logging.*;

/**  Logger extension that hooks into commons-logging */
public class CommonsLogger extends org.apache.struts.flow.core.Logger {

    private final static Log log = LogFactory.getLog(CommonsLogger.class);


    /**
     *  Logs an error message
     *
     *@param  msg  The message
     */
    public void error(String msg) {
        log.error(msg);
    }


    /**
     *  Logs a warning message
     *
     *@param  msg  The message
     */
    public void warn(String msg) {
        log.warn(msg);
    }


    /**
     *  Logs an info message
     *
     *@param  msg  The message
     */
    public void info(String msg) {
        log.info(msg);
    }


    /**
     *  Logs a debug message
     *
     *@param  msg  The message
     */
    public void debug(String msg) {
        log.debug(msg);
    }


    /**
     *  Gets whether debugging is enabled
     *
     *@return    True if enabled
     */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }


    /**
     *  Prints an exception
     *
     *@param  e  The exception
     */
    public void error(Exception e) {
        log.error(e);
    }
}
