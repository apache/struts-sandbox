flow.load("/templates/template.js");

importClass(Packages.javax.portlet.PortletURL);

// This function intercepts the forward back to Struts and renders the content
// directly using Javascript Templates
function processTemplateAndWait(page, bizdata) { 
   // if rpc call, use old forward, otherwise use template
   if (page == "n/a") {
       return _oldForwardAndWait(page, bizdata);
   } else {
       var k = new Continuation();
       
       // Use default ttl value from continuation manager
       var timeToLive = 0;
       var kont = new WebContinuation(flow, k, lastContinuation, timeToLive);
       
       bizdata.contid = kont.id;
       processTemplate(page, bizdata); 
       suicide();
       return kont;
   }
}

function processTemplate(page, bizdata) {
   res = flow.context.response;
   stream = flow.context.context.getResourceAsStream(page);
   if (stream != null) {
       text = new String(stream.getText());
       html = text.process(bizdata);
       res.contentType = "text/html";
       res.writer.print(html);
       res.flushBuffer();
       res.writer.close();
   } else {
       res.sendError(res.SC_INTERNAL_SERVER_ERROR, "Unable to find page "+page);
   }    
}

function renderUrl(params) {
    url = flow.context.response.createRenderURL();    
    if (params && params.params) {
        for (x in params.params) {
            url.setParameter(x, params.params[x]);
        }
    }
    return url.toString();
}

function actionUrl(params) {
    url = flow.context.response.createActionURL();    
    if (params && params.params) {
        for (x in params.params) {
            url.setParameter(x, params.params[x]);
        }
    }
    return url.toString();
}
    

// Replace old forward method with ours, but keep a reference to it so we can
// call it for remote flow responses
this._oldForwardAndWait = _forwardAndWait;
this._forwardAndWait = processTemplateAndWait;
