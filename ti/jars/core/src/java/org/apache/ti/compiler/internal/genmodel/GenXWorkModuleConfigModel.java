/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.compiler.internal.genmodel;

import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.MergedControllerAnnotation;
import org.apache.ti.compiler.internal.grammar.WebappPathOrActionType;
import org.apache.ti.compiler.internal.model.XWorkActionModel;
import org.apache.ti.compiler.internal.model.XWorkModuleConfigModel;
import org.apache.ti.compiler.internal.model.XWorkResultModel;
import org.apache.ti.compiler.internal.model.XmlModelWriterException;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class GenXWorkModuleConfigModel
        extends XWorkModuleConfigModel
        implements JpfLanguageConstants {
    private static final boolean CASE_INSENSITIVE_FILES = new File("x").equals(new File("X"));
    private ClassDeclaration _jclass;
    private String _containingPackage;
    private File _strutsConfigFile;
    private File _sourceFile;
    private AnnotationProcessorEnvironment _env;
    private FlowControllerInfo _fcInfo;

    protected void recalculateStrutsConfigFile() throws IOException, FatalCompileTimeException {
        _strutsConfigFile = calculateStrutsConfigFile(); // caching this
    }

    FlowControllerInfo getFlowControllerInfo() {
        return _fcInfo;
    }

    public GenXWorkModuleConfigModel(File sourceFile, ClassDeclaration jclass, AnnotationProcessorEnvironment env,
                                     FlowControllerInfo fcInfo, boolean checkOnly, Diagnostics diagnostics)
            throws IOException, FatalCompileTimeException {
        super(jclass.getQualifiedName());

        _jclass = jclass;
        _containingPackage = jclass.getPackage().getQualifiedName();
        _sourceFile = sourceFile;
        _env = env;
        assert fcInfo != null;
        _fcInfo = fcInfo;

        recalculateStrutsConfigFile();

        if (checkOnly) {
            return;
        }

        if (_jclass != null) {
            MergedControllerAnnotation mca = fcInfo.getMergedControllerAnnotation();
            setNestedPageFlow(mca.isNested());
            setLongLivedPageFlow(mca.isLongLived());

            //addMessageBundles( mca.getMessageBundles() );
            addSimpleActions(mca.getSimpleActions());
            setMultipartHandler(mca.getMultipartHandler());
            GenXWorkResultModel.addForwards(mca.getForwards(), this, _jclass, this, null);

            // TODO: comment
            addForward(new XWorkResultModel("_auto", "", this));

            GenXWorkExceptionHandlerModel.addCatches(mca.getCatches(), this, _jclass, this, CONTROLLER_TAG_NAME);
            addTilesDefinitionsConfigs(mca.getTilesDefinitionsConfigs());
            setAdditionalValidatorConfigs(mca.getCustomValidatorConfigs());

            addActionMethods();
            inferBeginAction(_jclass, fcInfo);
            addFormBeans(_jclass);
        }

        if (fcInfo != null) {
            setSharedFlows(fcInfo.getSharedFlowTypeNames());
            setReturnToActionDisabled(!fcInfo.isNavigateToActionEnabled());
            setReturnToPageDisabled(!fcInfo.isNavigateToPageEnabled());
        }
    }

    private void inferBeginAction(ClassDeclaration jclass, FlowControllerInfo fcInfo) {
        boolean isAbstract = jclass.hasModifier(Modifier.ABSTRACT);

        if (!isAbstract && !WebappPathOrActionType.actionExists(BEGIN_ACTION_NAME, jclass, null, getEnv(), fcInfo, true)) {
            XWorkActionModel inferredBeginAction = new XWorkActionModel(BEGIN_ACTION_NAME, this);
            inferredBeginAction.setSimpleAction(true);
            inferredBeginAction.setDefaultForwardName("success");

            XWorkResultModel fwd = new XWorkResultModel("success", BEGIN_ACTION_NAME + getDefaultFileExtension(), this);
            inferredBeginAction.addForward(fwd);
            inferredBeginAction.setComment("(implicit)");
            addAction(inferredBeginAction);
        }
    }

    private void addFormBeans(ClassDeclaration jclass) {
        Collection innerTypes = CompilerUtils.getClassNestedTypes(jclass);

        for (Iterator ii = innerTypes.iterator(); ii.hasNext();) {
            TypeDeclaration innerType = (TypeDeclaration) ii.next();

            if (innerType instanceof ClassDeclaration) {
                ClassDeclaration innerClass = (ClassDeclaration) innerType;

                if (innerType.hasModifier(Modifier.PUBLIC) &&
                        CompilerUtils.isAssignableFrom(PAGEFLOW_FORM_CLASS_NAME, innerClass, _env)) {
                    addFormBean(innerClass, null);
                }
            }
        }
    }

    /**
     * @return the typename of the form bean class
     */
    String addFormBean(TypeDeclaration formType, XWorkActionModel usedByAction) {
        String formClass = CompilerUtils.getFormClassName(formType, _env);

        //
        // Use the actual type of form to create the name.
        // This avoids conflicts if there are multiple forms using the
        // ANY_FORM_CLASS_NAME type.
        //
        String actualType = CompilerUtils.getLoadableName(formType);

        //
        // See if the app already has a form-bean of this type.  If so,
        // we'll just use it; otherwise, we need to create it.
        //
        boolean usesPageFlowScopedFormBean = (usedByAction != null) ? (usedByAction.getFormBeanMember() != null) : false;
        getMessageResourcesFromForm(formType, usedByAction);

        return formClass;
    }

    /*
    private void addMessageBundles( Collection messageBundles )
    {
        if ( messageBundles != null )
        {
            for ( Iterator ii = messageBundles.iterator(); ii.hasNext(); )
            {
                AnnotationInstance ann = ( AnnotationInstance ) ii.next();
                addMessageResources( new GenMessageBundleModel( this, ann ) );
            }
        }
    }
    */
    private void addSimpleActions(Collection simpleActionAnnotations) {
        if (simpleActionAnnotations != null) {
            for (Iterator ii = simpleActionAnnotations.iterator(); ii.hasNext();) {
                AnnotationInstance ann = (AnnotationInstance) ii.next();
                addAction(new GenSimpleActionModel(ann, this, _jclass));
            }
        }
    }

    private void setMultipartHandler(String mpHandler) {
        if (mpHandler != null) {
            if (mpHandler.equals(MULTIPART_HANDLER_DISABLED_STR)) {
                setMultipartHandlerClassName("none");
            } else {
                setMultipartHandlerClassName(COMMONS_MULTIPART_HANDLER_CLASSNAME);

                if (mpHandler.equals(MULTIPART_HANDLER_DISK_STR)) {
                    setMemFileSize("0K");
                } else {
                    assert mpHandler.equals(MULTIPART_HANDLER_MEMORY_STR) : mpHandler;
                }
            }
        }
    }

    private void addTilesDefinitionsConfigs(List tilesDefinitionsConfigs) {
        if ((tilesDefinitionsConfigs == null) || tilesDefinitionsConfigs.isEmpty()) {
            return;
        }

        List paths = new ArrayList();

        for (Iterator ii = tilesDefinitionsConfigs.iterator(); ii.hasNext();) {
            String definitionsConfig = (String) ii.next();

            if ((definitionsConfig != null) && (definitionsConfig.length() > 0)) {
                paths.add(definitionsConfig);
            }
        }

        setTilesDefinitionsConfigs(paths);
    }

    private void addActionMethods() {
        MethodDeclaration[] actionMethods = CompilerUtils.getClassMethods(_jclass, ACTION_TAG_NAME);

        for (int i = 0; i < actionMethods.length; i++) {
            MethodDeclaration actionMethod = actionMethods[i];

            if (!actionMethod.hasModifier(Modifier.ABSTRACT)) {
                XWorkActionModel actionModel = new GenXWorkActionModel(actionMethod, this, _jclass);
                addAction(actionModel);

                ParameterDeclaration[] params = actionMethod.getParameters();

                if (params.length > 0) {
                    ParameterDeclaration param1 = params[0];
                    TypeInstance paramType = param1.getType();

                    if (paramType instanceof DeclaredType) {
                        getMessageResourcesFromForm(CompilerUtils.getDeclaration((DeclaredType) paramType), actionModel);
                    }
                }
            }
        }
    }

    private void getMessageResourcesFromForm(TypeDeclaration formTypeDecl, XWorkActionModel actionModel) {
        if (!(formTypeDecl instanceof ClassDeclaration)) {
            return;
        }

        ClassDeclaration formClassDecl = (ClassDeclaration) formTypeDecl;

        AnnotationInstance ann = CompilerUtils.getAnnotation(formClassDecl, FORM_BEAN_TAG_NAME, true);

        if (ann != null) {
            String defaultMessageResources = CompilerUtils.getString(ann, MESSAGE_BUNDLE_ATTR, true);

            if (defaultMessageResources != null) {
                String key = "formMessages:" + CompilerUtils.getLoadableName(formClassDecl);

                /* TODO: re-add message bundle support
                for ( Iterator ii = getMessageResourcesList().iterator(); ii.hasNext(); )
                {
                    MessageResourcesModel i = ( MessageResourcesModel ) ii.next();
                    if ( key.equals( i.getKey() ) && i.getParameter().equals( defaultMessageResources ) ) return;
                }

                MessageResourcesModel mrm = new MessageResourcesModel( this );
                mrm.setKey( key );
                mrm.setParameter( defaultMessageResources );
                mrm.setReturnNull( true );
                addMessageResources( mrm );
                if ( actionModel != null ) actionModel.setFormBeanMessageResourcesKey( key );
                */
            }
        }
    }

    protected String getMergeFileName() {
        return null; // In Beehive, this was Struts merge.  Will we have XWork-merge?
    }

    public void writeToFile() throws FileNotFoundException, IOException, XmlModelWriterException, FatalCompileTimeException {
        PrintWriter writer = getEnv().getFiler().createTextFile(_strutsConfigFile);

        try {
            writeXml(writer, getMergeFile(getMergeFileName()));
        } finally {
            writer.close();
        }
    }

    public boolean isStale() throws FatalCompileTimeException {
        return isStale(getMergeFile(getMergeFileName()));
    }

    String getOutputFileURI(String filePrefix) {
        return getOutputFilePath(filePrefix, _containingPackage);
    }

    String getConfigFilePath() {
        return getOutputFilePath(PAGE_FLOW_CONFIG_FILE_BASENAME, _containingPackage);
    }

    protected String getContainingPackage() {
        return _containingPackage;
    }

    private File calculateStrutsConfigFile() {
        return new File(getConfigFilePath());
    }

    /**
     * Tell whether the XWork output file is out of date, based on the
     * file times of the source file and the (optional) xwork-merge file.
     */
    public boolean isStale(File mergeFile) {
        //
        // We can write to the file if it doesn't exist yet.
        //
        if (!_strutsConfigFile.exists()) {
            return true;
        }

        long lastWrite = _strutsConfigFile.lastModified();

        if ((mergeFile != null) && mergeFile.exists() && (mergeFile.lastModified() > lastWrite)) {
            return true;
        }

        if (_sourceFile.lastModified() > lastWrite) {
            return true;
        }

        return false;
    }

    /**
     * In some cases, canWrite() does not guarantee that a FileNotFoundException will not
     * be thrown when trying to write to a file.  This method actually tries to overwrite
     * the file as a test to see whether it's possible.
     */
    public boolean canWrite() {
        if (!_strutsConfigFile.canWrite()) {
            return false;
        }

        try {
            //
            // This appears to be the only way to predict whether the file can actually be
            // written to; it may be that canWrite() returns true, but the file permissions
            // (NTFS only?) will cause an exception to be thrown.
            //
            new FileOutputStream(_strutsConfigFile, true).close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public File getStrutsConfigFile() {
        return _strutsConfigFile;
    }

    public File getMergeFile(String mergeFileName) throws FatalCompileTimeException {
        if (mergeFileName != null) {
            return CompilerUtils.getFileRelativeToSourceFile(_jclass, mergeFileName, getEnv());
        }

        return null;
    }

    protected String getHeaderComment(File mergeFile) throws FatalCompileTimeException {
        StringBuffer comment = new StringBuffer(" Generated from ");
        comment.append(getWebappRelativePath(_sourceFile));

        if (mergeFile != null) {
            comment.append(" and ").append(getWebappRelativePath(mergeFile));
        }

        comment.append(" on ").append(new Date().toString()).append(' ');

        return comment.toString();
    }

    private String getWebappRelativePath(File file) throws FatalCompileTimeException {
        String filePath = file.getAbsoluteFile().getPath();
        String[] sourceRoots = CompilerUtils.getWebSourceRoots(_env);

        //
        // Look through the source roots.
        //
        for (int i = 0; i < sourceRoots.length; i++) {
            String sourceRoot = sourceRoots[i].replace('/', File.separatorChar);

            if (pathStartsWith(filePath, sourceRoot)) {
                return filePath.substring(sourceRoot.length()).replace('\\', '/');
            }
        }

        //
        // Look in the web content root.
        //
        String[] webContentRoots = CompilerUtils.getWebContentRoots(getEnv());

        for (int i = 0; i < webContentRoots.length; i++) {
            String webContentRoot = webContentRoots[i].replace('/', File.separatorChar);

            if (pathStartsWith(filePath, webContentRoot)) {
                return filePath.substring(webContentRoot.length()).replace('\\', '/');
            }
        }

        assert false : "could not calculate webapp-relative file from " + file;

        return file.toString();
    }

    private static boolean pathStartsWith(String path, String prefix) {
        if (CASE_INSENSITIVE_FILES) {
            return path.toLowerCase().startsWith(prefix.toLowerCase());
        } else {
            return path.startsWith(prefix);
        }
    }

    AnnotationProcessorEnvironment getEnv() {
        return _env;
    }

    /**
     * Get the file extension that is inferred from an action name if there is no explicit "success" forward.
     *
     * @todo make this configurable.
     */
    String getDefaultFileExtension() {
        return '.' + JSP_FILE_EXTENSION;
    }

    protected String getValidationFilePrefix() {
        return "pageflow-validation";
    }
}
