README.txt - mailreader-bang 

The MailReader demonstrates a localized application with a master/child 
CRUD workflow. 

This rendition also demonstrates switching from the "bang" syntax for 
invoking dynamic methods to a general-purpose wild card approach. 

To switch between approaches, edit the struts.xml file to include either 
the struts-bang.xml file OR the struts-wildcard.xml. (But not both.)

When using the -bang application, be sure that the 
struts.enable.DynamicMethodInvocation property is set to "true".

For the -wilcard application. be sure that the 
struts.enable.DynamicMethodInvocation property is set to "false". 

See the Sandbox for other MailReader examples using other architectures. 

* http://svn.apache.org/viewvc/struts/sandbox/trunk/struts2/apps/

For more about the MailReader applicaton genneraly, visit Struts University.

* http://www.StrutsUniversity.org/

----------------------------------------------------------------------------