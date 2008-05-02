package org.apache.struts2.osgi.admin.services.shell;

import java.io.PrintStream;

/**
 * Facade to Felix ShellService so we are not tied to Felix
 *
 */
public interface ShellService {
    public void executeCommand(String commandLine, PrintStream out, PrintStream err) throws Exception;
}
