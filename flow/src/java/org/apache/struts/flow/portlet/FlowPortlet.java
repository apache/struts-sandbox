package org.apache.struts.flow.portlet;

import java.io.PrintWriter;
import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.RenderRequest;
import javax.portlet.*;
import javax.portlet.PortletException;

import org.apache.struts.flow.*;
import org.apache.struts.flow.core.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.flow.core.Factory;
import org.apache.struts.flow.core.javascript.fom.FOM_JavaScriptInterpreter;
import org.apache.struts.flow.core.Interpreter;
import org.apache.struts.flow.core.javascript.ConversionHelper;
import org.apache.struts.flow.core.DefaultFlowVariableFactory;
import org.apache.struts.flow.sugar.SugarWrapFactory;
import org.apache.commons.chain.*;
import org.apache.commons.chain.web.portlet.*;
import org.apache.struts.flow.core.source.impl.ChainSourceResolver;
import java.util.*;
import java.io.*;
import org.mozilla.javascript.Scriptable;

/**  Description of the Class */
public class FlowPortlet extends GenericPortlet {
    
    private CompilingInterpreter interp;
    
    public void init() {
        
        Factory.setLogger(new CommonsLogger());
        Factory.getContinuationsManager().setDefaultTimeToLive(10 * 60 * 1000);
        interp = createInterpreter();
        interp.register(getInitParameter("scriptPath"));
    }
    
    private CompilingInterpreter createInterpreter() {
        FOM_JavaScriptInterpreter interp = new FOM_JavaScriptInterpreter();
        interp.setSourceResolver(new ChainSourceResolver(new PortletWebContext(getPortletContext(), null, null)));
        interp.setDebugger(false);
        interp.setCheckTime(0);
        interp.setReloadScripts(true);
        interp.setWrapFactory(new SugarWrapFactory());
        interp.initialize();
        interp.register("/org/apache/struts/flow/core/javascript/fom/fom_system.js");
        interp.addFlowVariable("portlet", new DefaultFlowVariableFactory(Portlet.class));
        //interp.addVariableRegistrar(new DefaultCallVariableRegistrar(SqlMap.class, "sqlMap"));
        return interp;
    }
    
    /**
     *  The portlet's main view prints "Hello, World"
     *
     *@param  request               Description of the Parameter
     *@param  response              Description of the Parameter
     *@exception  PortletException  If anything goes wrong
     *@exception  IOException       If anything goes wrong
     */
    public void doView(RenderRequest request, RenderResponse response)
             throws PortletException, IOException {
        
        // Create and populate a Context for this request
        PortletWebContext context = new PortletWebContext();
        context.initialize(getPortletContext(), request, response);
                 
        String view = request.getParameter("view");
        String contid = request.getParameter("contid");
        
        String func = "doView";
        if (view != null) {
            func = view + "View";
        }
        
        if (contid == null || contid.length() == 0) {

            // --- start a new flow

            List args = new LinkedList();
            
            try {
                // call control script function
                interp.callFunction(func, args, context);
            } catch (Exception ex) {
                throw new PortletException("Unable to execute flow script", ex);
            }

            // retrieve page, continuation ID, and attributes from chain context
            // FIXME: commenting out for now
            //String page = (String) ConversionHelper.jsobjectToObject(context.get(Constants.FORWARD_NAME_KEY));
            //contid = (String) context.get(Constants.CONTINUATION_ID_KEY);
            //Scriptable bizdata = (Scriptable) context.get(Constants.BIZ_DATA_KEY);
            //Map atts = null;
            //if (bizdata != null) {
            //    atts = ConversionHelper.jsobjectToMap(bizdata);
            //}    
            //dispatchToPage(request, response, page, contid, atts);
        } else {
            // --- continue an existing flow

            // kick off continuation
            context.put("id", "5");
            
            try {
                interp.handleContinuation(contid, new LinkedList(), context);
            } catch (Exception ex) {
                throw new PortletException("Unable to execute flow script", ex);
            }

            // retrieve page, continuation ID, and attributes from chain context
            //String page = (String) context.get(Constants.FORWARD_NAME_KEY);
            //contid = (String) context.get(Constants.CONTINUATION_ID_KEY);
            //Scriptable bizdata = (Scriptable) context.get(Constants.BIZ_DATA_KEY);
            //Map atts = null;
            //if (bizdata != null) {
            //    atts = ConversionHelper.jsobjectToMap(bizdata);
            //}    
           
            //dispatchToPage(request, response, page, contid, atts);
        }
    }
    
    /**
     *  Add continuation ID and attributes to request scope, dispatch to page.
     *
     *@param  request            The request
     *@param  response           The response
     *@param  page               The action forward name
     *@param  contid             Continuation ID to be set in request.
     *@param  atts               Attributes to be set in request.
     *@param  mapping            The action mapping
     *@return
     *@throws  ServletException
     *@throws  IOException
     */
    private void dispatchToPage(RenderRequest request, RenderResponse response, 
            String page, String contid, Map atts) 
             throws PortletException, IOException {

        // Probably only need to process if the response hasn't already been committed.  This
        // should let flow code be able to completely handle a request if desired.
        if (!response.isCommitted()) {
            request.setAttribute("contid", contid);

            if (atts != null) {
                Iterator attkeys = atts.keySet().iterator();
                while (attkeys.hasNext()) {
                    String attkey = (String) attkeys.next();
                    request.setAttribute(attkey, ConversionHelper.jsobjectToObject(atts.get(attkey)));
                }
            }    

            PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(page);
            rd.include(request,response);
        }   
    }
}

