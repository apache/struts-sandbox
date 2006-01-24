<!--

 Copyright 2006 The Apache Software Foundation.
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 $Id: build.xml 366441 2006-01-06 06:57:59Z craigmcc $

-->


# ==============================================================================
# README.txt for the Shale Blank Starter Application
# ==============================================================================


Welcome to the Shale Blank Starter Application!  These artifacts are provided
for two major reasons:

* Provide a very simple example of a minimal application using Shale.

* For developers using Ant to build their projects, provide a complete
  "starter kit" project directory structure, without requiring you
  to hand configure the entire environment.


# ------------------------------------------------------------------------------
# Components of the Shale Blank Starter Application
# ------------------------------------------------------------------------------

To download a runnable version of this application, which can be simply
dropped into a servlet container, download a file named
"shale-blank-YYMMDD.war" from the nightly builds directory:

    http://cvs.apache.org/builds/struts/nightly/struts-shale/

The source code for this application is the Starter Kit artifact described
in the next section.

To run the application, most servlet containers provide a directory into which
you can simply drop a WAR file, and it will automatically execute.  For Tomcat,
this is $CATALINA_HOME/webapps.

The application consists of a single JSP page (welcome.jsp) which uses
several <h:outputText> components to retrieve localized message strings from
a resource bundle included in the application, plus a component that is bound
to the "timestamp" property of the corresponding backing bean.  The backing
bean class (WelcomeBean) implements ViewController (indirectly, by virtue of
the fact that it extends AbstractViewController).  The prerender() method is
called immediately before the corresponding page is rendered, so we insert
logic here to go retrieve the current date/time value that will be displayed
by the component.

The source code for the application (found in the Starter Kit) also includes
other artifacts useful in understanding Shale based applications:

- Basic configuration resources (web.xml and faces-config.xml)
  with comments describing customizations you would typically make.

- A simple example of a unit test for a ViewController bean, used to
  perform out-of-container unit testing of the backing beans.  These
  tests leverage the facilities of the Shale Test Framework.


# ------------------------------------------------------------------------------
# Using the Shale Blank Starter Application As A Starter Kit
# ------------------------------------------------------------------------------

To use the Shale Blank Starter Application as a starter kit, follow these steps:

* Download a file named "shale-blank-YYYYMMDD.tar.gz" or "shale-blank-YYYYMMDD.zip"
  into the parent directory of where you want to create your new project.  You
  can find this at the Shale nightly distribution site:

    http://cvs.apache.org/builds/struts/nightly/struts-shale/

* Untar or unzip the downloaded file to create the standard Shale Blank
  directory structure.

* Rename the top level directory (shale-blank-YYYYMMDD) to the desired name
  for your project's top level directory, and change your current working
  directory into the project's top level directory.

* In a text viewing application, open the "default.properties" file in the
  top level directory to review the property settings that you may configure
  without having to modify the build.xml script at all.

* In a text editing application, create a new build.properties file (in the
  same directory as the default.properties file) to contain overrides for the
  properties you need to change.  At a minimum, you will generally need to
  create custom values for the following properties:

  - All of the "Project Description Information" properties (project.copyright
    through project.version).

  - webapp.state.saving - The desired setting for JavaServer Faces component
    state saving (client or server).

  - lib.dir - Fully qualified pathname of a directory containing all of the
    library dependencies (other than a Shale distribution) that you will want
    to include in the web application's /WEB-INF/lib directory.  It is acceptable
    for the JAR files in this directory to be nested inside other directories.
    One convenient mechanism to acquire such a set of libraries is to download
    a "shale-dependencies-YYYYMMDD.tar.gz" or "shale-dependencies-YYYYMMDD.zip"
    file from the nightly build distribution site mentioned above.

  - ext.dir - (Optional) Fully qualified pathname of a directory containing JAR
    files for dependencies that you wish to have present on the compile time
    classpath, but should *not* be included in the /WEB-INF/lib directory
    (presumably because they will be provided by the container you are running
    on already).  It is acceptable for the JAR files in this directory to be
    nested inside other directories.

  - shale.dir - Fully qualified pathname of a directory containing the Shale
    binary distribution.

  - All of the (Optional) Tomcat Integration properties, *if* you plan on using
    the corresponding targets to install, reload, and remove your web application
    from a running instance of Tomcat (5.0 or later).

* If necessary, you can customize the behavior of the build.xml script
  itself.  This will not be needed for most simple applications, however.

