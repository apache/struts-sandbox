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
package org.apache.struts2.uel.elresolvers;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import javax.el.ELContext;
import javax.el.ELException;
import java.util.Map;

/**
 * Will throw an exception if invoke is called and method invocation is not allowed
 */
public class MethodInvocationGuardELResolver extends AbstractELResolver {
    public MethodInvocationGuardELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    @Override
    public Object invoke(ELContext elContext, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        Map<String, Object> valueStackContext = getValueStackContext(elContext);
        if (ReflectionContextState.isDenyMethodExecution(valueStackContext)) {
            //you aint invoking this
            throw new ELException("Method ivocations are disabled");
        } else
            return null;
    }
}
