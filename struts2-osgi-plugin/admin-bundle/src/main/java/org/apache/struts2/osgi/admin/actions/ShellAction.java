package org.apache.struts2.osgi.admin.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.struts2.osgi.BundleAccessor;
import org.apache.struts2.osgi.admin.services.ServicesFactory;
import org.apache.struts2.osgi.admin.services.shell.ShellService;
import org.osgi.framework.ServiceReference;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.inject.Inject;

public class ShellAction extends ActionSupport {
    private String command;
    private String error;
    private String output;

    public String execute() {
        // get service
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errByteStream = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(outByteStream);
        PrintStream errStream = new PrintStream(errByteStream);

        try {
            ShellService shellService = ServicesFactory.getInstance().getShellService();
            shellService.executeCommand(command, outStream, errStream);
            output = outByteStream.toString();
            error = errByteStream.toString();
        } catch (Exception e) {
            error = e.getMessage();
        } finally {
            outStream.close();
            errStream.close();
        }

        return Action.SUCCESS;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getError() {
        return error;
    }

    public String getOutput() {
        return output;
    }
}
