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
package org.apache.ti.util.type;

import java.util.Locale;

/**
 * Implementation of the {@link TypeConverter} interface that delegates
 * to a {@link TypeConverter} implementation which doesn't extend
 * {@link BaseTypeConverter}.
 */
public final class DelegatingTypeConverter
        extends BaseTypeConverter {

    private TypeConverter _converter = null;

    public DelegatingTypeConverter(TypeConverter converter) {
        super();
        assert converter != null;
        _converter = converter;
    }

    public Object convertToObject(Class type, String value, Locale locale) {
        return _converter.convertToObject(value);
    }
}
