OverDrive is a working whiteboard proposal for a new Struts subproject. The subproject will feature a set of best-practice applications written using a MVC framework that merges Struts with the Commons Chain of Responsiblity package.

----

Applications rule, frameworks drool.

Most application frameworks ship with one or more example applications. Struts has MailReader. iBATIS has Petstore. JSF has CarDemo. ASP.NET distributes several "starter kits". Some frameworks, like Spring, even consider the examples to be "first-class citizens".

From the OverDrive perspective, the applications are not just first-class citizens, they are the only first-class citizens. The MVC framework shared by the applications is simply a means to an end.

OverDrive is about writing business applications, regardless of platform, and extracting components the applications can share. The initial OverDrive applications are being written for ASP.NET/Mono, but versions for Java5 and PHP5 are expected.

The first two applications on the billet are

    * PhoneBook – A single-table employee directory.
    * MailReader – A multi-table account listing.

Once these ship, others will follow, including

    * Examples – A coding reference to document common strategies.
    * Wicker - A shopping cart application.
    * Gavel - An online b2c auction application.

The MVC Framework behind the OverDrive applications bundles two major components:

    * Agility, a C# port of Commons Chain of Responsibility.
    * Nexus, an application controller built over Agility.

----

For More see http://204.157.11.160/docs/display/OVR/Home