* Next, customize the configuration files for your application as necessary.

  - src/web/WEB-INF/web.xml - IF you are going to use the optional Spring
    Framework, integration features, uncomment the context initialization
    parameter and listener entries used to configure it.  You will need to
    create a "src/web/WEB-INF/applicationContext.xml" configuration for the
    beans to be loaded by Spring (or, use a different name if you change the
    value of the configuration parameter).

  - src/web/WEB-INF/web.xml - Change the mapping for the JavaServer Faces
    servlet if you prefer something other than the default (".faces).  Changing
    this value will also require you to change the path in "src/web/index.jsp".

  - src/web/WEB-INF/faces-config.xml - Define the localization properties for
    the default and supported locales, and (optionally) declaring a resource
    bundle used to look up replacements for the standard messages.  The easiest
    way to accomplish this is to uncomment the existing example entries, making
    any required changes.

  - src/web/WEB-INF/dialog-config.xml - If you are using the Shale Dialogs
    feature, you will define your dialogs here.  Use the commented-out example
    as a guide to the required information, or see the web site documentation
    for more details:

        http://struts.apache.org/struts-shale/features-dialog-manager.html
        http://struts.apache.org/struts-shale/shale-core/apidocs/org/apache/shale/dialog/package-summary.html

  - src/web/WEB-INF/chain-config.xml - If you are using the advanced features
    of Shale to configure the Shale Application Controller Filter, you will
    create command chains here.

  - src/web/index.jsp - Adjust the path of the <jsp:forward> entry to point at
    your actual starting page.  Be sure you use a URL that includes the mapping
    value for the JavaServer Faces controller servlet.

* Now, you are ready to add a new page and its corresponding backing bean to
  the application.  This generally involves the following steps:

  - src/web/WEB-INF/faces-config.xml - Add a new section of configuration
    information for the new page.

  - src/web/WEB-INF/faces-config.xml - Add a <managed-bean> element describing
    the backing bean for your new view.  If you are using the View Controller
    feature, be sure that the name you assign this bean corresponds to the
    mapping requirements described in the documentation:

        http://struts.apache.org/struts-shale/features-view-controller.html

    Unless you have a *really* good reason to do otherwise, view controller
    managed beans should be placed into request scope.

  - Add a corresponding Java class in the "src/java" directory, creating new
    package subdirectories as needed.  If you are using the view controller
    capability, you will find it easiest to extend the convenience base class
    that Shale provides ("org.apache.shale.view.AbstractViewController").

  - (Optional, but highly recommended) add a unit test class, into the "src/test"
    directory.  The package name should be the same as the package containing
    the backing bean itself, and the class name should be the same as the
    simple class name of the backing bean itself, suffixed with "TestCase"
    (this enables the build.xml script to locate and execute all unit tests
    when you execute the "test" target).  You will generally find it easiest
    to extend one of the base test classes provided by Shale's Test Framework
    (AbstractJsfTestCase or AbstractViewControllerTestCase) because they will
    set up all of the relevant container mock objects for you.  For more
    information on using the test framework to create unit tests, see:

        http://struts.apache.org/struts-shale/features-test-framework.html

* After you have created one or more of your own pages, you will likely want
  to remove the demo page (welcome.jsp), and corresponding Java classes
  (WelcomeBean and WelcomeBeanTestCase).

* The supplied build.xml script includes optional integration with a running
  instance of Tomcat 5.0 or later.  This capability utilizes custom Ant tasks
  provided by Tomcat to dynamically install, remove, or reload a web application
  currenty being developed, without having to restart Tomcat itself every
  time.  Using this feature supports a very rapid turnaround cycle, with
  steps like this:

  (1) Start up Tomcat if it is not already running.

  (2) Execute the "install" target once, to install the application (if it
      is not already present).  This target will automatically recompile
      anything that has been changed.

  (3) Test your web application in a browser, and note a change that needs
      to be made.

  (4) Make the necessary changes in your favorite IDE environment, on the
      files in the "src" directory hierarchy of your project.

  (5) Execute the "reload" target to recompile (if necessary) and redeploy
      the application.

  (6) Return to step (3) for the next incremental change, or proceed to
      Step (7) if you are done for now.

  (7) Optionally, execute the "remove" target to undeploy the application
      from Tomcat.

  (8) Optionally, shut down Tomcat if you are through with it.

