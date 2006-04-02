/*
 *  This file provides a Wizard object that can be used to easily implement
 *  multi-page workflows, commonly called wizards.  The following features 
 *  are built into the framework:
 *
 *  - Pluggable validation
 *  - Pluggable data population and post-population processing
 *  - Automatic forward/backward navigation handling
 *  - Capable of handing any model (POJO, DynaBean, JDOM Element, etc)
 *
 *  To insert your own logic, particularly validation and population logic, 
 *  simply the desired methods with your own.
 */

 /**
  *  Constructor for the Wizard.
  *
  * @param model    The data model object to store form data
  */
function Wizard(model) {
    
    // The data model, can be anything
    this.model = model;
    
    // The time to live for forms, defaults to continuation manager settings
    this.timeToLive = 0;
    
    // The key the model is placed under
    this.modelKey = "form";
    
    // The key the errors are placed under
    this.errorsKey = "errors";
    
    // The name of the request parameter signifying the "Next" action
    this.nextId = "next";
}

/**
 *  Called to populate the model with the request
 */
Wizard.prototype.populate = function() {}

/**
 *  Called to handle any post-processing after populate has been called
 */
Wizard.prototype.postPopulate = function() {}

/**
 *  Called to validate the form
 *
 * @param frmName    The name of the form to validate
 * @return           Any errors, null if none
 */
Wizard.prototype.validate = function(frmName) {}

/**
 *  Called to prepare the model right before the form is sent.
 *
 * @return The model to include in the business data passed to the form
 */
Wizard.prototype.prepareModel = function() {return this.model;}

/**
 *  Creates a continuation, saves the context, and sends the form.  Shouldn't
 *  need to be called from outside the Wizard class.
 *
 * @param name         The name of the form to send
 * @param lastWebCont  The parent web continuation
 * @param bizdata      A map of objects to pass to the form
 */ 
Wizard.prototype.sendFormAndWait = function(name, lastWebCont, bizdata, ttl) {
   flow.forward(name, bizdata, new FOM_WebContinuation(new Continuation(), lastWebCont, ttl));
   flow.exit();
}

/**
 * Shows the form, handling validation and navigation.  
 *
 * @param doValidate    Whether to validate or not (optional)
 * @param exitIds       An array of submit button names that allow "next" behavior
 * @return              The submit button name that was pressed
 */
Wizard.prototype.showForm = function(frm, bizdata, doValidate, exitIds) {
    
    // Default validation to true if not passed
    var doValidate = (doValidate == null ? true : doValidate);

    // Default to the next id if none passed
    var exitIds = (exitIds == null ? new Array(this.nextId) : exitIds);
    
    var lastWebCont = flow.continuation;
    // create a continuation, the invocation of which will resend
    // the page: this is used to implement the back button
    var wk = new FOM_WebContinuation(new Continuation(), lastWebCont);
    flow.log.debug("saving spot "+wk.id+" before form:"+frm);
    
    // Loop to keep showing form until validation passes and next button 
    // is pressed
    var keepShowing = true;
    var thisWebCont;
    var tmpModel;
    var exitId;
    while (keepShowing) {
        keepShowing = false;

        // Wraps the model before attaching to bizdata to allow any necessary
        // cloning or ActionForm wrapping
        tmpModel = this.prepareModel(this.model);
        bizdata[this.modelKey] = tmpModel;

        // Send the form and wait
        thisWebCont = this.sendFormAndWait(frm, wk, bizdata);

        // Populate the model with form submission
        this.populate();
        this.postPopulate();
        
        // If validation is enabled, validate and determine if should keep 
        // showing
        if (doValidate) {
            var errors = this.validate(frm);
            if (errors == null || errors.length == 0) {
                bizdata[this.errorsKey] = null;
            } else {
                bizdata[this.errorsKey] = errors;
                keepShowing = true;
            }
        }
        
        // Determine if next button is pressed and should stop showing
        if ((doValidate && !keepShowing) || !doValidate) {
            keepShowing = true;
            var params = struts.param;
            for (var id in exitIds) {
                if (params[exitIds[id]] != null || params[exitIds[id]+'.x'] != null) {
                    exitId = exitIds[id];
                    keepShowing = false;
                    break;
                }
            }    
        }
    }
    flow.continuationn = thisWebCont;
    return exitId;
}


/**
 *  This function is called to restart a previously saved continuation
 *  passed as argument.  Overrides the default handleContinuation to
 *  add support for back buttons.
 * 
 * @param kont The continuation to restart
 */
function handleContinuation(k, wk) {
    
    // This can be overridden by declaring a "prevId" variable outside the function
    var prevId = (this.prevId != null ? this.prevId : "prev");
    
    flow.log.debug("Previous Id:"+struts.param[prevId]+" cont:"+wk.id);
    if (struts.param[prevId]) {
        var cont = wk;
        for (var x=0; x<2; x++) {
            if (cont == null) {
                flow.log.error("can't get parent continuation, back "+x);
                break;
            } else {
                cont = cont.getParent();
            }
        }
        if (cont != null) {
            cont.continuation(cont);
        } else {
            k(wk);
        }
    } else {
        k(wk);
    }
}
flow.handleContinuation = handleContinuation;
