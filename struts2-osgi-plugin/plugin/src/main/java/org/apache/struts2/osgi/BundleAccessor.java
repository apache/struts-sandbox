package org.apache.struts2.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public interface BundleAccessor {

    String CURRENT_BUNDLE_NAME = "__bundle_name__";

    void init(Map<String, Bundle> bundles, BundleContext bundleContext, Map<String, String> packageToBundle);

    Class loadClass(String name) throws ClassNotFoundException;

    InputStream loadResourceAsStream(String name) throws IOException;

    URL loadResource(String name);

    Map<String, Bundle> getBundles();

    Set<String> getPackagesByBundle(Bundle bundle);

    Object getService(ServiceReference ref);

    ServiceReference getServiceReference(String className);

}
