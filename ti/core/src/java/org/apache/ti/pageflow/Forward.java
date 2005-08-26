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
package org.apache.ti.pageflow;

import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.handler.ReloadableClassHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An object of this type is returned from an action methods in a {@link PageFlowController} to
 * determine the next URI to be displayed.  It is constructed on the name of a forward defined
 * by the @{@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward} annotation, and resolves to the URI
 * specified in that forward.
 */
public class Forward implements Serializable {

    public static final Forward SUCCESS = new Forward("success");

    private static final long serialVersionUID = 1;
    private static final Object[] EMPTY_ACTION_FORM_ARRAY = new Object[0];

    private static final Logger _log = Logger.getInstance(Forward.class);


    private String _name;
    private String _path;

    private List _outputFormBeans = null;
    private boolean _init = false;
    private InternalStringBuilder _queryString = null;
    private boolean _explicitPath = false;
    private String _outputFormBeanType = null;
    private Map _actionOutputs = null;

    /**
     * Construct based on an initializer forward.  This is a framework-invoked constructor that should not normally
     * be called directly.
     */
    protected Forward(Forward init) {
        _outputFormBeans = init._outputFormBeans;
        _init = init._init;
        _queryString = init._queryString;
        _explicitPath = init._explicitPath;
        _outputFormBeanType = init._outputFormBeanType;
        _actionOutputs = init._actionOutputs;
    }

    /**
     * Construct based on a given request.  This is a framework-invoked constructor that should not normally
     * be called directly.
     */
    protected Forward() {
        setPath(PageFlowActionContext.get().getRequestPath());
        _explicitPath = true;
    }

    /**
     * Constructor which accepts the name of a forward defined by the
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}
     * annotation.
     *
     * @param forwardName the name of the forward
     *                    ({@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}) to resolve.
     */
    public Forward(String forwardName) {
        setName(forwardName);
    }

    /**
     * Constructor which accepts the name of a forward defined by the
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}
     * annotation.  The value returned from {@link #getPath} is resolved from this forward.
     * Also accepts a form bean to make available in the request (or user session, as appropriate).
     *
     * @param forwardName    the name of the forward
     *                       ({@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}) to resolve.
     * @param outputFormBean a form bean instance to make available in the request (or user session, as appropriate).
     *                       See {@link #addOutputForm} for details about how this manifests itself.
     */
    public Forward(String forwardName, Object outputFormBean) {
        this(forwardName);

        if (outputFormBean != null) {
            addOutputForm(outputFormBean);
        }
    }

    /**
     * Constructor which accepts the name of a forward defined by the
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}
     * annotation.  The value returned from {@link #getPath}
     * is resolved from this forward.  Also accepts a named action output
     * to make available in the request, through {@link PageFlowUtils#getActionOutput}..
     *
     * @param forwardName       the name of the forward
     *                          ({@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}) to resolve.
     * @param actionOutputName  the name of a action output to make available in the request.
     * @param actionOutputValue the action output object to make available in the request.
     */
    public Forward(String forwardName, String actionOutputName, Object actionOutputValue) {
        this(forwardName);
        addActionOutput(actionOutputName, actionOutputValue);
    }

    /**
     * Constructs a forward that returns the given URI for {@link #getPath}.
     *
     * @param uri the URI to return for {@link #getPath}.
     */
    public Forward(String forwardName, URI uri) {
        this(forwardName);
        setPath(uri.toString());
        _explicitPath = true;
    }

