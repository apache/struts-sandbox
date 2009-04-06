package org.apache.struts2.osgi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.MalformedURLException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Bundle;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class OsgiUtil {
    private static final Logger LOG = LoggerFactory.getLogger(OsgiUtil.class);

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

    /**
     * A bundle is a jar, and a bunble URL will be useless to clients, this method translates
     * a URL to a resource inside a bundle from "bundle:something/path" to "jar:file:bundlelocation!/path"
     */
    public static URL translateBundleURLToJarURL(URL bundleUrl, Bundle bundle) throws MalformedURLException {
        if (bundleUrl != null && "bundle".equalsIgnoreCase(bundleUrl.getProtocol())) {
            StringBuilder sb = new StringBuilder("jar:");
            sb.append(bundle.getLocation());
            sb.append("!");
            sb.append(bundleUrl.getFile());
            return new URL(sb.toString());
        }

        return bundleUrl;
    }
}
