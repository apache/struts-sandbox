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

import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.beans.beancontext.BeanContextServiceProvider;
import java.beans.beancontext.BeanContextServices;
import java.util.Iterator;


/**
 * Service provider that offers initialization for PageFlowController and SharedFlowControler members in a Control.
 */
public class PageFlowServiceProvider implements BeanContextServiceProvider {

    private static final PageFlowServiceProvider _provider = new PageFlowServiceProvider();

    public static final PageFlowServiceProvider getProvider() {
        return _provider;
    }

    private PageFlowServiceProvider() {
    }

    public static interface HasServletRequest {

        ServletRequest getServletRequest();
    }

    public Object getService(BeanContextServices bcs, Object requestor, Class serviceClass,
                             Object serviceSelector) {
        //
        // These services are only available to controls running within the scope of a PageFlowBeanContext
        //
        if (!(bcs instanceof HasServletRequest)) return null;

        if (PageFlowController.class.equals(serviceClass)) {
            ServletRequest request = ((HasServletRequest) bcs).getServletRequest();
            if (!(request instanceof HttpServletRequest)) return null;
            return PageFlowUtils.getCurrentPageFlow();
        }

        return null;
    }

    public void releaseService(BeanContextServices bcs, Object requestor, Object service) {
    }

    public Iterator getCurrentServiceSelectors(BeanContextServices bcs, Class serviceClass) {
        return null;
    }
}
