package org.apache.struts2.osgi.admin.services;

import org.apache.struts2.osgi.admin.services.shell.FelixShellService;
import org.apache.struts2.osgi.admin.services.shell.ShellService;

/**
 * DI anyone? Container injection is no working in bundles
 *
 */
public class ServicesFactory {
    private static ServicesFactory self;

    private ServicesFactory() {
    }

    public static ServicesFactory getInstance() {
        if (self == null)
            self = new ServicesFactory();
        return self;
    }

    public ShellService getShellService() {
        return new FelixShellService();
    }
}
