About OverDrive

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

Installing a development copy of OverDrive 

SUBVERSION

* Use subversion to checkout OverDrive from the Apache repository

* If you are using [TortoiseSVN|http://tortoisesvn.tigris.org/] (recommended) 
** Create a likely subdirectory (e.g. /projects/Struts/OverDrive)
** Right-click on the sudirectory and select Checkout
** Enter the URL: [https://svn.apache.org/repos/asf/struts/sandbox/trunk/overdrive]
*** Be sure the destination path is set to the folder you created!

* After the checkout:
** Download the external dependencies to the same directory from [http://people.apache.org/~husted/overdrive-external-bin.zip]
** Unzip this archive to the same directory, to create the folders 
*** iBatisNet.Bin, NUnitAsp.bin, SpringNet.bin. 
** This archive contains development versions, so be sure to use these rather than the released versions. Our goal is to stay current with the development versions for now.
** If you have not already done so, also install NUnit 2.2 or later. There is a MSI available from  [http://NUnit.org], along with a Mono-friendly ZIP. 

VISUAL STUDIO

h2. NUnit

* To run the NUnit Tests (v2.2)
** Configure the Test project to run NUnit
*** Right Click on Tests, select Properties
*** Open Configuration Properties
*** Open Debugging
*** Change Debug Mode to to Program
*** Press Apply 
*** For Start Application, browse to your instance of nunit-gui.exe

h2. PhoneBook

* The PhoneBook application has a web project. To allow running this in-place: 
** Right-click on the PhoneBook/Web folder 
** Open Sharing and Security/Web Sharing
** Set the sharename to PhoneBook

DATABASE

* Right now, the PhoneBook application is using a MySQL 3.x database by default. We mean to change that to SharpHSQL Real Soon Now [http://www.c-sharpcorner.com/database/SharpHSQL.asp]. But for now, you need MySQL 3.x installed. 
** Create a database named phonebook
** Create an entry table 

# Database: phonebook
# Table: 'entry'
# 
CREATE TABLE `entry` (
  `pk_entry` char(36) NOT NULL default '',
  `last_name` char(18) NOT NULL default '',
  `first_name` char(18) NOT NULL default '',
  `extension` char(18) NOT NULL default '',
  `user_name` char(9) NOT NULL default '',
  `editor` tinyint(3) unsigned NOT NULL default '0',
  `hired` datetime NOT NULL default '0000-00-00 00:00:00',
  `hours` double NOT NULL default '37.5'
) TYPE=MyISAM ROW_FORMAT=FIXED; 

----

For more help, visit http://opensource.atlassian.com/confluence/oss/display/OVR/Home

----

