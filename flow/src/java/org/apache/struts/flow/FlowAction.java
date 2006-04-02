/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.flow.core.javascript.ConversionHelper;
import org.apache.struts.flow.core.Interpreter;
import org.apache.struts.flow.json.JSONArray;
import org.apache.struts.flow.json.JSONSerializer;
import org.apache.struts.flow.ibatis.SqlMap;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/**  Executes scripts and continuations */
public class FlowAction extends Action {

    private static final Log log = LogFactory.getLog(FlowAction.class);
    
    /**
     *  Gets the interpreter when no script is specified
     *
     *@return    The interpreter value
     */
    protected Interpreter getInterpreter(String prefix) {
        return (Interpreter) servlet.getServletContext().getAttribute(FlowPlugIn.INTERPRETER_KEY+"/"+prefix);
    }
    
    /**
     *  Gets the interpreter for the requested script
     *
     *@param script The script to load the interpeter for
     *@return    The interpreter value
     */
    protected Interpreter getInterpreter(String prefix, String script) {
        log.debug("Trying to retrieve module-specific interpreter from context: "+FlowPlugIn.INTERPRETER_KEY+"/"+prefix);
        Map map = (Map)servlet.getServletContext().getAttribute(FlowPlugIn.INTERPRETER_KEY+"/"+prefix);
        return (Interpreter) map.get(script);
    }


