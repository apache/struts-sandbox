package org.apache.struts2.osgi.admin.actions;

import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.apache.struts2.osgi.BundleAccessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.Configuration;

import java.util.*;
import java.lang.reflect.Array;

public class BundlesAction extends DefaultActionSupport {

    private String id;

    private BundleAccessor bundleAccessor;
    private Configuration configuration;

    public String index() {
        return SUCCESS;
    }

    public String view() {
        return SUCCESS;
    }

    public String start() throws BundleException {
        Bundle bundle = bundleAccessor.getBundles().get(id);
        try {
            bundle.start();
        } catch (Exception e) {
            addActionError(e.toString());
        }

        return view();
    }

    public String stop() throws BundleException {
        Bundle bundle = bundleAccessor.getBundles().get(id);
        try {
            bundle.stop();
        } catch (Exception e) {
            addActionError(e.toString());
        }

        return view();
    }

    public String update() throws BundleException {
        Bundle bundle = bundleAccessor.getBundles().get(id);
        try {
            bundle.update();
        } catch (Exception e) {
            addActionError(e.toString());
        }

        return view();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bundle getBundle() {
        return bundleAccessor.getBundles().get(id);
    }

    @Inject
    public void setBundleAccessor(BundleAccessor bundleAccessor) {
        this.bundleAccessor = bundleAccessor;
    }

    public List<PackageConfig> getPackages() {
        List<PackageConfig> pkgs = new ArrayList<PackageConfig>();
        Bundle bundle = getBundle();
        for (String name : bundleAccessor.getPackagesByBundle(bundle)) {
            pkgs.add(configuration.getPackageConfig(name));
        }
        return pkgs;
    }

    public Collection<Bundle> getBundles() {
        return bundleAccessor.getBundles().values();
    }

    public String displayProperty(Object obj) {
        if (obj.getClass().isArray()) {
            return Arrays.asList((Object[])obj).toString();
        } else {
            return obj.toString();
        }
    }

    public String getBundleState(Bundle bundle) {
        switch (bundle.getState()) {
            case Bundle.ACTIVE : return "Active";
            case Bundle.INSTALLED : return "Installed";
            case Bundle.RESOLVED : return "Resolved";
            case Bundle.STARTING : return "Starting";
            case Bundle.STOPPING : return "Stopping";
            case Bundle.UNINSTALLED : return "Uninstalled";
            default : throw new IllegalStateException("Invalid state");
        }
    }

    public boolean isAllowedAction(Bundle bundle, String val) {
        int state = -1;
        try {
            state = bundle.getState();
        } catch (Exception e) {
            addActionError("Unable to determine bundle state: " + e.getMessage());
            return false;
        }

        if ("start".equals(val)) {
            return state == Bundle.RESOLVED;
        } else if ("stop".equals(val)) {
            return state == Bundle.ACTIVE;
        } else if ("update".equals(val)) {
            return state == Bundle.ACTIVE || state == Bundle.INSTALLED
                    || state == Bundle.RESOLVED;
        }
        throw new IllegalArgumentException("Invalid state");
    }

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}