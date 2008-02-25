package org.apache.struts2.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public interface BundleAccessor {

    String CURRENT_BUNDLE_NAME = "__bundle_name__";

    void setBundles(Map<String, Bundle> bundles);

    void setBundleContext(BundleContext bundleContext);

    Class loadClass(String name) throws ClassNotFoundException;

    InputStream loadResourceAsStream(String name) throws IOException;

    URL loadResource(String name);

    void setPackageToBundleMapping(Map<String, String> packageToBundle);
}
