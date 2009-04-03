/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.osgi;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.Main;
import org.apache.felix.main.AutoActivator;
import org.apache.felix.shell.ShellService;
import org.apache.commons.lang.xwork.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.BundleActivator;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.finder.ResourceFinder;
import com.opensymphony.xwork2.util.URLUtil;
import com.opensymphony.xwork2.config.ConfigurationException;

/**
 * Apache felix implementation of an OsgiHost
 * See http://felix.apache.org/site/apache-felix-framework-launching-and-embedding.html
 */
public class FelixOsgiHost implements OsgiHost {
    private static final Logger LOG = LoggerFactory.getLogger(FelixOsgiHost.class);

    private static final String FELIX_FILEINSTALL_POLL = "felix.fileinstall.poll";
    private static final String FELIX_FILEINSTALL_DIR = "felix.fileinstall.dir";
    private static final String FELIX_FILEINSTALL_DEBUG = "felix.fileinstall.debug";

    private Felix felix;
    private Map<String, Bundle> bundles = Collections.synchronizedMap(new HashMap<String, Bundle>());
    private List<? extends BundleActivator> extraBundleActivators;
    private boolean cleanBundleCache;
    private static Pattern versionPattern = Pattern.compile("([\\d])+[\\.-]");

    protected void startFelix() {
        //load properties from felix embedded file
        Properties configProps = getProperties("default.properties");

        // Copy framework properties from the system properties.
        Main.copySystemProperties(configProps);
        replaceSystemPackages(configProps);

        //struts, xwork and felix exported packages
        Properties strutsConfigProps = getProperties("struts-osgi.properties");
        addExportedPackages(strutsConfigProps, configProps);

        //find bundles and adde em to autostart property
        int bundlePaths = addAutoStartBundles(configProps);

        // Bundle cache
        configProps.setProperty(Constants.FRAMEWORK_STORAGE, System.getProperty("java.io.tmpdir") + ".felix-cache");

        // File Install
        /*String bundlesDir = Thread.currentThread().getContextClassLoader().getResource("bundles").getPath();
        configProps.put(OsgiConfigurationProvider.FELIX_FILEINSTALL_POLL, "5000");
        configProps.put(OsgiConfigurationProvider.FELIX_FILEINSTALL_DIR, bundlesDir);
        configProps.put(OsgiConfigurationProvider.FELIX_FILEINSTALL_DEBUG, "1");*/

        if (cleanBundleCache) {
            if (LOG.isDebugEnabled())
                LOG.debug("Clearing bundle cache");
            configProps.put(FelixConstants.FRAMEWORK_STORAGE_CLEAN, FelixConstants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        }

        //other properties
        configProps.put(FelixConstants.SERVICE_URLHANDLERS_PROP, "false");
        configProps.put(FelixConstants.LOG_LEVEL_PROP, "4");
        configProps.put(FelixConstants.BUNDLE_CLASSPATH, ".");

        try {
            List<BundleActivator> list = new ArrayList<BundleActivator>();
            if (extraBundleActivators != null)
                list.addAll(extraBundleActivators);
            list.add(new AutoActivator(configProps));
            configProps.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);

            felix = new Felix(configProps);
            felix.start();
            if (LOG.isTraceEnabled())
                LOG.trace("Apache Felix is running");
        }
        catch (Exception ex) {
            throw new ConfigurationException("Couldn't start Felix (OSGi)", ex);
        }

        // Wait for all bundles to load
        while (bundles.size() < bundlePaths) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOG.error("An error occured while waiting for bundle activation", e);
            }
        }
    }

    private int addAutoStartBundles(Properties configProps) {
        List<String> bundleJars = new ArrayList<String>();
        try {
            ResourceFinder finder = new ResourceFinder();
            URL url = finder.find("bundles");
            if ("file".equals(url.getProtocol())) {
                File bundlerDir = new File(url.toURI());
                File[] bundles = bundlerDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String name) {
                        return StringUtils.endsWith(name, ".jar");
                    }
                });

                if (bundles != null && bundles.length > 0) {
                    //add all the bundles to the list
                    for (File bundle : bundles) {
                        String externalForm = bundle.toURI().toURL().toExternalForm();
                        if (LOG.isDebugEnabled())
                            LOG.debug("Adding bundle [#0]", externalForm);
                        bundleJars.add(externalForm);
                     }

                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("No bundles found under the 'bundles' directory");
                }
            }
        } catch (Exception e) {
            if (LOG.isWarnEnabled())
                LOG.warn("Unable load bundles from the 'bundles' directory", e);
            return 0;
        }

        // Add shell and File Install bundles activation
        bundleJars.add(getJarUrl(ShellService.class));
        //sb.append(getJarUrl(FileInstall.class)).append(" ");
        bundleJars.add(getJarUrl(ServiceTracker.class));

        //autostart bundles
        configProps.put(AutoActivator.AUTO_START_PROP + ".1", StringUtils.join(bundleJars, " "));

        return bundleJars.size();
    }

    private String getJarUrl(Class clazz) {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL loc = codeSource.getLocation();
        return loc.toString();
    }

    private void replaceSystemPackages(Properties properties) {
        //Felix has a way to load the config file and substitution expressions
        //but the method does not have a way to specify the file (other than in an env variable)

        //${jre-${java.specification.version}}
        String systemPackages = (String) properties.get(Constants.FRAMEWORK_SYSTEMPACKAGES);
        String jreVersion = "jre-" + System.getProperty("java.version").substring(0, 3);
        systemPackages = systemPackages.replace("${jre-${java.specification.version}}", (String) properties.get(jreVersion));
        properties.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
    }

    /*
        Find subpackages of the packages defined in the property file and export them
     */
    private void addExportedPackages(Properties strutsConfigProps, Properties configProps) {
        String[] rootPackages = StringUtils.split((String) strutsConfigProps.get("scanning.package.includes"), ",");
        ResourceFinder finder = new ResourceFinder(StringUtils.EMPTY, OsgiConfigurationProvider.class.getClassLoader());
        List<String> exportedPackages = new ArrayList<String>();
        //build a list of subpackages
        for (String rootPackage : rootPackages) {
            try {
                Map<URL, Set<String>> subpackagesMap = finder.findPackagesMap(StringUtils.replace(rootPackage.trim(), ".", "/"));
                for (Map.Entry<URL, Set<String>> entry : subpackagesMap.entrySet()) {
                    URL url = entry.getKey();
                    Set<String> packages = entry.getValue();
                    String version = getVersion(url);

                    if (packages != null) {
                        for (String subpackage : packages) {
                            exportedPackages.add(subpackage + "; version=" + version);
                        }
                    }
                }
            } catch (IOException e) {
                if (LOG.isErrorEnabled())
                    LOG.error("Unable to find subpackages of [#0]", e, rootPackage);
            }
        }

        //make a string with the exported packages and add it to the system properties
        if (!exportedPackages.isEmpty()) {
            String systemPackages = (String) configProps.get(Constants.FRAMEWORK_SYSTEMPACKAGES);
            systemPackages = StringUtils.chomp(systemPackages, ",") + "," + StringUtils.join(exportedPackages, ",");
            configProps.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
        }
    }

    /**
     * Gets the version used to export the packages. it tries to get it from MANIFEST.MF, or the file name
     */
    private String getVersion(URL url) {
        if ("jar".equals(url.getProtocol())) {
            try {
                JarFile jarFile = new JarFile(new File(URLUtil.normalizeToFileProtocol(url).toURI()));
                Manifest manifest = jarFile.getManifest();
                if (manifest != null) {
                    String version = manifest.getMainAttributes().getValue("Bundle-Version");
                    if (StringUtils.isNotBlank(version)) {
                        return getVersionFromString(version);
                    }
                } else {
                    //try to get the version from the file name
                    return getVersionFromString(jarFile.getName());
                }
            } catch (Exception e) {
                if (LOG.isErrorEnabled())
                    LOG.error("Unable to extract version from [#0], defaulting to '1.0.0'", url.toExternalForm());

            }
        }

        return "1.0.0";
    }

    /**
     * Extracts numbers followed by "." or "-" from the string and joins them with "."
     */
    static String getVersionFromString(String str) {
        Matcher matcher = versionPattern.matcher(str);
        List<String> parts = new ArrayList<String>();
        while(matcher.find()) {
            parts.add(matcher.group(1));
        }

        //default
        if (parts.size() == 0)
            return "1.0.0";

        while(parts.size() < 3)
            parts.add("0");

        return StringUtils.join(parts, ".");
    }

    private Properties getProperties(String fileName) {
        ResourceFinder finder = new ResourceFinder("");
        try {
            return finder.findProperties(fileName);
        } catch (IOException e) {
           if (LOG.isErrorEnabled())
               LOG.error("Unable to read property file [#]", fileName);
            return new Properties();
        }
    }

    /**
     * Bundle activators that will be added to the container
     */
    public void setExtraBundleActivators(List<? extends BundleActivator> extraBundleActivators) {
        this.extraBundleActivators = extraBundleActivators;
    }

    public Map<String, Bundle> getBundles() {
        return bundles;
    }

    public void destroy() throws Exception {
        try {
            felix.stop();
        } finally {
            bundles = null;
        }
    }

    @Override
    public void init() throws Exception {
        startFelix();
    }

    @Inject("struts.osgi.clearBundleCache")
    public void setCleanBundleCache(String cleanBundleCache) {
        this.cleanBundleCache = "true".equalsIgnoreCase(cleanBundleCache);
    }
}
