package org.apache.struts2.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.main.AutoActivator;
import org.apache.felix.shell.ShellService;
import org.apache.struts2.osgi.loaders.VelocityBundleResourceLoader;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.Velocity;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.twdata.pkgscanner.ExportPackage;
import org.twdata.pkgscanner.PackageScanner;
import static org.twdata.pkgscanner.PackageScanner.*;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class OsgiConfigurationProvider implements PackageProvider {

    private static final String FELIX_LOG_LEVEL = "felix.log.level";

    private static final Logger LOG = LoggerFactory.getLogger(OsgiConfigurationProvider.class);

    private static final String FELIX_FILEINSTALL_POLL = "felix.fileinstall.poll";
    private static final String FELIX_FILEINSTALL_DIR = "felix.fileinstall.dir";
    private static final String FELIX_FILEINSTALL_DEBUG = "felix.fileinstall.debug";

    private Felix felix;
    private Map<String,Bundle> bundles = Collections.synchronizedMap(new HashMap<String,Bundle>());
    private Configuration configuration;
    private BundleContext bundleContext;
    private BundleAccessor bundleAccessor;
    private boolean bundlesChanged = false;

    private ObjectFactory objectFactory;

    @Inject
    public void setBundleAccessor(BundleAccessor acc) {
        this.bundleAccessor = acc;
    }

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    @Inject
    public void setVelocityManager(VelocityManager vm) {
        Properties props = new Properties();
        props.setProperty("osgi.resource.loader.description","OSGI bundle loader");
        props.setProperty("osgi.resource.loader.class", VelocityBundleResourceLoader.class.getName());
        props.setProperty(Velocity.RESOURCE_LOADER, "strutsfile,strutsclass,osgi");
        vm.setVelocityProperties(props);
    }

    public void destroy() {
        try {
            felix.stop();
        } catch (BundleException e) {
            LOG.error("Failed to stop Felix", e);
        }
        bundles = null;
    }

    public void init(Configuration configuration) throws ConfigurationException {
        loadOsgi();
        this.configuration = configuration;
    }

    public synchronized void loadPackages() throws ConfigurationException {
        ServiceReference[] refs;
        try {
            refs = bundleContext.getServiceReferences(PackageLoader.class.getName(), null);
        } catch (InvalidSyntaxException e) {
            throw new ConfigurationException(e);
        }
        Map<String,String> packageToBundle = new HashMap<String,String>();
        Set<String> bundleNames = new HashSet<String>();
        if (refs != null) {
            for (ServiceReference ref : refs) {
                if (!bundleNames.contains(ref.getBundle().getSymbolicName())) {
                    bundleNames.add(ref.getBundle().getSymbolicName());
                    LOG.info("Loading packages from bundle #1", ref.getBundle().getSymbolicName());
                    PackageLoader loader = (PackageLoader) bundleContext.getService(ref);
                    for (PackageConfig pkg : loader.loadPackages(ref.getBundle(),  bundleContext, objectFactory, configuration.getPackageConfigs())) {
                        configuration.addPackageConfig(pkg.getName(), pkg);
                        packageToBundle.put(pkg.getName(), ref.getBundle().getSymbolicName());
                    }
                }
            }
        }
        bundleAccessor.init(bundles, bundleContext, packageToBundle);
        bundlesChanged = false;
    }

    public synchronized boolean needsReload() {
        return bundlesChanged;
    }

    protected void loadOsgi() {
        //configuration properties
        Properties systemProperties = getProperties("default.properties");

        //struts, xwork and felix exported packages
        Properties packages = getProperties("struts-osgi.properties");

        Map<String, String> configMap = new StringMap(false);

        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES, packages.getProperty("packages")
                + "," + getScannedPackages(packages)
                + getSystemPackages(systemProperties));


        // find bundles
        Set<String> bundlePaths = new HashSet<String>(findInPackage("bundles"));
        LOG.info("Loading Struts bundles "+bundlePaths);

        StringBuilder sb = new StringBuilder();
        for (String path : bundlePaths) {
            sb.append(path).append(" ");
        }

        // Add shell and File Install bundles activation
        sb.append(getJarUrl(ShellService.class)).append(" ");
        //sb.append(getJarUrl(FileInstall.class)).append(" ");
        //sb.append(getJarUrl(ServiceTracker.class));

        //autostart bundles
        configMap.put(AutoActivator.AUTO_START_PROP + ".1", sb.toString());

        // Bundle cache
        configMap.put(BundleCache.CACHE_PROFILE_DIR_PROP, System.getProperty("java.io.tmpdir") + ".felix-cache");
        configMap.put(BundleCache.CACHE_DIR_PROP, "jim");

        // File Install
        String bundlesDir = Thread.currentThread().getContextClassLoader().getResource("bundles").getPath();
        configMap.put(OsgiConfigurationProvider.FELIX_FILEINSTALL_POLL, "5000");
        configMap.put(OsgiConfigurationProvider.FELIX_FILEINSTALL_DIR, bundlesDir);
        configMap.put(OsgiConfigurationProvider.FELIX_FILEINSTALL_DEBUG, "1");

        //other properties
        configMap.put(FelixConstants.EMBEDDED_EXECUTION_PROP, "true");
        configMap.put(FelixConstants.SERVICE_URLHANDLERS_PROP, "false");
        configMap.put(OsgiConfigurationProvider.FELIX_LOG_LEVEL, "4");
        configMap.put(FelixConstants.BUNDLE_CLASSPATH, ".");

        try {
            List<BundleActivator> list = new ArrayList<BundleActivator>();
            list.add(new BundleRegistration());
            list.add(new AutoActivator(configMap));
            // Now create an instance of the framework.
            Map configMap2 = new StringMap(configMap, false);
            felix = new Felix(configMap2, list);
            felix.start();
        }
        catch (Exception ex) {
            throw new ConfigurationException("Couldn't start Felix (OSGi)", ex);
        }

        // Wait for all bundles to load
        while (bundles.size() < bundlePaths.size()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                LOG.error("An error occured while waiting for bundle activation", e);
            }
        }
    }

    private String getJarUrl(Class clazz) {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL loc = codeSource.getLocation();
        return loc.toString();
    }

    private String getSystemPackages(Properties properties) {
        String jreVersion = "jre-" + System.getProperty("java.version").substring(0, 3);
        return properties.getProperty(jreVersion);
    }

    String getScannedPackages(Properties props) {
        Collection<ExportPackage> exports = new PackageScanner()
                .select(
                    jars(
                         include(toArray(props.getProperty("scanning.jar.includes"))),
                         exclude(toArray(props.getProperty("scanning.jar.excludes")))),
                    packages(
                         include(toArray(props.getProperty("scanning.package.includes"))),
                         exclude(toArray(props.getProperty("scanning.package.excludes")))))
                .scan();

        StringBuilder sb = new StringBuilder();
        sb.append("Export-Package: ");
        for (Iterator<ExportPackage> i = exports.iterator(); i.hasNext(); ) {
            ExportPackage pkg = i.next();
            sb.append(pkg.getPackageName());
            if (pkg.getVersion() != null) {
                sb.append(";version=").append(pkg.getVersion());
            }
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    String[] toArray(String val) {
        if (val != null) {
            return val.split("\\s*,\\s*");
        } else {
            return new String[]{};
        }
    }

    private Properties getProperties(String fileName) {
        URL propertiesURL = OsgiConfigurationProvider.class.getClassLoader().getResource(
            fileName);
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = propertiesURL.openConnection().getInputStream();
            properties.load(is);
            is.close();
        } catch (Exception ex2) {
            // Try to close input stream if we have one.
            try {
                if (is != null)
                    is.close();
            } catch (IOException ex3) {
                // Nothing we can do.
            }
        }
        return properties;
    }

    class BundleRegistration implements BundleActivator, BundleListener {

        public void start(BundleContext context) throws Exception {
            context.addBundleListener(this);
            bundleContext = context;
        }

        public void stop(BundleContext ctx) throws Exception {
        }

        public void bundleChanged(BundleEvent evt) {
            if (evt.getType() == BundleEvent.STARTED && evt.getBundle().getSymbolicName() != null) {
                LOG.info("Started bundle #1", evt.getBundle().getSymbolicName());

                bundles.put(evt.getBundle().getSymbolicName(), evt.getBundle());
                bundlesChanged = true;
            }
        }
    }

    /**
     * Scans for classes starting at the package provided and descending into subpackages.
     * Each class is offered up to the Test as it is discovered, and if the Test returns
     * true the class is retained.  Accumulated classes can be fetched by calling
     *
     * @param packageName the name of the package from which to start scanning for
     *        classes, e.g. {@code net.sourceforge.stripes}
     */
    public List<String> findInPackage(String packageName) {
        packageName = packageName.replace('.', '/');
        Enumeration<URL> urls;
        List<String> paths = new ArrayList<String>();

        try {
            urls = Thread.currentThread().getContextClassLoader().getResources(packageName);
        }
        catch (IOException ioe) {
            LOG.warn("Could not read package: " + packageName, ioe);
            return paths;
        }

        while (urls.hasMoreElements()) {
            try {
                String urlPath = urls.nextElement().getFile();
                urlPath = URLDecoder.decode(urlPath, "UTF-8");

                // If it's a file in a directory, trim the stupid file: spec
                if ( urlPath.startsWith("file:") ) {
                    urlPath = urlPath.substring(5);
                }

                // Else it's in a JAR, grab the path to the jar
                if (urlPath.indexOf('!') > 0) {
                    urlPath = urlPath.substring(0, urlPath.indexOf('!'));
                }

                //log.info("Scanning for classes in [" + urlPath + "] matching criteria: " + test);
                File file = new File(urlPath);
                if ( file.isDirectory() ) {
                    loadImplementationsInDirectory(paths, packageName, file);
                }
                else {
                    loadImplementationsInJar(paths, packageName, file);
                }
            }
            catch (IOException ioe) {
                LOG.warn("could not read entries", ioe);
            }
        }
        return paths;
    }


    /**
     * Finds matches in a physical directory on a filesystem.  Examines all
     * files within a directory - if the File object is not a directory, and ends with <i>.class</i>
     * the file is loaded and tested to see if it is acceptable according to the Test.  Operates
     * recursively to find classes within a folder structure matching the package structure.
     *
     * @param parent the package name up to this directory in the package hierarchy.  E.g. if
     *        /classes is in the classpath and we wish to examine files in /classes/org/apache then
     *        the values of <i>parent</i> would be <i>org/apache</i>
     * @param location a File object representing a directory
     */
    private void loadImplementationsInDirectory(List<String> paths, String parent, File location) {
        File[] files = location.listFiles();
        StringBuilder builder = null;

        for (File file : files) {
            builder = new StringBuilder(100);
            builder.append(parent).append("/").append(file.getName());
            String packageOrClass = ( parent == null ? file.getName() : builder.toString() );

            if (file.isDirectory()) {
                loadImplementationsInDirectory(paths, packageOrClass, file);
            }
            else if (file.getName().endsWith(".jar")) {
                try {
                    paths.add(file.toURI().toURL().toString());
                } catch (MalformedURLException e) {
                    LOG.error("Invalid file path", e);
                }
            }
        }
    }

    /**
     * Finds matching classes within a jar files that contains a folder structure
     * matching the package structure.  If the File is not a JarFile or does not exist a warning
     * will be logged, but no error will be raised.
     *
     * @param parent the parent package under which classes must be in order to be considered
     * @param jarfile the jar file to be examined for classes
     */
    private void loadImplementationsInJar(List<String> paths, String parent, File jarfile) {

        try {
            JarEntry entry;
            JarInputStream jarStream = new JarInputStream(new FileInputStream(jarfile));

            while ( (entry = jarStream.getNextJarEntry() ) != null) {
                String name = entry.getName();
                if (!entry.isDirectory() && name.startsWith(parent) && name.endsWith(".jar")) {
                    paths.add(jarfile.toURI().toURL()+"!"+entry.getName());
                }
            }
        }
        catch (IOException ioe) {
            LOG.error("Could not search jar file #1 due to an IOException", ioe, jarfile.toString());
        }
    }

}
