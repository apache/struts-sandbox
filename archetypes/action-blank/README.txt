
Struts Archetypes - Action Blank
================================

This directory contains the Struts Action Blank Archetype for Maven 2.

To use the archetype to create a blank project:

   $ cd ~/projects
   $ mvn archetype:create 
         -DarchetypeGroupId=org.apache.struts.action
         -DarchetypeArtifactId=struts-archetype-action-blank 
         -DarchetypeVersion=1.0-SNAPSHOT
         -DgroupId=com.example
         -DpackageName=com.example.projectname
         -DartifactId=my-webapp
         -DremoteRepositories=http://people.apache.org/maven-snapshot-repository
         
To build your new webapp:

   $ cd my-webapp
   $ mvn install

To start Tomcat and deploy your new webapp:

  Modify pom.xml to provide the path to a local Tomcat 5.x installation:
       <plugin>
         <groupId>org.codehaus.cargo</groupId>
         <artifactId>cargo-maven2-plugin</artifactId>
         ...
               <home>c:/java/apache-tomcat-5.5.17</home>

  $ mvn package cargo:start
  
To build and install the archetype in your local repository:

   $ svn co http://svn.apache.org/repos/asf/struts/sandbox/trunk/archetypes
   $ cd archetypes/action-blank
   $ mvn install

