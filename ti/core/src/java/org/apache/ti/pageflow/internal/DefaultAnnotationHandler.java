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
package org.apache.ti.pageflow.internal;

import org.apache.ti.pageflow.handler.AnnotationHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.SourceResolver;
import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;

import java.util.Map;

public class DefaultAnnotationHandler extends DefaultHandler implements AnnotationHandler {

    private static final String CACHE_ATTR = InternalConstants.ATTR_PREFIX + "annCache";

    private SourceResolver _sourceResolver;

    public void setSourceResolver(SourceResolver sourceResolver) {
        _sourceResolver = sourceResolver;
    }

    public AnnotationReader getAnnotationReader(Class type) {
        Map appScope = PageFlowActionContext.get().getApplication();
        InternalConcurrentHashMap cache = (InternalConcurrentHashMap) appScope.get(CACHE_ATTR);

        if (cache == null) {
            cache = new InternalConcurrentHashMap();
            appScope.put(CACHE_ATTR, cache);
        }

        AnnotationReader reader = (AnnotationReader) cache.get(type);

        if (reader == null) {
            reader = new AnnotationReader(type, _sourceResolver);
            cache.put(type, reader);
        }

        return reader;
    }


}
