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
package org.apache.ti.util.internal.cache;

import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;
import org.apache.ti.util.logging.Logger;

import java.lang.reflect.Method;

/**
 * @exclude
 */
public class MethodCache {

    private static final Logger LOGGER = Logger.getInstance(MethodCache.class);

    private final InternalConcurrentHashMap _methodCache;

    /**
     *
     */
    public MethodCache() {
        _methodCache = new InternalConcurrentHashMap();
    }

    /**
     *
     */
    public final Method[] getMethods(Class type) {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("type: " + type + " hash code: " + type.hashCode());

        Object obj = _methodCache.get(type);

        if (obj == null) {
            obj = type.getMethods();
            _methodCache.put(type, obj);
        }

        return (Method[]) obj;
    }

    /**
     *
     */
    public final Method getMethod(Class type, String methodName, int argCount) {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Get method \"" + methodName + "\" from type \"" + type + "\" with " + argCount + " params");

        if (methodName == null)
            return null;

        Method[] methods = getMethods(type);

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName) && (argCount == methods[i].getParameterTypes().length))
                return methods[i];
        }

        return null;
    }

    /**
     *
     */
    public final Method getMethod(Class type, String methodName, String[] argTypes) {
        if (methodName == null)
            return null;

        Method[] methods = getMethods(type);
        Class[] parameterTypes = null;

        for (int i = 0; i < methods.length; i++) {
            // method names don't match
            if (!methods[i].getName().equals(methodName))
                continue;

            // never null...
            parameterTypes = methods[i].getParameterTypes();
            
            // zero arg method
            if ((argTypes == null || argTypes.length == 0) && parameterTypes.length == 0)
                return methods[i];
            // looking for zero arg method; found multi arg method
            else if ((argTypes == null || argTypes.length == 0) && !(parameterTypes.length == 0))
                continue;
            // method matching arg count; check argument types
            else if (parameterTypes != null && parameterTypes.length == argTypes.length) {
                boolean match = true;
                for (int j = 0; j < parameterTypes.length; j++) {
                    if (!parameterTypes[j].getName().equals(argTypes[j])) {
                        match = false;
                        break;
                    }
                }
                if (match) return methods[i];
            }
        }

        return null;
    }

    /**
     *
     */
    public final Method getMethod(Class type, String methodName, Class[] argTypes) {
        if (argTypes == null)
            return getMethod(type, methodName, (String[]) null);

        String[] typeStrs = new String[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            typeStrs[i] = argTypes[i].getName();
        }

        return getMethod(type, methodName, typeStrs);
    }
}
