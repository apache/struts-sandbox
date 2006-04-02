GuessController = function() {
    
    this.play = function() {
    
      this.random =  Math.round( Math.random() * 9 ) + 1;
      this.hint = "No hint for you!"
      this.guesses = 0;
    
      while (true) {
    
        // send guess page to user and wait for response
        flow.wait("play", this);
    
        // process user's guess
        this.guess = parseInt( params.guess );
        this.guesses++;
        if (this.guess) {
          if (this.guess > this.random) {
            this.hint = "Nope, lower!"
          } 
          else if (this.guess < this.random) {
            this.hint = "Nope, higher!"
          } 
          else {
            // correct guess
            break;
          }
        }
      }
    
      // send success page to user
      flash.guesses = this.guesses;
      flash.random = this.random;

      flow.redirect( { "action" : "endGame" } );
    }

    this.endGame = function() {
        renderTemplate("endGame", this);
    }

    this.cheat = function() {
        this.guesses += 5;
        return {"secret":this.random, "guesses":this.guesses};
    }    
}


// This function renders the content directly using Javascript Templates
function renderTemplateAndWait(page, bizdata, ttl) {
    var cont = new FOM_WebContinuation(new Continuation(), flow.continuation, ttl);
    bizdata.contid = cont.id;
    renderTemplate(page, bizdata);
    flow.forward(null, bizdata, cont);
    FOM_Flow.suicide();
}

function renderTemplate(page, bizdata) {
    var res = flow.context.response;
    var stream = struts.servletContext.getResourceAsStream("/WEB-INF/templates/views/guess/"+page+".jt");
    if (stream != null) {
        var text = new String(stream.getText());
        var html = text.process(bizdata);
        res.writer.print(html);
        res.writer.close();
    } else {
        res.sendError(res.SC_INTERNAL_SERVER_ERROR, "Unable to find page "+page);
    }
}

FOM_Flow.prototype._wait=renderTemplateAndWait;
