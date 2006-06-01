
Shale Blank Archetype
=====================

This directory contains the Shale Blank Archetype for Maven 2.

To use the archetype to create a blank project:

   $ cd ~/projects
   $ mvn archetype:create                                             \
         -DarchetypeGroupId=org.apache.struts.shale                   \
         -DarchetypeArtifactId=struts-archetype-shale-blank           \
         -DarchetypeVersion=1.0-SNAPSHOT                              \
         -DgroupId=com.example                                        \
         -DpackageName=com.example.projectname                        \
         -DartifactId=projectname                                     \
         -DremoteRepositories=http://people.apache.org/maven-snapshot-repository
         
   You will then need to move src/main/resources/Bundle.properties into the 
   proper package structure under src/main/resources.

To build your new webapp and install it in your local Maven repository:

   $ cd projectname
   $ mvn install

To start Tomcat and deploy your new webapp:

  Modify pom.xml to provide the path to a local Tomcat 5.x installation:
       <plugin>
         <groupId>org.codehaus.cargo</groupId>
         <artifactId>cargo-maven2-plugin</artifactId>
         ...
               <home>c:/java/apache-tomcat-5.5.17</home>

  $ mvn package cargo:start

   Open a browser and visit http://localhost:8080/projectname

To build and install the archetype in your local repository:

   $ svn co http://svn.apache.org/repos/asf/struts/sandbox/trunk/archetypes
   $ cd archetypes/shale-blank
   $ mvn install

Prerequisites
-------------

   Maven 2 from http://maven.apache.org

Cargo Configuration
-------------------

   The Cargo plugin can be configured to download Apache Tomcat 5.5.17 by
   (in pom.xml) removing the <home> element and uncommenting the 
   <zipUrlInstaller> section.

