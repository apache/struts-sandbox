package org.apache.struts2.osgi.loaders;

import java.io.InputStream;

import org.apache.struts2.osgi.DefaultBundleAccessor;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Finds Velocity templates in bundles
 */
public class VelocityBundleResourceLoader extends ClasspathResourceLoader {

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