    /**
     * Constructs a forward that returns the given URL for {@link #getPath}.  Because the URL path
     * is absolute by nature, this forward will cause a browser redirect.
     *
     * @param url the URL to return for {@link #getPath}.
     */
    public Forward(String forwardName, URL url) {
        this(forwardName);
        setPath(url.toString());
        _explicitPath = true;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    /**
     * Add a form bean that will be made available in the request (or user session, as
     * appropriate) if this forward is returned by an action method in a {@link PageFlowController}.
     * Specifically, each form bean is stored as a request attribute with a name determined by
     * {@link PageFlowUtils#getFormBeanName}.
     *
     * @param formBean the form bean instance to add.
     */
    public final void addOutputForm(Object formBean) {
        if (formBean == null) throw new IllegalArgumentException("The output form bean may not be null.");
        if (_outputFormBeans == null) _outputFormBeans = new ArrayList();
        _outputFormBeans.add(formBean);
    }

    /**
     * Get all form beans attached to this forward through {@link #addOutputForm} or {@link #Forward(String, Object)}.
     *
     * @return an array of form bean instances that are attached to this forward.
     */
    public final Object[] getOutputForms() {
        if (_outputFormBeans == null) return EMPTY_ACTION_FORM_ARRAY;
        return _outputFormBeans.toArray(EMPTY_ACTION_FORM_ARRAY);
    }

    /**
     * Get all form beans attached to this forward through {@link #addOutputForm} or {@link #Forward(String, Object)}.
     *
     * @return an List of form bean instances that are attached to this forward.  <strong>May be <code>null</code></strong>.
     */
    public final List getOutputFormBeans() {
        return _outputFormBeans;
    }

    /**
     * Get the first output form bean that was added to this forward.
     */
    public Object getFirstOutputForm() {
        if (_outputFormBeans == null || _outputFormBeans.size() == 0) {
            if (_outputFormBeanType != null) {
                try {
                    if (_log.isDebugEnabled()) {
                        _log.debug("Creating form bean of type " + _outputFormBeanType);
                    }

                    ReloadableClassHandler rch = Handlers.get().getReloadableClassHandler();
                    Object formBean = rch.newInstance(_outputFormBeanType);
                    addOutputForm(formBean);
                    return formBean;
                } catch (Exception e) {
                    _log.error("Could not create form bean instance of " + _outputFormBeanType, e);
                }
            }

            return null;
        } else {
            return _outputFormBeans.get(0);
        }
    }

    /**
     * Set the path to be returned by {@link #getPath}.  This overrides any path or forward name
     * set in a constructor.
     *
     * @param contextRelativePath the path to be returned by {@link #getPath}.
     */
    public void setPath(String contextRelativePath) {
        _path = contextRelativePath;
    }

    public boolean isExplicitPath() {
        return _explicitPath;
    }

    /**
     * Get the path associated with this object.  Resolve it from the name of a forward
     * ({@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}) if necessary.
     *
     * @return a String that is the URI path.
     * @see #Forward(String)
     * @see #Forward(String, Object)
     * @see #Forward(String, URI)
     * @see #Forward(String, URL)
     * @see #setPath
     */
    public String getPath() {
        return _path;
    }

    /**
     * Set the query string that will be appended to the URI returned by {@link #getPath}.
     *
     * @param queryString the query string that will be appended to the URI.  If this string does not
     *                    start with <code>'?'</code>, then this character will be prepended; if the string is
     *                    <code>null</code>, the query string will be removed.
     */
    public void setQueryString(String queryString) {
        if (queryString == null || queryString.length() == 0) {
            _queryString = null;
        } else if (queryString.charAt(0) == '?') {
            _queryString = new InternalStringBuilder(queryString);
        } else {
            _queryString = new InternalStringBuilder("?").append(queryString);
        }
    }

    /**
     * Get the query string that will be appended to the URI returned by {@link #getPath}.
     *
     * @return the query string that will be appended to the URI, or <code>null</code> if there
     *         is no query string.
     */
    public String getQueryString() {
        return _queryString != null ? _queryString.toString() : null;
    }

    /**
     * Add a query parameter to the URI returned by {@link #getPath}.
     *
     * @param paramName the name of the query parameter.
     * @param value     the value of the query parameter, or <code>null</code> if there is no value.
     */
    public void addQueryParam(String paramName, String value) {
        if (_queryString == null) {
            _queryString = new InternalStringBuilder("?");
        } else {
            _queryString.append('&');
        }

        _queryString.append(paramName);

        if (value != null) {
            _queryString.append('=').append(value);
        }
    }

    /**
     * Add a query parameter with no value to the URI returned by {@link #getPath}.
     *
     * @param paramName the name of the query parameter.
     */
    public final void addQueryParam(String paramName) {
        addQueryParam(paramName, null);
    }

    /**
     * Adds an action output that will be made available in the request, through {@link PageFlowUtils#getActionOutput}.
     *
     * @param paramName the name of the action output.
     * @param value     the action output value.
     */
    public void addActionOutput(String paramName, Object value) {
        if (paramName == null || paramName.length() == 0) {
            throw new IllegalArgumentException("An action output name may not be null or empty.");
        }

        if (_actionOutputs == null) {
            _actionOutputs = new HashMap();
        }

        _actionOutputs.put(paramName, value);
    }

    /**
     * Get all action outputs that have been set on this forward.
     *
     * @return a Map of name/value pairs representing all action outputs.
     * @see #addActionOutput
     */
    public Map getActionOutputs() {
        return _actionOutputs;
    }
}
