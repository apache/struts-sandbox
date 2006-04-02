GuessController = function() {
    
    this.play = function() {
    
      this.random =  Math.round( Math.random() * 9 ) + 1;
      this.hint = "No hint for you!"
      this.guesses = 0;
    
      while (true) {
    
        // send guess page to user and wait for response
        flow.wait();
    
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

    this.endGame = function() {}

    this.cheat = function() {
        this.guesses += 5;
        return {"secret":this.random, "guesses":this.guesses};
    }    
}
