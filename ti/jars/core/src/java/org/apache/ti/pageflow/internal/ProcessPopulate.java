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

import org.apache.ti.pageflow.RequestParameterHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.script.Expression;
import org.apache.ti.script.ExpressionEvaluator;
import org.apache.ti.script.ExpressionEvaluatorFactory;
import org.apache.ti.script.ExpressionUpdateException;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.Bundle;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import javax.servlet.jsp.el.VariableResolver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implement the processPopulate stage of the Struts / PageFlow request
 * processing lifecycle.  The {@link #populate(Object, boolean)} method is
 * invoked in order to take request parameters from the request
 * use the key / value pairs from the request to perform an update to the underlying
 * JavaBean objects.
 * <br/>
 * <br/>
 * Updates are performed on a key / value pair if the key is an expression; otherwise,
 * the updates are delegated to the Struts processPopulate infrastructure.
 */
public class ProcessPopulate {

    /**
     * This defines the name of the parameter that will contain the NetUI ID map..
     */
    public static final String IDMAP_PARAMETER_NAME = "netuiIdMap";

    private static final Logger _logger = Logger.getInstance(ProcessPopulate.class);

    // these must be kept in sync with the context names specified in the scripting languages
    private static final String PAGE_FLOW_CONTEXT = "pageFlow";

    private static final String WLW_TAG_HANDLER_PREFIX = "wlw-";
    private static final String WLW_TAG_HANDLER_SUFFIX = ":";

    private static final Map handlerMap = new HashMap();

    /**
     * An inner class that represnts the data that will be used to
     * perform an update.  If a key has a prefix handler, this
     * node is constructed and passed to the prefix handler
     * so that the prefix handler can change the expression or
     * values that will be used to execute the expression update.
     */
    public final static class ExpressionUpdateNode {

        public String expression = null;
        public String[] values = null;

        // can't be constructed outside of this class
        private ExpressionUpdateNode() {
        }

        public String toString() {
            InternalStringBuilder buf = new InternalStringBuilder();
            buf.append("expression: " + expression + "\n");
            if (values != null)
                for (int i = 0; i < values.length; i++)
                    buf.append("value[" + i + "]: " + values[i]);
            else
                buf.append("values are null");

            return buf.toString();
        }
    }

    /**
     * Register a {@link org.apache.ti.pageflow.RequestParameterHandler} that is added to handle a
     * particular prefix which be present as a prefix to a request parameter
     * key.  For keys that match the prefix, the key / value from the request
     * are put in an {@link ExpressionUpdateNode} struct and handed to the
     * {@link org.apache.ti.pageflow.RequestParameterHandler} for processing.  The returned {@link ExpressionUpdateNode}
     * is used to perform an expression update.
     *
     * @param prefix  the String prefix that will be appended to request paramters that
     *                should pass through the {@link org.apache.ti.pageflow.RequestParameterHandler} before being updated.
     * @param handler the handler that should handle all request paramters with
     *                the given <code>prefix</code>
     */
    public static void registerPrefixHandler(String prefix, RequestParameterHandler handler) {
        // should happen very infrequently
        synchronized (handlerMap) {
            String msg = "Register RequestParameterHandler with\n\tprefix: " + prefix + "\n\thandler: " + (handler != null ? handler.getClass().getName() : null);

            if (_logger.isInfoEnabled()) _logger.info(msg);

            if (handlerMap.get(prefix) == null)
                handlerMap.put(prefix, handler);
        }
    }

    /**
     * Write the handler name specified onto the given expression.
     */
    public static String writeHandlerName(String handler, String expression) {
        if (!ExpressionEvaluatorFactory.getInstance().isExpression(expression))
            throw new IllegalArgumentException(Bundle.getErrorString("ProcessPopulate_handler_nonAtomicExpression", new Object[]{expression}));

        if (!handlerMap.containsKey(handler))
            throw new IllegalStateException(Bundle.getErrorString("ProcessPopulate_handler_notRegistered", new Object[]{handler}));

        InternalStringBuilder buf = new InternalStringBuilder();
        buf.append(WLW_TAG_HANDLER_PREFIX);
        buf.append(handler);
        buf.append(WLW_TAG_HANDLER_SUFFIX);
        buf.append(expression);

        return buf.toString();
    }

    /**
     * Use the request parameters to populate all properties that have expression keys into
     * the underlying JavaBeans.
     * Creates a <code>java.util.Map</code> of objects that will be consumed by
     * Struts processPopulate.  This includes all request attributes that
     * were not expressions
     *
     * @param form if this request references an action and it has an <code>ActionForm</code>
     *             associated with it, then the <code>form</code> parameter is non-null.
     */
    public static void populate(Object form, boolean requestHasPopulated) {
        String key = null;
        Map strutsProperties = null;
        ExpressionEvaluator ee = ExpressionEvaluatorFactory.getInstance();
        PageFlowActionContext actionContext = PageFlowActionContext.get();        

        // a boolean so that we can avoid an instanceof below...
        boolean isMultipart = false;

        // if this returns null, it's not a mulitpart request
        // TODO: re-add multipart request support
        //Map params = MultipartRequestUtils.handleMultipartRequest(request, form);
        Map params = null;

        // make adjustments
        if (params != null)
            isMultipart = true;
        else
            params = actionContext.getParameters();

        if (params == null) {
            if (_logger.isWarnEnabled()) _logger.warn("An error occurred checking a request for multipart status.  No model values were updated.");
            return;
        }

        /* explicitly build a variable resolver that is used to provide objects that may be updated to the expression engine */
        VariableResolver variableResolver = ImplicitObjectUtil.getUpdateVariableResolver(form, true);

        /* todo: are there any ordering issues with using an Iterator vs. an Enumeration here? */
        Iterator iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            String expr = null;

            // if there is an expression map, lookup the real expression from the name
            expr = key;
            if (_logger.isDebugEnabled())
                _logger.debug("key: " + key + " value type: " + params.get(key).getClass().getName() + " value: " + params.get(key));

            try {
                Object paramsValue = params.get(key);
                if (ee.containsExpression(expr)) {
                    Object updateValue = null;
                    if (!isMultipart || paramsValue instanceof String[]) {
                        String[] values = (String[]) paramsValue;

                        // the only "contains" case that is accepted
                        if (expr.startsWith(WLW_TAG_HANDLER_PREFIX)) {
                            if (_logger.isDebugEnabled()) _logger.debug("Found an expression requiring a TAG HANDLER");

                            ExpressionUpdateNode node = doTagHandler(key, expr, values);

                            expr = node.expression;
                            values = node.values;
                        }

                        if (values != null && values.length == 1)
                            updateValue = values[0];
                        else
                            updateValue = values;
                    }
                    // handle funky types that Struts returns for a file upload request handler
                    else {
                        updateValue = params.get(key);
                    }

                    try {
                        // trap any bad expressions here
                        if (ee.isExpression(expr)) {
                            // common case, make this fast
                            if (!requestHasPopulated)
                                ee.update(expr, updateValue, variableResolver, true);
                            // must check the expression to make sure pageFlow. doesn't get executed more than once
                            else {
                                Expression pe = ee.parseExpression(expr);
                                String contextName = pe.getContext();
                                if (!contextName.equals(PAGE_FLOW_CONTEXT))
                                    ee.update(expr, updateValue, variableResolver, true);
                            }
                        }
                    }
                            // catch any errors, particularly expression parse failures
                    catch (ExpressionUpdateException e) {
                        String s = Bundle.getString("ExprUpdateError", new Object[]{expr, e});

                        // this is the hairy NetUI Warning that gets printed to the console
                        System.err.println(s);
                        if (_logger.isErrorEnabled()) _logger.error(s);

                        // add binding errors via PageFlowUtils
                        InternalUtils.addBindingUpdateError(expr, s, e);
                    }
                } else {
                    if (_logger.isDebugEnabled()) _logger.debug("HTTP request parameter key \"" + key + "\" is not an expression, handle with Struts");

                    if (strutsProperties == null)
                        strutsProperties = new HashMap();

                    strutsProperties.put(key, paramsValue);
                }
            }
                    // catch any unexpected exception
            catch (Exception e) {
                String s = Bundle.getString("ProcessPopulate_exprUpdateError", new Object[]{expr, e});
                //e.printStackTrace();

                System.err.println(s);

                if (_logger.isWarnEnabled()) _logger.warn(s, e);

                // add binding errors via PageFlowUtils
                InternalUtils.addBindingUpdateError(expr, s, e);
            }
        }

        //handleStrutsProperties(strutsProperties, form);
    }

    /**
     * Process a single key.
     *
     * @param key the request key that is being processed
     */
    static final ExpressionUpdateNode doTagHandler(String key, String expression, String[] values) {
        // not sure if this array will be mutable.  don't want to find out at this point.
        String[] _values = values;

        // key might be mangled here; make a copy
        String expr = expression;

        if (_logger.isDebugEnabled()) _logger.debug("Found prefixed tag; handlerName: " + key.substring(WLW_TAG_HANDLER_PREFIX.length(), key.indexOf(WLW_TAG_HANDLER_SUFFIX)));

        String handlerName = expression.substring(WLW_TAG_HANDLER_PREFIX.length(), expression.indexOf(WLW_TAG_HANDLER_SUFFIX));
        
        // execute callback to parameter handler.  Generally, these are tags.
        RequestParameterHandler handler = (RequestParameterHandler) handlerMap.get(handlerName);

        if (handler != null) {
            expr = expression.substring(expression.indexOf(WLW_TAG_HANDLER_SUFFIX) + 1);

            if (_logger.isDebugEnabled())
                _logger.debug("found handler for prefix \"" + handlerName + "\" type: " +
                        (handler != null ? handler.getClass().getName() : null) + "\n\t" +
                        "key: \"" + key + "\" expr: \"" + expr + "\"");

            ExpressionUpdateNode node = new ExpressionUpdateNode();
            node.expression = expr;
            node.values = _values;
            
            // request, request key, the standalone expression (may have other stuff bracketing the expression
            handler.process(key, expression, node);

            return node;
        } else
            throw new IllegalStateException("Request parameter references a tag handler prefix \"" +
                    handlerName + "\" that is not registered for expression \"" + key + "\"");
    }

    /*
    // @struts: org.apache.struts.util.RequestUtils.populate
    private static final void handleStrutsProperties(Map strutsProperties, ActionForm form)
    {
        if(strutsProperties != null)
        {
            if(_logger.isDebugEnabled()) _logger.debug("Handle Struts request parameters.");

            // default to Struts for non-expression keys
            try
            {
                BeanUtils.populate(form, strutsProperties);
            }
            catch(Exception e)
            {
                throw new RuntimeException("Exception processing bean and request parameters: ", e);
            }
        }
    }
    */
}
