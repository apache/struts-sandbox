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

import org.mozilla.javascript.Scriptable;
import org.apache.struts.flow.core.javascript.fom.FOM_Flow;
import java.lang.reflect.Constructor;

/**
 *  Defines a variable registrar used to define a call-specific variable in the
 *  global scope. Static variables are defined once per global scope, while
 *  call-specific variables can define instances of themselves for every script
 *  call.
 */
public class DefaultFlowVariableFactory implements FlowVariableFactory {

    private Class variableClass;

    public DefaultFlowVariableFactory(Class variableClass) {
        this.variableClass = variableClass;
    }

    /**
     *  Gets an instance of the variable. First tries to call constructor that
     *  takes a single argument of the Context. If not found, it calls the empty
     *  constructor.
     *
     *@param  scope  The scope the variable will be placed in
     *@param  ctx    The commons chain context for the call, null if defining a
     *      static variable
     *@return        The instance value
     */
    public Object getInstance(Scriptable scope, FOM_Flow flow) {
        try {
            Constructor c = null;
            try {
                c = variableClass.getConstructor(new Class[]{flow.getClass()});
            } catch (NoSuchMethodException ex) {
                // ignored
            }
            if (c != null) {
                return c.newInstance(new Object[]{flow});
            } else {
                return variableClass.newInstance();
            }
        } catch (Exception ex) {
            Factory.getLogger().error(ex);
            return null;
        }
    }

}

