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

import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.handler.Handlers;


/**
 * Information that is cached per pageflow class.
 */
public class CachedPageFlowInfo
        extends CachedSharedFlowRefInfo {

    /**
     * A cached copy of the namespace for this PageFlowController.
     */
    private String _namespace;

    /**
     * A cached copy of the webapp-relative URI for this PageFlowController.
     */
    private String _path;


    public CachedPageFlowInfo(Class pageFlowClass) {
        AnnotationReader annReader = Handlers.get().getAnnotationHandler().getAnnotationReader(pageFlowClass);
        initSharedFlowFields(annReader, pageFlowClass.getDeclaredFields());
        
        // URI
        String className = pageFlowClass.getName();
        _path = '/' + className.replace('.', '/') + PageFlowConstants.PAGEFLOW_EXTENSION;
        
        // namespace
        _namespace = InternalUtils.inferNamespaceFromClassName(className);
    }

    public String getNamespace() {
        return _namespace;
    }

    public String getPath() {
        return _path;
    }
}
