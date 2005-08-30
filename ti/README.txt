OVERVIEW

Struts Ti is a simplified Model 2 framework for developing webapps which
allows the developer better access to the underlying servlet/portlet
environment. 

Struts Ti serves a niche of web applications that don’t want the
additional complexity of server-side components and verbose
configuration, yet want the structure and controller features of a
modern web framework. Struts Ti builds on the directions of Struts 1.x,
yet re-implements the framework to provide a clean slate for the next
generation of Struts Ti. It aims to combine the simplicity of Ruby on
Rails and NanoWeb, the refinement of WebWork 2, the tool-friendly
authoring and Page Flow of Beehive, and the lessons learned from Struts 1.x.

The key word for Struts Ti is simplicity. Ideally, Struts Ti should
approach Ruby on Rails levels of easy of use, yet scale up to large
applications providing a smooth transition to JSF/Shale if desired.

KEY FEATURES

    * POJO-based action that combines an Action and ActionForm in a
similar manner to JSF backing beans and WebWork 2 Commands
    * Intelligent defaults utilizing naming and placement conventions
to require minimal, if any, configuration per page, however it will be
possible to override everything on a global and per-action basis
    * Configuration can be “assumed” or declared through annotations,
xml or properties files, or any other pluggable mechanism
    * Pluggable EL for data binding defaulting to JSP 2.0 EL but
allowing OGNL or even BeanUtils
    * Integration of a dialog or page flow capability drawing from
Beehive, Spring’s web flow, and Shale’s Dialogs.
    * Per-Action optional interceptor chain ala WebWork 2
    * Built-in dependency injection support

DESIGN GOALS

    * No servlet dependency in core framework, portlet and JSF support
out of the box
    * Spring-based dependency injection in core to allow for pluggability
    * No bias to any view technology
    * Ability to layer Struts 1.x compatibility on top
    * Highly toolable
    * Smooth integration into a portal/portlet environment
    * AJAX-friendly

IMPLEMENTATION

    * Built on the backbone of commons-chain
    * No restriction on multiple Servlets and/or Servlet Filter
implementations
    * Key decision points (action selection for example) use CoR chain
for maximum flexiblity
    * Configuration specified using XDoclet (Java 1.4) or Annotations
(Java 5+), both supported out of the box

DEPENDENCIES

    * Servlets 2.4
    * Java 1.4 runtime, Java 1.5 to build
    * JSP 2.0 if taglibs used
    * XWork 1.5

Existing project collaboration

    * XWork/WebWork using their XWork and possibly parts/all of their
tag libraries
    * Beehive using the Page Flow and annotations

BUILDING FROM SOURCE
To build Struts Ti from source, you must have Maven 1.x installed.
Execute 'maven dist' from 'core' directory of your local copy of Struts Ti.

 $ cd ti    <- your local copy
 $ cd core
 $ maven dist



	The build plan:
	
	  ${jdk} - replace this with either 1.4 or 1.5
	  ${ver} - replace with current version 1.0-dev (or YYYYMMDD for nightly)
	 
	    Target   Artifact(s)             Description
	    ______   ___________             ___________
	    dist     target/                 creates a full distribution of core and example apps
	               ti-core${jdk}-${ver}.jar
	               ti-sample${jdk}.war
	               
	    jar      target/                 creates only core archive
	                ti-core15.jar
	                
	    test     (nothing)               run full set of (1.5 based) junit tests
	  
	    cactus   (nothing)               run full set of (1.5 based) junit and cactus tests
	  
	    site     target/docs/            create ti web site (includes build reports)
	               **/*.html
	             
	    nightly  target/                 creates nightly distribution that gets uploaded to 
	               ti-core${jdk}         http://svn.apache.org/builds/struts/maven/trunk/nightly/struts-sandbox/ti/
	               ti-sample${jdk}-${ver}.war	    
	  
	  


STATUS

A working, if feature sparce, framework is in place.  

The code is available in the Struts sandbox, but at this point, 
the project site and more detailed design discussions are on an
external server:

https://www.twdata.org/projects/struts-ti

####
