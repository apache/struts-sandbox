
Shale Blank Archetype
=====================

This directory contains the Shale Blank Archetype for Maven 2.

To build and install the archetype in your local repository:

   $ svn co http://svn.apache.org/repos/asf/struts/sandbox/trunk/archetypes
   $ cd archetypes/shale-blank
   $ mvn install

To use the archetype to create a blank project:

   $ cd ~/projects
   $ mvn archetype:create 
         -DarchetypeGroupId=struts
         -DarchetypeArtifactId=struts-archetype-shale-blank 
         -DarchetypeVersion=1.0.1-SNAPSHOT 
         -DgroupId=com.example
         -DpackageName=com.example.projectname
         -DartifactId=projectname
         
To build your new webapp:

   $ cd projectname
   $ mvn package

To start Tomcat 5.5 and deploy your webapp:

   $ mvn cargo:start

   Open a browser and visit http://localhost:8080/projectname

Prerequisites
-------------

   Maven 2 from http://maven.apache.org

Cargo Configuration
-------------------

   The Cargo plugin is configured to download Apache Tomcat 5.5.15.

   To use a locally installed version, in pom.xml,
      1. Uncomment the <home> element and change the path
      2. Remove the <zipUrlInstaller> section.

Other Issues
------------


FAQs
----

Q:  

A:  
