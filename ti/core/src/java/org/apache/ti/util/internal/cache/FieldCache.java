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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * @exclude
 */
public class FieldCache {

    private static final Logger _log = Logger.getInstance(FieldCache.class);

    private final InternalConcurrentHashMap _fieldCache;
    private final InternalConcurrentHashMap _declaredFieldCache;

    public FieldCache() {
        _fieldCache = new InternalConcurrentHashMap();
        _declaredFieldCache = new InternalConcurrentHashMap();
    }

    public final Field getField(Class type, String fieldName) {
        if (_log.isDebugEnabled()) _log.debug("getFields for: " + type);

        HashMap fields = (HashMap) _fieldCache.get(type);

        if (fields == null) {
            Field[] fieldArray = type.getFields();
            fields = new HashMap();

            for (int i = 0; i < fieldArray.length; i++) {
                Field field = fieldArray[i];
                fields.put(field.getName(), field);
            }

            _fieldCache.put(type, fields);
        }

        return (Field) fields.get(fieldName);
    }

    public final Field getDeclaredField(Class type, String fieldName) {
        if (_log.isDebugEnabled()) _log.debug("getDeclaredFields for: " + type);

        HashMap fields = (HashMap) _declaredFieldCache.get(type);

        if (fields == null) {
            Field[] fieldArray = type.getDeclaredFields();
            fields = new HashMap();

            for (int i = 0; i < fieldArray.length; i++) {
                Field field = fieldArray[i];
                if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
                fields.put(field.getName(), field);
            }

            _declaredFieldCache.put(type, fields);
        }

        return (Field) fields.get(fieldName);
    }
}
