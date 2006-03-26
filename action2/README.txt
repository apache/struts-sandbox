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

----------------------------------------------------------------------------

STATUS - MAILREADER

* Work in progress.

----

Welcome

Nominal
+ Logon - Cancel
+ Register - Cancel

Issues
* Powered image not displaying.

----

Logon

Nominal
+ Cancel
+ Reset
- Submit (invalid) (*)
+ Submit (incorrect)
+ Submit

Issues
* Submit (invalid)
** The "errors.password.mismatch" is not being resolved as message

----

Registraton Edit

Nominal
+ Cancel
+ Reset
- Submit (no change) (*)
- Submit (change) (*)
- Submit (invalid change) (*)

Issues
* Submit - no change
** Password is displayed in plain text
** Is there a WW way to set the focus?
* Edit - Submit (change)
** Password doesn't change when edited
** Password Confirmation message not displayed
* Edit = Submit (invalid change) (*)
** When client-side validation is enabled, messages stack up on multiple invalid submits. Sever-side only OK.
*** This doesn't happen with Logon page

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
  Submit (bad data)

----

Logoff

Nominal
* Logoff - Refresh
* Logoff - Skip to Registeration page (*)

Issues
* Skip to Registration - Displays blank Main Menu; Edit defaults to Create

----

Registration Create

Nominal
+ Cancel
+ Reset
- Submit (no data)  (*)
+ Submit (invalid data)
+ Submit (data)
- Submit (duplicate data) (*)

Issues (*)
* Submit
** Not parsing message variables: {0} in the range {1}
* Submit (duplidate data)
** Fails silently for duplicate user name
** Password Confirmation message not displayed
* Submit (invalid data)
** When client-side validation is enabled, messages stack up on multiple invalid submits. Sever-side only OK.

----

Locale change
* TODO

----

Tour
* TODO

----

Error
* Need to log and present unexpected exceptions

----



====
