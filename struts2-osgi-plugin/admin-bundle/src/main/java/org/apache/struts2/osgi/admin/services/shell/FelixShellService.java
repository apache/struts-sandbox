package org.apache.struts2.osgi.admin.services.shell;

import java.io.PrintStream;

import org.apache.felix.shell.ShellService;
import org.apache.struts2.osgi.DefaultBundleAccessor;
import org.osgi.framework.ServiceReference;

public class FelixShellService implements org.apache.struts2.osgi.admin.services.shell.ShellService {

    public void executeCommand(String commandLine, PrintStream out, PrintStream err) throws Exception {
        ShellService shellService = getShellService();
        if (shellService != null)
            shellService.executeCommand(commandLine, out, err);
        else
            err.println("Shell service is installed");
    }

    private ShellService getShellService() {
        //bundle can be de-activated, so keeping a reference aorund is not a good idea
        DefaultBundleAccessor bundleAcessor = DefaultBundleAccessor.getInstance();
        ServiceReference ref = bundleAcessor.getServiceReference(ShellService.class.getName());
        return (ShellService) bundleAcessor.getService(ref);
    }

}
