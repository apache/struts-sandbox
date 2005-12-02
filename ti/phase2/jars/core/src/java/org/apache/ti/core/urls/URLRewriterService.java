/*
 * $Id$
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
 */
package org.apache.ti.core.urls;

import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Methods for registering URL rewriters, adding URL rewriters
 * to the chain, and for rewriting URLs using registered rewriters.
 * <p/>
 * <p> Note that when a URLRewriter is registered with this service
 * it is added to a chain (a List) of rewriters. When rewriting
 * occurs, we loop through each rewriter in the list. The only exception
 * to this is when a rewriter that does not allow other rewriters
 * to be used is registered. This then becomes the exclusive rewriter
 * to use and no other rewriters can be registered. </p>
 * <p/>
 * <p> The final step of the full rewriting process should be to run the
 * rewritten URI through the templated URL formatting process. See
 * {@link #getTemplatedURL} </p>
 * <p/>
 * <p> Also note that this API allows a client to register their own templated
 * URI formatter so they can manage their own templates and formatting. </p>
 */
public class URLRewriterService {
    private static final Logger _log = Logger.getInstance(URLRewriterService.class);
    private static final String URL_REWRITERS_KEY = "url_rewriters";
    private static final String TEMPLATTED_URL_FORMATTER_KEY = "templated_url_formatter";

    /**
     * Get the prefix to use when rewriting a query parameter name.
     * Loops through the list of registered URLRewriters to build up a the prefix.
     *
     * @param name the name of the query parameter.
     * @return a prefix to use to rewrite a query parameter name.
     */
    public static String getNamePrefix(String name) {
        ArrayList /*< URLRewriter >*/ rewriters = getRewriters();

        InternalStringBuilder prefix = new InternalStringBuilder();

        if (rewriters != null) {
            for (Iterator i = rewriters.iterator(); i.hasNext();) {
                URLRewriter rewriter = (URLRewriter) i.next();
                String nextPrefix = rewriter.getNamePrefix(name);

                if (nextPrefix != null) {
                    prefix.append(nextPrefix);
                }
            }
        }

        return prefix.toString();
    }

    /**
     * Rewrite the given URL, looping through the list of registered URLRewriters.
     * <p/>
     * <p> Once the MutableURI has been rewritten, and if it is an instance of
     * {@link FreezableMutableURI}, then this method will set the URI to a frozen
     * state. I.e. immutable. If a user then tries to use a setter method on the
     * rewritten URI, the FreezableMutableURI will throw an IllegalStateException. </p>
     * <p/>
     * <p> Note that after the rewritting the caller should run the rewritten URI
     * through the templated URI formatting process as the last step in rewriting.
     * See {@link #getTemplatedURL} </p>
     *
     * @param url             the URL to be rewritten.
     * @param type            the type of URL to be rewritten.  This is one of the following values:
     *                        <ul>
     *                        <li><code>action</code>: a standard (non-resource) URL
     *                        <li><code>resource</code>: a resource (e.g., image) URL
     *                        </ul>
     * @param needsToBeSecure a flag indicating whether the URL should be secure (SSL required) or not
     * @see #registerURLRewriter
     */
    public static void rewriteURL(MutableURI url, URLType type, boolean needsToBeSecure) {
        ArrayList /*< URLRewriter >*/ rewriters = getRewriters();

        if (rewriters != null) {
            for (Iterator i = rewriters.iterator(); i.hasNext();) {
                URLRewriter rewriter = (URLRewriter) i.next();
                rewriter.rewriteURL(url, type, needsToBeSecure);
            }
        }

        if (url instanceof FreezableMutableURI) {
            ((FreezableMutableURI) url).setFrozen(true);
        }
    }

    /**
     * Get the unmodifiable list of URLRewriter objects in the request that will be used if
     * {@link #rewriteURL} is called.
     *
     * @return an unmodifiable list of the URLRewriters that have been registered.
     */
    public static List /*< URLRewriter >*/ getURLRewriters() {
        return Collections.unmodifiableList(getRewriters());
    }

    /**
     * Register a URLRewriter (add to a list) in the request.  It will be added to the end
     * of a list of URLRewriter objects and will be used if {@link #rewriteURL} is called.
     *
     * @param rewriter the URLRewriter to register.
     * @return <code>false</code> if a URLRewriter has been registered
     *         that does not allow other rewriters. Otherwise, <code>true</code>
     *         if the URLRewriter was added to the chain or already exists in
     *         the chain.
     */
    public static boolean registerURLRewriter(URLRewriter rewriter) {
        ArrayList /*< URLRewriter >*/ rewriters = getRewriters();

        if (rewriters == null) {
            rewriters = new ArrayList /*< URLRewriter >*/();
            rewriters.add(rewriter);
            PageFlowActionContext.get().getRequestScope().put(URL_REWRITERS_KEY, rewriters);
        } else {
            return addRewriter(rewriters, rewriter, rewriters.size());
        }

        return true;
    }

    /**
     * Register a URLRewriter (add to a list) in the request.  It will be added at the
     * specified position in this list of URLRewriter objects and will be used if
     * {@link #rewriteURL} is called.
     *
     * @param index    the place to insert the URLRewriter
     * @param rewriter the URLRewriter to register.
     * @return <code>false</code> if a URLRewriter has been registered
     *         that does not allow other rewriters. Otherwise, <code>true</code>
     *         if the URLRewriter was added to the chain or already exists in
     *         the chain.
     */
    public static boolean registerURLRewriter(int index, URLRewriter rewriter) {
        ArrayList /*< URLRewriter >*/ rewriters = getRewriters();

        if (rewriters == null) {
            rewriters = new ArrayList /*< URLRewriter >*/();
            rewriters.add(rewriter);
            PageFlowActionContext.get().put(URL_REWRITERS_KEY, rewriters);
        } else {
            return addRewriter(rewriters, rewriter, index);
        }

        return true;
    }

    private static ArrayList /*< URLRewriter >*/ getRewriters() {
        Map innerRequestScope = PageFlowActionContext.get().getInnerRequestScope();

        return (ArrayList /*< URLRewriter >*/) innerRequestScope.get(URL_REWRITERS_KEY);
    }

    private static boolean addRewriter(ArrayList /*< URLRewriter >*/ rewriters, URLRewriter rewriter, int index) {
        if (otherRewritersAllowed(rewriters)) {
            if (!rewriters.contains(rewriter)) {
                if (!rewriter.allowOtherRewriters()) {
                    rewriters.clear();

                    if ((rewriters.size() > 0) && _log.isDebugEnabled()) {
                        InternalStringBuilder message = new InternalStringBuilder();
                        message.append("Register exclusive URLRewriter, \"");
                        message.append(rewriter.getClass().getName());
                        message.append("\". This removes any other URLRewriter objects already registered in the chain.");
                        _log.debug(message.toString());
                    }
                }

                rewriters.add(index, rewriter);
            }
        } else {
            if (_log.isDebugEnabled()) {
                InternalStringBuilder message = new InternalStringBuilder();
                message.append("Cannot register URLRewriter, \"");
                message.append(rewriter.getClass().getName());
                message.append("\". The URLRewriter, \"");
                message.append(rewriters.get(0).getClass().getName());
                message.append("\", is already registered and does not allow other rewriters.");
                _log.debug(message.toString());
            }

            return false;
        }

        return true;
    }

    private static boolean otherRewritersAllowed(ArrayList /*< URLRewriter >*/ rewriters) {
        if ((rewriters != null) && (rewriters.size() == 1) && !((URLRewriter) rewriters.get(0)).allowOtherRewriters()) {
            return false;
        }

        return true;
    }

    /**
         * @todo Finish documenting me!
         *
     * Unregister the URLRewriter (remove from the list) from the request.
     *
     * @param rewriter the URLRewriter to unregister
     * @todo fix this -- @ see #registerURLRewriter (ambiguous reference)
     */
    public static void unregisterURLRewriter(URLRewriter rewriter) {
        if (rewriter == null) {
            return;
        }

        ArrayList /*< URLRewriter >*/ rewriters = getRewriters();

        if (rewriters == null) {
            return;
        } else {
            rewriters.remove(rewriter);

            if (rewriters.size() == 0) {
                PageFlowActionContext.get().getRequestScope().remove(URL_REWRITERS_KEY);
            }
        }
    }

    /**
     * Unregister the URLRewriter (remove from the list) from the request.
     */
    public static void unregisterAllURLRewriters() {
        PageFlowActionContext.get().getRequestScope().remove(URL_REWRITERS_KEY);
    }

    /**
     * Tell whether rewritten form actions should be allowed to have query parameters.  If this returns
     * <code>false</code>, then a form-tag implementation should render query parameters into hidden
     * fields on the form instead of allowing them to remain in the URL.
     *
     * @return Return true if allowed, false if not.
     */
    public static boolean allowParamsOnFormAction() {
        ArrayList /*< URLRewriter >*/ rewriters = getRewriters();

        if (rewriters != null) {
            for (Iterator i = rewriters.iterator(); i.hasNext();) {
                URLRewriter rewriter = (URLRewriter) i.next();

                if (!rewriter.allowParamsOnFormAction()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Print out information about the chain of URLRewriters in this request.
     *
     * @param output a PrintStream to output chain of URLRewriters in this request.
     *               If <code>null</null>, <code>System.err</code> is used.
     */
    public static void dumpURLRewriters(PrintStream output) {
        ArrayList /*< URLRewriter >*/ rewriters = getRewriters();

        if (output == null) {
            output = System.err;
        }

        output.println("*** List of URLRewriter objects: " + rewriters);

        if (rewriters != null) {
            int count = 0;

            for (Iterator i = rewriters.iterator(); i.hasNext();) {
                URLRewriter rewriter = (URLRewriter) i.next();
                output.println("        " + count++ + ".  " + rewriter.getClass().getName());
                output.println("            allows other rewriters: " + rewriter.allowOtherRewriters());
                output.println("            rewriter: " + rewriter);
            }
        } else {
            output.println("        No URLRewriter objects are registered with this request.");
        }
    }

    /**
     * Format the given URI using a URL template, if defined in the URL template
     * config file, WEB-INF/url-template-config.xml. The {@link URIContext}
     * encapsulates some additional data needed to write out the string form.
     * E.g. It defines if the &quot;&amp;amp;&quot; entity or the
     * '&amp;' character should be used to separate quary parameters.
     * <p/>
     * <p>First try to use ther per-request registered <code>TemplatedURLFormatter</code>.
     * If one is not registered, try to use the per-webapp default
     * <code>TemplatedURLFormatter</code>, defined in struts-ti-config.xml
     * (with a class name) and set as an attribute of the application. Otherwise,
     * with no formatter, just return {@link MutableURI#getURIString(URIContext)}.
     *
     * @param uri        the MutableURI to be formatted into a String.
     * @param key        the URL template type to use for formatting the URI
     * @param uriContext data required to write out the string form.
     * @return the URL as a <code>String</code>
     */
    public static String getTemplatedURL(MutableURI uri, String key, URIContext uriContext) {
        TemplatedURLFormatter formatter = getTemplatedURLFormatter();

        if (formatter == null) {
            formatter = TemplatedURLFormatter.getTemplatedURLFormatter();

            if (formatter == null) {
                return uri.getURIString(uriContext);
            }
        }

        return formatter.getTemplatedURL(uri, key, uriContext);
    }

    private static TemplatedURLFormatter getTemplatedURLFormatter() {
        Map innerRequestScope = PageFlowActionContext.get().getInnerRequestScope();

        return (TemplatedURLFormatter) innerRequestScope.get(TEMPLATTED_URL_FORMATTER_KEY);
    }

    /**
     * Register a TemplatedURLFormatter in the request.
     * <p/>
     * <p> The TemplatedURLFormatter should be used as a final step in the rewriting
     * process to format the rewritten URL as defined by a template in the
     * WEB-INF/url-template-config.xml. There can only be one TemplatedURLFormatter,
     * not a chain as with the URLRewriters. </p>
     *
     * @param formatter the TemplatedURLFormatter to register.
     */
    public static void registerTemplatedURLFormatter(TemplatedURLFormatter formatter) {
        PageFlowActionContext.get().getRequestScope().put(TEMPLATTED_URL_FORMATTER_KEY, formatter);
    }

    /**
     * Unregister the TemplatedURLFormatter from the request.
     */
    public static void unregisterTemplatedURLFormatter() {
        PageFlowActionContext.get().getRequestScope().remove(TEMPLATTED_URL_FORMATTER_KEY);
    }
}
