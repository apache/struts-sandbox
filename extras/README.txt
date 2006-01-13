
    This project provides:
     - alternate implementation of org.apache.struts.util.MessageResources (and factory)
     - Struts plugin that configures the specified subclass of
       org.apache.struts.plugins.resources.CommonsResources (and factory)
    
    See the user documentation for further details about commons-resources beyond 
    what is provided here as a Plugin and Wrapper.
    
     http://jakarta.apache.org/commons/resources/
    
    The Resources plugin provides a configured MessageResources impl "as if" it 
    was specified above as a <message-resource ...
    
    Available Properties and defaults:
    
    Property                  Description                 Default
    strutsPluginFactoryClass  The factory class used to   org.apache.struts.plugins.resources.CommonsResourcesFactory
                              configure this Module.
    
    implFactoryClass          The Jakarta Commons         org.apache.commons.resources.impl.WebappPropertyResourcesFactory
                              Resources Factory to use.
    
    implClass                 The Jakarta Commons         org.apache.commons.resources.impl.WebappPropertyResources
                              Resources implementaion 
                              to use.
    
    bundle                    The properties file to      (bundle is required)
                              load.
    
    returnNull                Determines whether a null   false
                              is returned if the 
                              specified key is not found
    
    key                       Used in combination with    null (not needed if null)
                              Struts internal constant 
                              (Globals.MESSAGE_KEY) to
                              store the configured 
                              MessageResources in the
                              ServletContext
    
    
    
    Q. Why would I use this instead of the above?
    A. Because with an overridden Factory and plugin of your own, 
       you can easily customize the default behaviour and unlike
       the above, you will have direct access to the ActionServlet.

 

 Below is an example of how to use this plugin with your Struts 1.1
 implementation.


 in your struts-config.xml....

 (Note - the below is a direct copy from the modified struts-example.war
         that demonstrates this plugin in action)

...
...
  <!-- ========== Message Resources Definitions =========================== -->

<!-- shown here using a subclass of MessageResources -->
  <message-resources 
    factory="org.apache.struts.plugins.resources.CommonsResourcesFactory" 
    parameter="org.apache.struts.webapp.example.ApplicationResources"/>

  <message-resources
    factory="org.apache.struts.plugins.resources.CommonsResourcesFactory" 
    parameter="org.apache.struts.webapp.example.AlternateApplicationResources"
    key="alternate"/>

...



(below is how you might do this with the plugin)

 NOTE - the same rule applies where if you define multiple message-resources,
        you must provide a unique key attribute or the later will overwrite 
        the former

...
  <!-- ========== Plug Ins Configuration ================================== -->
...
...
  <plug-in className="org.apache.struts.plugins.resources.ResourcesPlugin">
      <set-property property="bundle" 
    	value="org.apache.struts.webapp.example.ApplicationResources"/>
  </plug-in>
  
  <plug-in className="org.apache.struts.plugins.resources.ResourcesPlugin">
    <set-property property="strutsPluginFactoryClass" 
    	value="org.apache.struts.plugins.resources.CommonsResourcesFactory"/>
    <set-property property="implFactoryClass" 
    	value="org.apache.commons.resources.impl.WebappPropertyResourcesFactory"/>
    <set-property property="implClass" 
    	value="org.apache.commons.resources.impl.WebappPropertyResources"/>
    <set-property property="key" 
    	value="alternate"/>
    <set-property property="bundle" 
    	value="org.apache.struts.webapp.example.AlternateApplicationResources"/>
  </plug-in>
...
...