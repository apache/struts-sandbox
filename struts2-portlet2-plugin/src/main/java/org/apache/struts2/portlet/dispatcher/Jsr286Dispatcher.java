package org.apache.struts2.portlet.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import static org.apache.struts2.portlet.PortletContstants.SERVE_RESOURCE_PHASE;
import org.apache.struts2.portlet.servlet.PortletServletResponse;
import org.apache.struts2.portlet.servlet.PortletServletResponseJSR286;

import javax.portlet.*;
import java.io.IOException;

public class Jsr286Dispatcher extends Jsr168Dispatcher {

	private final static Logger LOG = LoggerFactory.getLogger(Jsr286Dispatcher.class);


	@Override
	public void processEvent( EventRequest request, EventResponse response)
			throws PortletException, IOException {
		if (LOG.isDebugEnabled()) LOG.debug("Entering processEvent");
		resetActionContext();
		try {
			// We'll use the event name as the "action"
			serviceAction(request, response,
					getRequestMap(request), getParameterMap(request),
					getSessionMap(request), getApplicationMap(),
					portletNamespace, EVENT_PHASE);
			if (LOG.isDebugEnabled()) LOG.debug("Leaving processAction");
		} finally {
			ActionContext.setContext(null);
		}
	}

	@Override
	public void serveResource( ResourceRequest request, ResourceResponse response)
			throws PortletException, IOException {
		if (LOG.isDebugEnabled()) LOG.debug("Entering serveResource");
		resetActionContext();
		try {
			serviceAction(request, response,
					getRequestMap(request), getParameterMap(request),
					getSessionMap(request), getApplicationMap(),
					portletNamespace, SERVE_RESOURCE_PHASE);
		}
		finally {
			ActionContext.setContext(null);
		}
	}

    @Override
    protected String getDefaultActionPath( PortletRequest portletRequest ) {
        if (portletRequest instanceof EventRequest) {
            return ((EventRequest) portletRequest).getEvent().getName();
        }
        return super.getDefaultActionPath(portletRequest);
    }

    @Override
    protected PortletServletResponse createPortletServletResponse( PortletResponse response ) {
        return new PortletServletResponseJSR286(response);
    }
}
