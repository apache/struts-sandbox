package org.apache.struts2.osgi;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class DelegatingObjectFactory extends ObjectFactory {
    private ObjectFactory delegateObjectFactory;
    private BundleAccessor bundleResourceLoader;
    
    @Inject
    public DelegatingObjectFactory(@Inject Container container,
                                   @Inject("struts.objectFactory.delegate") String delegate) {
        
        if (delegate == null) {
            delegate = "struts";
        }
        delegateObjectFactory = container.getInstance(ObjectFactory.class, delegate);
    }
    
    @Inject
    public void setBundleResourceLoader(BundleAccessor rl) {
        this.bundleResourceLoader = rl;
    }
    
    
    @Override
    public Class getClassInstance(String className) throws ClassNotFoundException {
        try
        {
            return delegateObjectFactory.getClassInstance(className);
        }
        catch (ClassNotFoundException cnfe)
        {
            return bundleResourceLoader.loadClass(className);
        }
    }
    
    
}
