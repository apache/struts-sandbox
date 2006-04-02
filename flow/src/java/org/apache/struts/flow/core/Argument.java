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

/**  Stores an argument to be passed into the method called for flow execution */
public class Argument {
    private String name;
    private Object value;


    /**
     *  Constructor for the Argument object
     *
     *@param  name   The argument name
     *@param  value  The argument value
     */
    public Argument(String name, Object value) {
        this.name = name;
        this.value = value;
    }


    /**
     *  Returns the value of name.
     *
     *@return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Returns the value of value.
     *
     *@return    The value value
     */
    public Object getValue() {
        return value;
    }


    /**
     *  Prints the string value of the state
     *
     *@return    The string state
     */
    public String toString() {
        return name + ": " + value;
    }

}
