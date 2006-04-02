flow.load("/WEB-INF/wizard/controllers/wizard.js");

RegistrationController = function() {

    this.register = function() {
        var model = new java.util.HashMap();

        var wizard = new Wizard(model);
        wizard.populate = populate;
        wizard.validate = validate;
      
        wizard.showForm( { "action" : "name-form" }, {
                  "title" : "User Name Information"
                  });
        wizard.showForm( { "action" : "hobbies-form" }, {
                  "title" : "User Hobbies"
                  });
        wizard.showForm( { "action" : "summary-form" } , {
                  "title" : "User Summary"
                  });  
    };              
}

function populate() {
  var m = struts.paramValues;
  for (var i = m.keySet().iterator(); i.hasNext(); ) {
    var key = i.next();
    this.model.put(key, m.get(key)[0]);
  }
  // Bug in commons-chain prevents this
  //this.model.putAll(struts.paramValues);
}

function validate() {
  if (this.model.get("name").length() < 2) {
    return "Name must be specified";
  }
}

