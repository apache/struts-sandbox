/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.shale.test.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;


/**
 * <p>Mock implementation of <code>FacesContext</code>.</p>
 *
 * $Id$
 */

public class MockFacesContext extends FacesContext {


    // ------------------------------------------------------------ Constructors


    public MockFacesContext() {
        super();
        setCurrentInstance(this);
    }


    public MockFacesContext(ExternalContext externalContext) {
        setExternalContext(externalContext);
        setCurrentInstance(this);
    }


    public MockFacesContext(ExternalContext externalContext, Lifecycle lifecycle) {
        this(externalContext);
    }


    // ----------------------------------------------------- Mock Object Methods


    public void setApplication(Application application) {

        this.application = application;

    }


    public void setExternalContext(ExternalContext externalContext) {

        this.externalContext = externalContext;

    }


    // ------------------------------------------------------ Instance Variables


    private Application application = null;
    private ExternalContext externalContext = null;
    private Map messages = new HashMap();
    private boolean renderResponse = false;
    private boolean responseComplete = false;
    private ResponseStream responseStream = null;
    private ResponseWriter responseWriter = null;
    private UIViewRoot viewRoot = null;


    // ---------------------------------------------------- FacesContext Methods


    public Application getApplication() {

        return this.application;

    }


    public Iterator getClientIdsWithMessages() {

        return messages.keySet().iterator();

    }


    public ExternalContext getExternalContext() {

        return this.externalContext;

    }


    public Severity getMaximumSeverity() {
  
        throw new UnsupportedOperationException();

    }


    public Iterator getMessages() {

        ArrayList results = new ArrayList();
        Iterator clientIds = messages.keySet().iterator();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            results.addAll((List) messages.get(clientId));
        }
        return results.iterator();

    }


    public Iterator getMessages(String clientId) {

        List list = (List) messages.get(clientId);
        if (list == null) {
            list = new ArrayList();
        }
        return list.iterator();

    }


    public RenderKit getRenderKit() {

        UIViewRoot vr = getViewRoot();
        if (vr == null) {
            return null;
        }
        String renderKitId = vr.getRenderKitId();
        if (renderKitId == null) {
            return null;
        }
        RenderKitFactory rkFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        return rkFactory.getRenderKit(this, renderKitId);

    }


    public boolean getRenderResponse() {

        return this.renderResponse;

    }


    public boolean getResponseComplete() {

        return this.responseComplete;

    }


    public ResponseStream getResponseStream() {

        return this.responseStream;

    }


    public void setResponseStream(ResponseStream responseStream) {

        this.responseStream = responseStream;

    }


    public ResponseWriter getResponseWriter() {

        return this.responseWriter;

    }


    public void setResponseWriter(ResponseWriter responseWriter) {

        this.responseWriter = responseWriter;

    }


    public UIViewRoot getViewRoot() {

        return this.viewRoot;

    }


    public void setViewRoot(UIViewRoot viewRoot) {

        this.viewRoot = viewRoot;

    }


    public void addMessage(String clientId, FacesMessage message){ 

        if (message == null) {
            throw new NullPointerException();
        }
        List list = (List) messages.get(clientId);
        if (list == null) {
            list = new ArrayList();
            messages.put(clientId, list);
        }
        list.add(message);

    }


    public void release() {

        application = null;
        externalContext = null;
        messages.clear();
        renderResponse = false;
        responseComplete = false;
        responseStream = null;
        responseWriter = null;
        viewRoot = null;
        setCurrentInstance(null);

    }


    public void renderResponse() {

        this.renderResponse = true;

    }


    public void responseComplete() {

        this.responseComplete = true;

    }


}
