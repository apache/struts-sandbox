flow.load("/templates/template.js");

function doView() {

  var random =  Math.round( Math.random() * 9 ) + 1;
  var hint = "No hint for you!"
  var guesses = 0;

  while (true) {

    // send guess page to user and wait for response
    forwardAndWait("/WEB-INF/guess/guess.jsp", 
       { "random"  : random, 
         "hint"    : hint,
         "guesses" : guesses} );

    // process user's guess
    var guess = parseInt( flow.context.param.guess );
    guesses++;
    if (guess) {
      if (guess > random) {
        hint = "Nope, lower!"
      } 
      else if (guess < random) {
        hint = "Nope, higher!"
      } 
      else {
        // correct guess
        break;
      }
    }
  }

  // send success page to user
  renderTemplate("/WEB-INF/guess/success.jsp", 
     {"random"  : random, 
      "guess"   : guess, 
      "guesses" : guesses} );
}


function renderTemplate(page, bizdata) { 
       res = flow.context.response;
       stream = flow.context.context.getResourceAsStream("/WEB-INF/guess/"+page+".jt");
       if (stream != null) {
           text = new String(stream.getText());
           html = text.process(bizdata);
           res.contentType = "text/html";
           res.writer.print(html);
           res.writer.close();
       } else {
           res.sendError(res.SC_INTERNAL_SERVER_ERROR, "Unable to find page "+page);
       }    
       
       suicide();
   }
}

// This function intercepts the forward back to Struts and renders the content
// directly using Javascript Templates
function _renderTemplateAndWait(page, bizdata) { 
   // if rpc call, use old forward, otherwise use template
   if (page == "n/a") {
       return _oldForwardAndWait(page, bizdata);
   } else {
       var k = new Continuation();
       
       // Use default ttl value from continuation manager
       var timeToLive = 0;
       var kont = new WebContinuation(flow, k, lastContinuation, timeToLive);
       
       bizdata.contid = kont.id;
       renderTemplate(page, bizdata);
       return kont;
   }
}

// Replace old forward method with ours, but keep a reference to it so we can
// call it for remote flow responses
this._oldForwardAndWait = _forwardAndWait;
this._forwardAndWait = _renderTemplateAndWait;
