package org.apache.struts2.osgi;

import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;

public interface PackageLoader {
    List<PackageConfig> loadPackages(Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory, Map<String,PackageConfig> map) throws ConfigurationException;
}
