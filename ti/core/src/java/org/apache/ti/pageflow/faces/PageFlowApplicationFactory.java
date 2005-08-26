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
package org.apache.ti.pageflow.faces;

import org.apache.ti.pageflow.faces.internal.PageFlowApplication;
import org.apache.ti.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

/**
 * JavaServer Faces application factory that enables Page Flow integration.  It is activated like this in
 * faces-config.xml:
 * <blockquote>
 * <code>
 * &lt;factory&gt;<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;application-factory&gt;org.apache.ti.pageflow.faces.PageFlowApplicationFactory&lt;/application-factory&gt;<br/>
 * &lt;/factory&gt;
 * </code>
 * </blockquote>
 * <p/>
 * JSF/Page Flow integration has the following features:
 * <ul>
 * <li>
 * A JSF component like CommandLink or CommandButton can raise a Page Flow action simply by specifying the
 * action name in its <code>action</code> attribute, like this:
 * <blockquote>
 * <code>
 * &lt;h:commandLink action="someAction" value="raise a Page Flow action"/&gt;
 * </code>
 * </blockquote>
 * The component can send a form bean to the action by adding a "submitFormBean" attribute with a binding
 * expression that will determine the bean instance:
 * <blockquote>
 * <code>
 * &lt;h:commandLink action="someAction" value="submit a form bean to a Page Flow action"&gt;<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;f:attribute name="submitFormBean" value="backing.theForm"/&gt;<br/>
 * &lt;/h:commandLink&gt;
 * </code>
 * </blockquote>
 * In the example above, the value returned from the backing bean's <code>getTheForm</code> method will be
 * sent to the "someAction" action in the page flow.
 * </li>
 * <li>
 * A {@link org.apache.ti.pageflow.FacesBackingBean} instance will be created whenever a
 * corresponding JSF path is requested (e.g., an instance of FacesBackingBean-derived foo.MyPage will be created
 * for the webapp-relative path "/foo/MyPage.faces").  The instance will be released (removed from the user
 * session) when a non-matching path is requested.  A faces backing bean can hold component references and
 * event/command handlers, and it can raise actions with normal JSF command event handlers that are annotated
 * with {@link org.apache.ti.pageflow.annotations.ti.commandHandler &#64;ti.commandHandler}.
 * The bean instance can be bound to with a JSF-style expression like <code>#{backing.myComponent}</code>.
 * </li>
 * <li>
 * When a Page Flow action goes back to a JSF page through the <code>currentPage</code> or
 * <code>previousPage</code> values for
 * {@link org.apache.ti.pageflow.annotations.ti.forward#navigateTo navigateTo}
 * on {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}, the page's backing
 * bean and component tree are restored.
 * </li>
 * </ul>
 */
public class PageFlowApplicationFactory
        extends ApplicationFactory {

    private static final Logger _log = Logger.getInstance(PageFlowApplicationFactory.class);

    private ApplicationFactory _delegate;
    private PageFlowApplication _app;

    public PageFlowApplicationFactory(ApplicationFactory delegate) {
        if (_log.isDebugEnabled()) {
            _log.debug("Adapting ApplicationFactory" + delegate);
        }

        _delegate = delegate;
        _app = new PageFlowApplication(delegate.getApplication());
    }

    public Application getApplication() {
        return _app;
    }

    public void setApplication(Application application) {
        _delegate.setApplication(application);
        _app = new PageFlowApplication(application);
    }
}
