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

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import org.apache.struts2.uel.UELValueStack;

import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;

/**
 * Base class for resolvers in the UEL plugin
 */
public abstract class AbstractELResolver extends ELResolver {
    protected final ReflectionProvider reflectionProvider;
    protected final XWorkConverter xworkConverter;
    protected final NullHandler nullHandler;
    protected final ObjectTypeDeterminer objectTypeDeterminer;
    protected final ObjectFactory objectFactory;

    public AbstractELResolver(Container container) {
        this.reflectionProvider = container.getInstance(ReflectionProvider.class);
        this.xworkConverter = container.getInstance(XWorkConverter.class);
        this.nullHandler = container.getInstance(NullHandler.class, "java.lang.Object");
        this.objectTypeDeterminer = container.getInstance(ObjectTypeDeterminer.class);
        this.objectFactory = container.getInstance(ObjectFactory.class);
    }

    protected Map getValueStackContext(ELContext elContext) {
        return (Map) elContext.getContext(UELValueStack.ContextKey.class);
    }

    public Class<?> getCommonPropertyType(ELContext elContext, Object o) {
        return null;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object o) {
        return null;
    }

    public Class<?> getType(ELContext elContext, Object o, Object o1) {
        return null;
    }

    public boolean isReadOnly(ELContext elContext, Object o, Object o1) {
        return false;
    }
}
