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

package org.apache.struts2.osgi.admin.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.osgi.BundleAccessor;
import org.apache.struts2.osgi.OsgiHost;
import org.apache.struts2.osgi.StrutsOsgiListener;
import org.apache.struts2.util.ServletContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BundlesAction extends ActionSupport implements ServletContextAware {

    private String id;

    private BundleAccessor bundleAccessor;
    private Configuration configuration;
    private OsgiHost osgiHost;

    public BundlesAction() {
    }

    public String index() {
        return SUCCESS;
    }

    public String view() {
        return SUCCESS;
    }

    public String start() throws BundleException {
        Bundle bundle = osgiHost.getBundles().get(id);
        try {
            bundle.start();

            //start fires the BundleEvent.STARTED, which load the config
            //we need to wait until the config is loaded from that bundle
            //there no easy way/elegant way to know if the bundle is being processed
            Thread.sleep(1000);
        } catch (Exception e) {
            addActionError(e.toString());
        }

        return view();
    }

    public String stop() throws BundleException {
        Bundle bundle = osgiHost.getBundles().get(id);
        try {
            bundle.stop();
        } catch (Exception e) {
            addActionError(e.toString());
        }

        return view();
    }

    public String update() throws BundleException {
        Bundle bundle = osgiHost.getBundles().get(id);
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
        return osgiHost.getBundles().get(id);
    }

    public List<PackageConfig> getPackages() {
        List<PackageConfig> pkgs = new ArrayList<PackageConfig>();
        Bundle bundle = getBundle();
        if (bundle.getState() == Bundle.ACTIVE) {
            for (String name : bundleAccessor.getPackagesByBundle(bundle)) {
                PackageConfig packageConfig = configuration.getPackageConfig(name);
                if (packageConfig != null)
                    pkgs.add(packageConfig);
            }
        }
        return pkgs;
    }

    public Collection<Bundle> getBundles() {
        return osgiHost.getBundles().values();
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

    @Inject
    public void setBundleAccessor(BundleAccessor bundleAccessor) {
        this.bundleAccessor = bundleAccessor;
    }

    public void setServletContext(ServletContext servletContext) {
        osgiHost = (OsgiHost) servletContext.getAttribute(StrutsOsgiListener.OSGI_HOST);
    }
}
