This directory is a whiteboard area for working on new material related to
the adoption of WebWork2 as Action2.

While WebWork2/Action2 is passing through the incubator, this material is
being based on OS WebWork 2.2.2.

Note this area is intended for *NEW* material only. Existing material must
enter Apache Struts through the incubation process.

At this time, the material is being built directly with IDEA, and a standard
build file is not yet available.

For the time being, the dependencies required to build this material may be
downloaded here:

* http://people.apache.org/~husted/sandbox-action2-lib.zip

For more about the materials under construction, visit

* http://www.StrutsUniversity.org/Application+Use+Cases

----------------------------------------------------------------------------

APPS

Under the apps folder, two applications are now being constructed

 * Cookbook, and
 * Mailreader

The goal of the Cookbook is to combine the WebWork 2.2.2  Showcase with the
Struts 1.3 Examples and the Struts 1.3 Cookbook.

The Cookbook demonstrates working code and provides links to display the
code for each example.

The goal of the Mailreader is to provide a simple application that
demonstrates best practices.

The Mailreader is a simple parent/child workflow, where visitors can register
with the application and the create child records related to the user's
master account.

Other new applications may include the iBATIS JPetstore example,
which was based on Struts Action 1.

Other WebWork example applications, like the shopping cart, could also be
kept here.

----------------------------------------------------------------------------

STATUS - COOKBOOK

* Several examples have been added.

* The "Select" example could be streamlined to use one example of each
Select control.

* Other examples can be added at will.

----

Hmmmm ... 

* Are the validator objects singletons?


----

Example idea jar

* See http://www.niallp.pwp.blueyonder.co.uk/strutsvalidatorextends.html
for some validation use cases.

* http://wiki.opensymphony.com/display/WW/Cookbook


* Display an unexpected exception on an error page. 
** What's the conditional logic using JSP tags?

* "param->prepare->param interceptor pattern"
** http://forums.opensymphony.com/thread.jspa?threadID=23750&tstart=0

* Using proxy objects to restrict access to domain objects
** http://forums.opensymphony.com/thread.jspa?threadID=23750&tstart=0

* Using an authentification interceptor. 
** 

* Overriding a bundled UI Tag. 

* Creating a custom UI Tag.

* Canceling a form with client-side validation.
** (Should there be a "validate" attribute to generate the "form.onsubmit=null" script?)
** (Should ActionSupport provide "public String cancel() {return CANCEL;} ?
** (Should we support action="!cancel" to be consistent with form tag.)
*** (Trying !cancel in WW2.2.2 carries cancel over to the next action.)

* Setting form type to "POST"
** (Should POST be the default?)

* Switching to SSL after login
** http://forums.opensymphony.com/thread.jspa?messageID=11452&#11452

* Customizing pages for multiple installations across multiple releases
** http://forums.opensymphony.com/thread.jspa?messageID=23991&#23991

----

Examples that might involve new development

* How do we set checkboxes false (on uncheck)?
** http://forums.opensymphony.com/thread.jspa?threadID=23601&tstart=0

* How to set the focus on a form field?
** http://forums.opensymphony.com/thread.jspa?threadID=23777&tstart=0

*  Populating POJO that implements an Interface 
** http://forums.opensymphony.com/thread.jspa?threadID=23750&tstart=0

*  Wizard
** http://forums.opensymphony.com/thread.jspa?threadID=23778&tstart=15

* DRY UI Tags 
** http://forums.opensymphony.com/thread.jspa?threadID=24140&tstart=0

* Default package names 
** Allow a default package name to be set for Action classes
   so that .MyAction could resolve to org.mycorp.myapp.mypackage.MyAction 

* Proxy Result 
** http://forums.opensymphony.com/thread.jspa?threadID=23621&tstart=0


----------------------------------------------------------------------------

STATUS - MAILREADER

* Feature complete, but some marginal issue remain. 
* Could use a screen shot next to the code for each page.

----

Welcome

Nominal
+ Logon - Cancel
+ Register - Cancel

----

Logon

Nominal
+ Cancel
+ Reset
- Submit (invalid) 
+ Submit (incorrect)
+ Submit

----

Registraton Edit

Nominal
+ Cancel
+ Reset
+ Submit (no change)
+ Submit (change) 
+ Submit (invalid change) 

Issues
* When client-side validation is enabled, messages stack up on multiple invalid submits. Sever-side only OK.
*** This doesn't happen with Logon page, only Registration and Subscription (!?)(
*** Asked on forum - http://forums.opensymphony.com/thread.jspa?threadID=23871&tstart=0

----

Subscription Edit

Nominal
+ Cancel
+ Save (no changes)
+ Save (changes)

----

Subscription Delete

Norminal
+ Cancel
+ Confirm

----

Subscription Add
+ Cancel 
+ Submit 
+ Submit (bad data)
+ Double submit

----

Logoff

Nominal
+ Logoff - Refresh
+ Logoff - Skip to Registeration page

----

Registration Create

Nominal
+ Cancel
+ Reset
+ Submit (no data)
+ Submit (invalid data)
+ Submit (data)
+ Submit (duplicate data)
+ Double submit

----

Locale change
+ Change locale from Welcome page.
+ Buttons and Labels reflect changed locale

----

Tour
* In progress

----

Error
+ Need to log and present unexpected exceptions


Issue 
* If the error page is fired by a global exception handler, is the exception going to be logged. Do we need to use a chain result to an action that would log the exception? What would be the Java syntax?

* It would be nice to omit the message markup if there is not message. 
** http://forums.opensymphony.com/thread.jspa?threadID=7480&messageID=16618#16618

Things that didn't work:

<saf:if test=" %(exception.message}?exists">

<saf:if test=" %this.exception.message?exists">

<saf:if test=" #(exception.message} != null ">

----

ApplicationListener
* Another approach would be to instantiate the database via Spring.


----

VerifyResourcesInterceptor
* Could use a better test to detect whether message resources loaded

====
