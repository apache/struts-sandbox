About OverDrive

OverDrive is a working whiteboard proposal for a new Struts subproject. 
The subproject will feature a set of best-practice applications written 
using a MVC framework that merges Struts with the Commons Chain of 
Responsiblity package.

----

Applications rule, frameworks drool.

Most application frameworks ship with one or more example applications. 
Struts has MailReader. iBATIS has Petstore. JSF has CarDemo. ASP.NET 
distributes several "starter kits". Some frameworks, like Spring, even 
consider the examples to be "first-class citizens".

From the OverDrive perspective, the applications are not just first-class 
citizens, they are the only first-class citizens. The MVC framework shared 
by the applications is simply a means to an end.

OverDrive is about writing business applications, regardless of platform, 
and extracting components the applications can share. The initial OverDrive 
applications are being written for ASP.NET/Mono, but versions for Java5 and 
PHP5 are expected.

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

* Use Subversion to checkout OverDrive from the 
   [Apache repository|http://svn.apache.org/viewcvs.cgi/struts/sandbox/trunk/overdrive/].

* If you are using [TortoiseSVN|http://tortoisesvn.tigris.org/] (recommended) 
** Create a likely subdirectory (e.g. /projects/Struts/OverDrive)
** Right-click on the sudirectory and select Checkout
** Enter the URL: [https://svn.apache.org/repos/asf/struts/sandbox/trunk/overdrive]
*** Be sure the destination path is set to the folder you created!

* After the checkout:
** [Download the external dependencies|
   http://opensource2.atlassian.com/confluence/oss/download/attachments/442/overdrive-local-cache.zip] 
   (local-cache) to the same directory.
** Unzip this archive to the same directory, to create the folder local-cache 
   with several subfolders.
*** local-cache
**** Agility
**** iBatisNet
**** NUnitAsp
**** Nexus
**** SpringNet 
** This archive may contain development versions of some products, 
   so be sure to use these rather than the released assemblies. 
   Our goal is to stay current with the development versions for now.
** If you have not already done so, also install NUnit 2.2 or later. 
   There is a MSI available from  [http://NUnit.org], along with a Mono-friendly ZIP. 

----

See also 
* [VStudio Readme]
* [Database Readme]


VISUAL STUDIO

h2. NUnit

* To run the NUnit Tests (v2.2)
** Configure the Test project to run NUnit
*** Right Click on "Tests", select "Properties"
*** Open "Configuration Properties"
*** Open "Debugging"
*** Change "Debug Mode" to "Program"
*** Press Apply 
*** For "Start Application", browse to your instance of "nunit-gui.exe".

* We recommend using the TestDriven.Net plugin rather than the NUnit GUI.
** [http://www.testdriven.net/]

h2. Agility 

* Build Agility first

h2. Nexus

* The Nexus solution has a web project. To allow running the project in-place: 
** Right-click on the "Nexus/Web" folder 
** Open "Sharing" and "Security/Web Sharing"
** Set the sharename to "Nexus"
* Build Agility before building Nexus

h2. PhoneBook

* The PhoneBook solution has a web project. To allow running the project in-place: 
** Right-click on the "PhoneBook/Web" folder 
** Open "Sharing" and "Security/Web" Sharing
** Set the sharename to "Phonebook"
* Build Nexus (and Agility) before building PhoneBook

h2. Subversion 

* We recommend using the Ankh plugin for Subversion.
** [http://ankhsvn.tigris.org/]

----

See also

* [Subversion Readme]
* [Database Readme]


DATABASE

* Right now, the PhoneBook application is using a MySQL 4.0 database by default. 
** We mean to change the default that to [SharpHSQL|
   http://www.c-sharpcorner.com/database/SharpHSQL.asp] Real Soon Now, 
   and make it easy to switch between various database systems. 
** But for now, you will need [MySQL 4.0|
   ftp://mirror.mcs.anl.gov/pub/mysql/Downloads/MySQL-4.0/] installed. 
   Then, all you need to do is execute the {{Phonebook-Start.sql}} script

{code:sql}
CREATE DATABASE `phonebook`;
USE `phonebook`;
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
INSERT INTO `entry` (`pk_entry`,`last_name`,`first_name`,`extension`,`user_name`,`editor`,`hired`,`hours`) VALUES 
 ('c5b6bbb1-66d6-49cb-9db6-743af6627828','Clinton','William','5557437828','bubba',0,'1992-08-19 00:00:00',37.5),
 ('7c424227-8e19-4fb5-b089-423cfca723e1','Roosevelt','Theodore','5557438942','bull',0,'2001-09-14 00:00:00',37.5),
 ('9320ea40-0c01-43e8-9cec-8fb9b3928c2c','Kennedy','John F.','5557433928','fitz',0,'1987-05-29 00:00:00',37.5),
 ('3b27c933-c1dc-4d85-9744-c7d9debae196','Pierce','Franklin','5557437919','hawkeye',0,'1984-11-18 00:00:00',35),
 ('554ff9e7-a6f5-478a-b76b-a666f5c54e40','Jefferson','Thomas','5557435440','monty',0,'1976-07-04 00:00:00',37.5);
{code}

----

See also 

* [Subversion Readme] 
* [VStudio Readme]

----

For more help, visit http://opensource.atlassian.com/confluence/oss/display/OVR/Home

----
