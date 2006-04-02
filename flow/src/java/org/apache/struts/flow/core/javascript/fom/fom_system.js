/*
* Copyright 1999-2004 The Apache Software Foundation
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
*/
FOM_Flow.suicide = new Continuation();

FOM_Flow.prototype.forwardAndWait = function(uri, bizData, fun, ttl) {
    
    this.forward(uri, bizData,
                  new FOM_WebContinuation(new Continuation(), 
                                          this.continuation, ttl));
    if (fun) {
        if (!(fun instanceof Function)) {
            throw "Expected a function instead of: " + fun;
        }
        fun();
    }
    FOM_Flow.suicide();
}


FOM_Flow.prototype.wait = function(uri, bizData, ttl, fun) {
    this._wait(uri, bizData, ttl, fun);
    var funcName = params.FlowCall;
    if (funcName) {
        var func = controller[funcName];
        if (func) {
            var json;
            eval("json = "+flow.context.json);
            var ret = func.call(controller, json);
            this.forward(null, ret);
            FOM_Flow.suicide();
        } else {
            flow.log.error("Unable to locate function "+funcName+
                " on controller "+typeof(controller));
        }        
    } else {
        flow.log.debug("Not a flow call");
    }    
}

FOM_Flow.prototype._wait = function(uri, bizData, ttl, fun) {
    this.forward(uri, bizData,
                  new FOM_WebContinuation(new Continuation(), 
                                          this.continuation, ttl));
    if (fun) {
        if (!(fun instanceof Function)) {
            throw "Expected a function instead of: " + fun;
        }
        fun();
    }
    
    FOM_Flow.suicide();
}

FOM_Flow.prototype.redirect = function(options) {
    var obj = {}
    
    // This set of logic allows the following arguments:
    // redirect("http://foo");
    // redirect({ action: "foo", controller: "bar"})
    if (typeof(options) == 'string') {
        obj.uri = arguments[0];
    } else {
        obj = options;
    }
    
    obj.redirect = true;
    
    this.forward(obj, null, null, null);
}


FOM_Flow.prototype.handleContinuation = function(k, wk) {
    k(wk);
}

FOM_Flow.prototype.createWebContinuation = function(ttl) {
   var wk = this.makeWebContinuation(new Continuation(), ttl);
   wk.setBookmark(true);
   return wk;
}

/**
 * Exit the current flowscript invocation.
 * <p>
 * There are some flowscript use cases where we want to stop the current 
 * flowscript without creating a continuation, as we don't want the user 
 * to go back to the script.
 * <p>
 * An example is a "login" function where the caller function expects this 
 * function to exit only if login is successful, but that has to handle 
 * e.g. a registration process that includes a "cancel" button.
 */
FOM_Flow.prototype.exit = function() {
    FOM_Flow.suicide();
}


// This function renders the content directly using Javascript Templates
FOM_Flow.prototype.renderAndWait = function(page, bizdata, ttl) { 
   var cont = new FOM_WebContinuation(new Continuation(), 
                                          this.continuation, ttl)
   bizdata.contid = cont.id;
   render(page, bizdata);
   this.forward(null, bizdata, cont);
   FOM_Flow.suicide();
}

FOM_Flow.prototype.render = function(page, bizdata) {
   var res = this.context.response;
   var stream = struts.servletContext.getResourceAsStream("/WEB-INF/templates/"+page+".jt");
   if (stream != null) {
       var text = new String(stream.getText());
       var html = text.process(bizdata);
       res.writer.print(html);
       res.writer.close();
   } else {
       res.sendError(res.SC_INTERNAL_SERVER_ERROR, "Unable to find page "+page);
   }  
}


