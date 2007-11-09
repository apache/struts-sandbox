package org.apache.struts2.osgi;

import java.util.Map;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

public class DelegatingObjectFactory extends ObjectFactory {
    private ObjectFactory delegateObjectFactory;
    private BundleAccessor bundleResourceLoader;
    
    @Inject
    public void setDelegateObjectFactory(@Inject Container container, 
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
    
    
    public boolean isNoArgConstructorRequired() {
        return delegateObjectFactory.isNoArgConstructorRequired();
    }

    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        return delegateObjectFactory.buildBean(clazz, extraContext);
    }

    @Override
    public Class getClassInstance(String className) throws ClassNotFoundException {
        try
        {
            return delegateObjectFactory.getClassInstance(className);
        }
        catch (Exception e)
        {
            return bundleResourceLoader.loadClass(className);
        }
    }
    
    
}
