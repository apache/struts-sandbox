package org.apache.ti.compiler.xdoclet;

import org.apache.ti.compiler.internal.processor.PageFlowAnnotationProcessor;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.DeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.env.AnnotationProcessorEnvironmentImpl;
import xdoclet.DocletContext;
import xdoclet.SubTask;
import xdoclet.XDocletException;
import xjavadoc.SourceClass;
import xjavadoc.XJavaDoc;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


/**
 * XJavaDoc subtask to run through a set of page flows in a webapp and generate Struts XML config files for them.
 */
public class PageFlowDocletSubTask extends SubTask {

    private SourceClass _currentSourceClass;
    private PageFlowDocletTask _pageFlowDocletTask;

    PageFlowDocletSubTask(PageFlowDocletTask task) {
        _pageFlowDocletTask = task;
    }

    /**
     * Main entry point for xjavadoc tasks. Here we iterate through all the classes found by xjavadoc and process
     * the ones whose annotations we recognize.
     *
     * @throws XDocletException
     */
    public void execute() throws XDocletException {
        Collection classes = getXJavaDoc().getSourceClasses();

        Iterator iter = classes.iterator();
        while (iter.hasNext()) {
            SourceClass sourceClass = (SourceClass) iter.next();
            HashMap options = new HashMap();
            options.put("-Aweb.content.root", _pageFlowDocletTask.getWebContentDir());
            options.put("-destdir", _pageFlowDocletTask.getDestDir().getAbsolutePath());
            options.put("-sourcepath", new File(_pageFlowDocletTask.getSrcDir()).getAbsolutePath());
            AnnotationProcessorEnvironment env =
                    AnnotationProcessorEnvironmentImpl.get(getContext(), this, sourceClass, options);
            AnnotationTypeDeclaration[] decls = DeclarationImpl.getAllAnnotations();    // TODO: filter appropriately

            PageFlowAnnotationProcessor pfap = new PageFlowAnnotationProcessor(decls, env);

            try {
                _currentSourceClass = sourceClass;
                pfap.process();
            }
            finally {
                _currentSourceClass = null;
            }
        }
    }

    public static PageFlowDocletSubTask get() {
        SubTask subtask = DocletContext.getInstance().getActiveSubTask();
        assert subtask instanceof PageFlowDocletSubTask : subtask.getClass().getName();
        return (PageFlowDocletSubTask) subtask;
    }

    public XJavaDoc getXJavaDoc() {
        return super.getXJavaDoc();
    }

    public SourceClass getCurrentSourceClass() {
        return _currentSourceClass;
    }

    PageFlowDocletTask getPageFlowDocletTask() {
        return _pageFlowDocletTask;
    }
}
