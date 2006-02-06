
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
   $ mvn war:war


Prerequisites
-------------


Other Issues
------------


FAQs
----

Q:  

A:  
