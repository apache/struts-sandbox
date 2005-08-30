/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.script.el;

import org.apache.ti.script.IllegalExpressionException;
import org.apache.ti.util.logging.Logger;

import javax.servlet.jsp.el.VariableResolver;

/**
 *
 */
public class NetUIReadVariableResolver
        extends NetUIVariableResolver {

    private static final Logger LOGGER = Logger.getInstance(NetUIReadVariableResolver.class);

    private VariableResolver _vr = null;

    public NetUIReadVariableResolver(VariableResolver vr) {
        assert vr != null;
        _vr = vr;
    }

    public Object resolveVariable(String name) {

        try {
            return _vr.resolveVariable(name);
        } catch (javax.servlet.jsp.el.ELException ele) {
            RuntimeException re = new RuntimeException("Could not resolve variable named \"" + name + "\"", new IllegalExpressionException());

            if (LOGGER.isErrorEnabled())
                LOGGER.error("", re);

            throw re;
        }
    }

    public String[] getAvailableVariables() {
        return new String[]{"actionForm", "pageFlow", "globalApp", "request", "session", "application", "pageContext", "bundle", "container", "url", "pageInput"};
    }
}
