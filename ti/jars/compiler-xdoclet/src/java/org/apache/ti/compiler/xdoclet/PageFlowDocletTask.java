package org.apache.ti.compiler.xdoclet;

import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import xdoclet.DocletTask;
import xdoclet.util.Translator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Page Flow XDoclet task
 */
public class PageFlowDocletTask extends DocletTask {

    private HashMap _buildMessages = new HashMap();   // String filename -> List messages
    private String _webContentDir;
    private String _srcDir;

    public PageFlowDocletTask() {
        addSubTask(new PageFlowDocletSubTask(this));
    }

    /**
     * Set the root of web content, for finding referenced web resources (like JSPs).
     */
    public void setWebContentDir(String webContentDir) {
        _webContentDir = webContentDir;
    }

    public String getWebContentDir() {
        return _webContentDir;
    }

    public String getSrcDir() {
        return _srcDir;
    }

    public void setSrcDir(String srcDir) {
        _srcDir = srcDir;
    }

    /**
     * @throws BuildException
     */
    protected void start() throws BuildException {
        try {
            super.start();
        }
        finally {
            // list any warnings and errors
            boolean overallError = false;
            if (_buildMessages != null) {
                Iterator i = _buildMessages.keySet().iterator();

                while (i.hasNext()) {
                    String sourceFile = (String) i.next();
                    List messages = (List) _buildMessages.get(sourceFile);
                    int errorCount = 0;
                    int warningCount = 0;

                    for (Iterator j = messages.iterator(); j.hasNext();) {
                        BuildMessage message = (BuildMessage) j.next();
                        System.err.println();
                        System.err.print(sourceFile);
                        System.err.print(": ");

                        if (message.getLine() > 0) {
                            String[] args = {new Integer(message.getLine()).toString()};
                            System.err.println(XDocletCompilerUtils.getMessage("compiler.line", args));
                        }

                        if (message.isError()) {
                            overallError = true;
                            ++errorCount;
                        } else {
                            System.err.print(XDocletCompilerUtils.getMessage("compiler.warning", null));
                            ++warningCount;
                        }

                        System.err.println(message.getMessage());
                    }

                    System.err.println(XDocletCompilerUtils.getMessage("compiler.build.results",
                            new String[]{new Integer(errorCount).toString(),
                                    new Integer(warningCount).toString(),
                                    sourceFile}));
                }
            }

            _buildMessages = null;

            if (overallError) {
                System.err.println(XDocletCompilerUtils.getMessage("compiler.build.failed", null));
                throw new PageFlowDocletBuildException();
            }
        }
    }

    public void addError(String error, SourcePosition sourcePosition) {
        assert sourcePosition != null;
        String sourceFilePath = sourcePosition.file().getPath();
        int line = sourcePosition.line();
        addError(error, sourceFilePath, line);
    }

    public void addError(String error, String sourceFile, int line) {
        List messages = (List) _buildMessages.get(sourceFile);

        if (messages == null) {
            messages = new ArrayList();
            _buildMessages.put(sourceFile, messages);
        }

        messages.add(new BuildMessage(error, line, true));
    }

    public void addWarning(String warning, SourcePosition sourcePosition) {
        assert sourcePosition != null;
        String sourceFilePath = sourcePosition.file().getPath();
        int line = sourcePosition.line();
        addWarning(warning, sourceFilePath, line);
    }

    public void addWarning(String warning, String sourceFile, int line) {
        List messages = (List) _buildMessages.get(sourceFile);

        if (messages == null) {
            messages = new ArrayList();
            _buildMessages.put(sourceFile, messages);
        }

        messages.add(new BuildMessage(warning, line, false));
    }

    private static class BuildMessage {

        private String _message;
        private boolean _error;
        private int _line;

        public BuildMessage(String message, int line, boolean error) {
            _message = message;
            _error = error;
            _line = line;
        }

        public final String getMessage() {
            return _message;
        }

        public final boolean isError() {
            return _error;
        }

        public final int getLine() {
            return _line;
        }
    }

    protected void validateOptions() throws BuildException {
        if (_webContentDir == null) {
            throw new BuildException(Translator.getString(xdoclet.XDocletMessages.class, "ATTRIBUTE_NOT_PRESENT_ERROR",
                    new String[]{"webcontentdir"}), getLocation());
        }
        if (_srcDir == null) {
            throw new BuildException(Translator.getString(xdoclet.XDocletMessages.class, "ATTRIBUTE_NOT_PRESENT_ERROR",
                    new String[]{"srcdir"}), getLocation());
        }
        FileSet defaultFileSet = new FileSet();
        defaultFileSet.setDir(new File(_srcDir));
        defaultFileSet.setIncludes("**/*.java");
        addFileset(defaultFileSet);
        super.validateOptions();
    }

    public static PageFlowDocletTask get() {
        return PageFlowDocletSubTask.get().getPageFlowDocletTask();
    }
}
