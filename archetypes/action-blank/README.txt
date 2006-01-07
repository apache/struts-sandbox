
Struts Action Blank Archetype
=============================

This directory contains the Struts Action Blank Archetype for Maven 2.

To build and install the archetype in your local repository:

   $ svn co http://svn.apache.org/repos/asf/struts/sandbox/trunk/archetypes
   $ cd archetypes/action-blank
   $ mvn install
   
To use the archetype to create a blank project:

   $ cd ~/projects
   $ mvn archetype:create 
         -DarchetypeGroupId=struts
         -DarchetypeArtifactId=struts-archetype-action-blank 
         -DarchetypeVersion=1.3.0-SNAPSHOT 
         -DgroupId=com.example
         -DpackageName=com.example.projectname
         -DartifactId=my-webapp
         
To build your new webapp:

   $ cd my-webapp
   $ mvn war:war


Prerequisites
-------------

In order to build the archetype (and the webapp created from it) you will need
the Struts Action, Taglib and Tiles jars in your local Maven 2 repository.

You can accomplish this by building the artifacts with Maven 2:

$ svn co http://svn.apache.org/repos/asf/struts/current struts/current
$ cd struts/current/build
$ mvn install

Alternately, download the nightly builds and install each file as follows:

   $ mvn install:install-file 
         -Dfile=/path/to/struts-action-1.3.0-dev.jar
         -DgroupId=org.apache.struts
         -DartifactId=struts-action
         -Dversion=1.3.0-SNAPSHOT
         -Dpackaging=jar
         -DgeneratePom=true

Repeat for Tiles and Taglib.


Other Issues
------------


FAQs
----

Q:  

A:  
