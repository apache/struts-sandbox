package org.apache.struts2.osgi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class SpringOSGiUtil {
    public static boolean isValidBean(BundleContext bundleContext, String beanId) throws InvalidSyntaxException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return getBean(bundleContext, beanId) != null;
    }
    
    public static Object getBean(BundleContext bundleContext, String beanId) throws InvalidSyntaxException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ServiceReference[] references = bundleContext.getAllServiceReferences(
            "org.springframework.context.ApplicationContext", null);
        if (references != null && references.length > 0) {
            Object beanFactory = bundleContext.getService(references[0]);
            //this class and the BeanFactory service are loaded by different classloaders
            //so we cannot cast to a common interface (is there any other (nice) way of doing this?)
            return getBean(beanFactory, beanId);
        }
        
        return null;
    }
    
    private static Object getBean(Object beanFactory, String beanId) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method getBeanMethod = beanFactory.getClass().getMethod("getBean", String.class);
        return getBeanMethod.invoke(beanFactory, beanId);
    }
}
