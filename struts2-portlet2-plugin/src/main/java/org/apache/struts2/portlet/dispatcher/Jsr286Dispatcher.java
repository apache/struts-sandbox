package org.apache.struts2.portlet.dispatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.servlet.PortletServletRequest;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import static org.apache.struts2.portlet.PortletContstants.*;

public class Jsr286Dispatcher extends Jsr168Dispatcher {

	private final static Logger LOG = LoggerFactory.getLogger(Jsr286Dispatcher.class);
	
	
	@Override
	public void processEvent(EventRequest request, EventResponse response)
			throws PortletException, IOException {
		LOG.debug("Entering processEvent");
		resetActionContext();
		try {
			// We'll use the event name as the "action"
			serviceAction(request, response, getActionMapping(request),
					getRequestMap(request), getParameterMap(request),
					getSessionMap(request), getApplicationMap(),
					portletNamespace, EVENT_PHASE);
			LOG.debug("Leaving processAction");
		} finally {
			ActionContext.setContext(null);
		}
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws PortletException, IOException {
		LOG.debug("Entering serveResource");
		resetActionContext();
		try {
			serviceAction(request, response, getActionMapping(request),
					getRequestMap(request), getParameterMap(request),
					getSessionMap(request), getApplicationMap(),
					portletNamespace, SERVE_RESOURCE_PHASE);
		}
		finally {
			ActionContext.setContext(null);
		}
	}
	
	/**
	 * Gets the action mapping from the event request.
	 * 
	 * @param request
	 *            the EventRequest object.
	 */
	protected ActionMapping getActionMapping(final EventRequest request) {
		ActionMapping mapping = null;
		String actionPath = null;
		actionPath = request.getEvent().getName();
		if (!TextUtils.stringSet(actionPath)) {
			mapping = (ActionMapping) actionMap.get(request.getPortletMode());
		} else {
			Map<String, String[]> extra = new HashMap<String, String[]>();
			extra.put(ACTION_PARAM, new String[]{actionPath});
			// Use the usual action mapper, but it is expecting an action
			// extension
			// on the uri, so we add the default one, which should be ok as the
			// portlet is a portlet first, a servlet second
			PortletServletRequest httpRequest = new PortletServletRequest(
					request, getPortletContext(), extra);
			mapping = actionMapper.getMapping(httpRequest, dispatcherUtils
					.getConfigurationManager());
		}

		if (mapping == null) {
			throw new StrutsException(
					"Unable to locate action mapping for request, probably due to "
							+ "an invalid action path: " + actionPath);
		}
		return mapping;
	}

}