    /**
     *  Handle by either starting a new Control Flow or continuing an existing
     *  one. The logic is:<br />
     *  - If request contains contid, then continue existing flow.<br />
     *  - Else if the action mapping parameter attribute has the contid, then
     *  continue the existing flow.<br />
     *  - Else start a new flow.<br />
     *  The name of the function to execute for a new flow should be specified
     *  in the <code>function</code> property of the custom action mapping
     *  class, <code>FlowMapping</code>
     *
     *@param  request        the request send by the client to the server
     *@param  response       the response send by the server to the client
     *@param  mapping        the action mapping
     *@param  form           the action form
     *@return                the action forward
     *@exception  Exception  If something goes wrong
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
             throws Exception {

        // Create and populate a Context for this request
        ServletWebContext context = new ServletWebContext();
        context.initialize(servlet.getServletContext(), request, response);
        context.put(Constants.ACTION_SERVLET_KEY,
                this.servlet);
        context.put(Constants.ACTION_CONFIG_KEY,
                mapping);
        context.put(Constants.ACTION_FORM_KEY,
                form);
        context.put(Constants.MESSAGE_RESOURCES_KEY,
                getResources(request));
        context.put(Constants.ACTION_KEY, this);

        String contid = request.getParameter("contid");
       
        // A FlowCall means the request came from client-side javascript that
        // expects the return type to be JSON
        boolean isFlowCall = (request.getParameter("FlowCall") != null);
        String func = getProperty("function", request, mapping);
        String controller = getProperty("controller", request, mapping);
        
        if ("true".equals(FlowConfiguration.getInstance().getProperty("flow.devMode"))) {
            SqlMap.reloadConfig();
        }
       
        Forward forward = new Forward();
        forward.setModule(mapping.getModuleConfig().getPrefix());
        forward.setAction(func);
        forward.setController(controller);
        context.put(Constants.FORWARD_KEY, forward);

        Interpreter interp = null;
        String scriptPattern = getProperty("script", request, mapping);
        if (scriptPattern == null) {
            interp = getInterpreter(mapping.getModuleConfig().getPrefix());
        } else {
            interp = getInterpreter(mapping.getModuleConfig().getPrefix(),
                forward.toUri(scriptPattern));
        }

        if (contid == null || contid.length() == 0) {

            // --- start a new flow

            List args = new LinkedList();

            if (func == null || func.length() == 0) {
                throw new ServletException("You must specify a function name to call");
            } else {
                
                String id = mapping.getProperty("id");
                if (id != null) {
                    args.add(new Interpreter.Argument("id", id));
                }
               
                // modify controller name
                StringBuffer sb = new StringBuffer();
                sb.append(Character.toUpperCase(controller.charAt(0)));
                sb.append(controller.substring(1));
                sb.append("Controller");
               
                // call control script function
                interp.callController(sb.toString(), func, args, context);
                
                return dispatchToPage(request, response, mapping, forward);
            }
        } else {
            // --- continue an existing flow

            // validate JSON in the body of a flowcall
            if (isFlowCall) {
                StringBuffer sb = new StringBuffer();
                char[] buffer = new char[1024];
                int len = 0;
                BufferedReader reader = request.getReader();
                while ((len = reader.read(buffer)) > 0 ) {
                    sb.append(buffer, 0, len);
                }
                String json = sb.toString();
                if (log.isDebugEnabled()) {
                    log.debug("processing json:"+json);
                }
                if (isValidJSON(json)) {
                    context.put("json", json);
                }
                
            }
            
            interp.handleContinuation(
                    request.getParameter("contid"), new LinkedList(), context);

            if (isFlowCall) {
                String json = new JSONSerializer().serialize(forward.getBizData());
                if (log.isDebugEnabled()) {
                    log.debug("returning json: "+json);
                }
                response.getWriter().write(json);
                response.getWriter().flush();
                response.getWriter().close();
                return null;
            } else {     
                return dispatchToPage(request, response, mapping, forward);
            }
        }
    }

    
    protected String getProperty(String name, HttpServletRequest req, ActionMapping mapping) {
        String value = req.getParameter(name);
        if (value == null) {
            value = mapping.getProperty(name);
        }
        return value;
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
    private ActionForward dispatchToPage(
            HttpServletRequest request, HttpServletResponse response, ActionMapping mapping, Forward forward)
             throws ServletException, IOException {

        // Probably only need to process if the response hasn't already been committed.  This
        // should let flow code be able to completely handle a request if desired.
        if (!response.isCommitted()) {
            request.setAttribute("contid", forward.getContid());

            Map atts = forward.getBizData();
            if (atts != null) {
                Iterator attkeys = atts.keySet().iterator();
                while (attkeys.hasNext()) {
                    String attkey = (String) attkeys.next();
                    request.setAttribute(attkey, ConversionHelper.jsobjectToObject(atts.get(attkey)));
                }
            }    

            ActionForward af = null;
            if (forward.isRedirect()) {
                ActionForward redirect = mapping.findForward("redirect");
                af = new ActionForward(forward.toUri(redirect.getPath()));
                af.setRedirect(true);
            } else {
                af = mapping.findForward(forward.getUri());
                if (af == null) {
                    ActionForward normal = mapping.findForward("forward");
                    af = new ActionForward(forward.toUri(normal.getPath()));
                }
            }
            return af;
        }   
        return null;
    }
       
    protected String createPath(String pattern, Map vars) {

        StringBuffer path = new StringBuffer();
        char c;
        Object val;
        for (int x=0; x<pattern.length(); x++) {
            c = pattern.charAt(x);
            if (c == '%') {
                c = pattern.charAt(++x);
                val = vars.get(String.valueOf(c));
                if (val != null) {
                    path.append(val.toString());
                } else {
                    path.append('%');
                    path.append(c);
                }
            } else {
                path.append(c);
            }
        }
        return path.toString();
    }
    
    private boolean isValidJSON(String val) {
        try {
            if (val != null && val.length() > 1) {
                JSONArray obj = new JSONArray(val);
                if (log.isDebugEnabled()) {
                    log.debug("Valid JSON");
                }
                return true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("No JSON detected");
                }
            }
        } catch (Exception ex) {
            log.warn("Invalid JSON object", ex);
        }
        return false;
    }
        


    // These methods seem necessary as some scripting engines are not able to
    // access Action's protected methods.

    /**
     *  Saves a token
     *
     *@param  req  The request object
     */
    public void saveToken(HttpServletRequest req) {
        super.saveToken(req);
    }


    /**
     *  Checks to see if the request is cancelled
     *
     *@param  req  The request object
     *@return      True if cancelled
     */
    public boolean isCancelled(HttpServletRequest req) {
        return super.isCancelled(req);
    }


    /**
     *  Checks to see if the token is valid
     *
     *@param  req  The request object
     *@return      True if valid
     */
    public boolean isTokenValid(HttpServletRequest req) {
        return super.isTokenValid(req);
    }


    /**
     *  Resets the token
     *
     *@param  req  The request object
     */
    public void resetToken(HttpServletRequest req) {
        super.resetToken(req);
    }


    /**
     *  Saves the messages to the request
     *
     *@param  req  The request object
     *@param  mes  The action messages
     */
    public void saveMessages(HttpServletRequest req, ActionMessages mes) {
        super.saveMessages(req, mes);
    }


    /**
     *  Saves the errors to the request
     *
     *@param  req   The request object
     *@param  errs  The action errors
     */
    public void saveErrors(HttpServletRequest req, ActionErrors errs) {
        super.saveErrors(req, errs);
    }

}

