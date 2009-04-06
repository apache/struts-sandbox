package org.apache.struts2.osgi;

import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;

import java.net.URL;
import java.util.Enumeration;
import java.util.Collections;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;

/**
 * ClassLoaderInterface instance that delegates to the singleton of DefaultBundleAccessor 
 */
public class BundleClassLoaderInterface implements ClassLoaderInterface {
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return DefaultBundleAccessor.getInstance().loadClass(name);
    }

    public URL getResource(String name) {
        return  DefaultBundleAccessor.getInstance().loadResource(name, true);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        return Collections.enumeration(DefaultBundleAccessor.getInstance().loadResources(name, true));
    }

    public InputStream getResourceAsStream(String name) throws IOException {
        return DefaultBundleAccessor.getInstance().loadResourceAsStream(name);
    }

    public ClassLoaderInterface getParent() {
        return null;
    }
}
