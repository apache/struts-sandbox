package org.apache.struts2.osgi;

import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;

public interface PackageLoader {
    List<PackageConfig> loadPackages(Bundle bundle, ObjectFactory objectFactory, Map<String,PackageConfig> map) throws ConfigurationException;
}
