
Struts Blank Archetype
======================

This directory contains the Struts Blank Archetype for Maven 2.

To build and install the archetype in your local repository:

   $ mvn install
   
To use the archetype to create a blank project:

   $ cd ~/projects
   $ mvn archetype:create 
         -DarchetypeGroupId=org.apache.struts.archetypes
         -DarchetypeArtifactId=struts-archetype-blank 
         -DarchetypeVersion=1.3.0-SNAPSHOT 
         -DgroupId=com.mypackage
         -DartifactId=my-webapp
         
To build your new webapp:

   $ cd my-webapp
   $ mvn war:war


Prerequisites
-------------

In order to build the archetype (and the webapp created from it) you will need
the Struts Action, Taglib and Tiles jars in your local Maven 2 repository.

You can accomplish this by building the artifacts with Maven 2, or by installing
the jars as follows:

   $ mvn install:install-file 
         -Dfile=/path/to/struts-action-1.3.0-dev.jar
         -DgroupId=org.apache.struts
         -DartifactId=struts-action
         -Dversion=1.3.0-dev
         -Dpackaging=jar
         -DgeneratePom=true

Repeat for Tiles and Taglib.

(The 'generatePom' parameter is currently only available if you build the
install plugin from source.  Otherwise, you can ignore Maven's complaints about
missing poms, or manually create them in your local repository.)


Other Issues
------------


FAQs
----

Q:  

A:  
