/*
 * Copyright 2003,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ti.processor.chain;


import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;

import com.opensymphony.xwork.ActionContext;

/**
 * <p>Select the <code>Locale</code> to be used for this request.</p>
 *
 * @version $Rev: 179995 $ $Date: 2005-06-04 07:58:46 -0700 (Sat, 04 Jun 2005) $
 */

public abstract class AbstractSelectLocale implements Command {

    private static final Log log = LogFactory.getLog(AbstractSelectLocale.class);

    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Select the <code>Locale</code> to be used for this request.</p>
     *
     * @param actionCtx The <code>Context</code> for the current request
     *
     * @return <code>false</code> so that processing continues
     */
    public boolean execute(Context context) throws Exception {

        WebContext ctx = (WebContext)context;
        
        // Retrieve and cache appropriate Locale for this request
        Locale locale = getLocale(ctx);
        log.debug("set context locale to " + locale);

        ActionContext.getContext().put(ActionContext.LOCALE, locale);

        return (false);

    }


    // ------------------------------------------------------- Protected Methods


    /**
     * <p>Return the <code>Locale</code> to be used for this request.</p>
     *
     * @param context The <code>Context</code> for this request
     */
    protected abstract Locale getLocale(WebContext context);


}
