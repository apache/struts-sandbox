MailReader-Chain README.txt

This is a work-in-progress demonstration application showing how you can use
Commons-Chain with a web application framework like Struts.

The appication does not rely on the new Struts-Chain request processor, but
this is liable to change as Struts 1.3.x development progresses.

The primary build and documentation environment is Maven. An Ant build file is
provided for integration with IDEs. It can be used from the command line,
but using Maven directly is more efficient.

The web application is built under target/mailreader by the "webapp.war" goal.
For play testing, you may like to point your web container directly to this
folder. Containers like Jetty, Resin, and Tomcat can use alternate configuration
files that you can store with your copy of the repository.

The business logic is tested with JUnit. A set of WebTest Canoo tests are under
development but not working yet.

Note that this application uses multiple configuration files to implement a
"config-behind-story" approach. Each workflow (or "story") has its own
pair of struts-config and validator config files.

For the time being, development is tracked by STATUS.txt file, but we expect
this to be integrated into the Maven documentation shortly.

----

/src - source code files
/xdocs - documentation files
/target - build files

/src/java - Source code for production Java classes
/src/resources - XML and property file resources
/src/test - Unit tests for Java classes
/src/webapp - Web application specific files

/src/webapp/ - HTML and JSP assets
/src/webapp/WEB-INF/ - various configuration resources
/src/webapp/entities -  WebTest configuration entities
/src/webapp/WEB-INF/struts-config - Struts configuration documents
/src/webapp/WEB-INF/tld - Taglib descriptors

####
