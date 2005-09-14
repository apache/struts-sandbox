package org.apache.ti.compiler.xdoclet;

import org.apache.tools.ant.BuildException;

/**
 * Extension of ant's BuildException that does not print a stack trace. This is used to
 * signal ant that the build has failed from the doclet task, but without the
 * XJavaDocTask superclass printing out a stacktrace, so that pageflow build error messages
 * will be easier to find.
 */
public class PageFlowDocletBuildException extends BuildException {

    public PageFlowDocletBuildException() {
        super();
    }

    public void printStackTrace() {
        // no-op so we won't let XJavaDocTask print out a stack when the build fails
    }
}
