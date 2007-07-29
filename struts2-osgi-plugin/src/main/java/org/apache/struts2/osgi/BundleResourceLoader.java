package org.apache.struts2.osgi;

import java.io.InputStream;

import org.apache.struts2.util.ClassLoaderUtils;
import org.apache.struts2.views.velocity.StrutsResourceLoader;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.opensymphony.xwork2.inject.Inject;

public class BundleResourceLoader extends ClasspathResourceLoader {

    public synchronized InputStream getResourceStream(String name)
            throws ResourceNotFoundException {
        if ((name == null) || (name.length() == 0)) {
            throw new ResourceNotFoundException("No template name provided");
        }

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        try {
            return DefaultBundleAccessor.getInstance().loadResourceAsStream(name);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
