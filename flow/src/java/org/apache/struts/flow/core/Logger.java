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
 *  Simple logger. Can be extended to hook into any logging system, but by
 *  default, prints messages to the console.
 */
public class Logger {

    /**
     *  Logs an error
     *
     *@param  msg  The message
     */
    public void error(String msg) {
        System.out.println("JS-ERROR: " + msg);
    }


    /**
     *  Logs a warning
     *
     *@param  msg  The message
     */
    public void warn(String msg) {
        System.out.println("JS-WARNING: " + msg);
    }


    /**
     *  Logs an info message
     *
     *@param  msg  The message
     */
    public void info(String msg) {
        System.out.println("JS-INFO: " + msg);
    }


    /**
     *  Logs a debugging message
     *
     *@param  msg  The message
     */
    public void debug(String msg) {
        System.out.println("JS-DEBUG: " + msg);
    }
    
    /**
     *  Logs a debugging message
     *
     *@param  msg  The message
     */
    public void debug(String msg, Throwable t) {
        System.out.println("JS-DEBUG: " + msg);
        t.printStackTrace();
    }


    /**
     *  Gets whether debug is enabled
     *
     *@return    True if enabled
     */
    public boolean isDebugEnabled() {
        return true;
    }


    /**
     *  Prints an exception
     *
     *@param  e  The exception
     */
    public void error(Exception e) {
        e.printStackTrace();
    }
}
