/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.uel;

import com.opensymphony.xwork2.inject.Container;
import de.odysseus.el.util.SimpleContext;
import org.apache.struts2.uel.elresolvers.*;

import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;


/**
 * An implementation of SimpleContext that knows about the ValueStack's
 * CompoundRoot.
 */
public class CompoundRootELContext extends SimpleContext {
    private final static BuiltinFunctionMapper BUILTIN_FUNCTION_MAPPER = new BuiltinFunctionMapper();

    public CompoundRootELContext(final Container container) {
        super(new CompositeELResolver() {
            {
                add(new MethodInvocationGuardELResolver(container));
                add(new CompoundRootELResolver(container));
                add(new ValueStackContextReferenceELResolver(container));
                add(new XWorkBeanELResolver(container));
                add(new XWorkListELResolver(container));
                add(new XWorkMapELResolver(container));
                add(new XWorkArrayELResolver(container));
                add(new BeanELResolver());
            }});
    }

    @Override
    public VariableMapper getVariableMapper() {
        return null;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return BUILTIN_FUNCTION_MAPPER;
    }
}
