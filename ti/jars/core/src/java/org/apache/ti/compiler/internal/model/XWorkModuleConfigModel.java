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
package org.apache.ti.compiler.internal.model;

import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.model.validation.ValidationModel;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class XWorkModuleConfigModel
        extends AbstractResultContainer
        implements XWorkResultContainer, XWorkExceptionHandlerContainer, JpfLanguageConstants {

    static final String PATH_RESULT = "pathResult";
    static final String NAVIGATE_TO_PAGE_RESULT = "navigateToPageResult";
    static final String NAVIGATE_TO_ACTION_RESULT = "navigateToActionResult";
    static final String RETURN_ACTION_RESULT = "returnActionResult";

    //protected boolean _isRootApp = false;
    private HashMap _actions = new HashMap();
    private ArrayList _exceptionCatches = new ArrayList();
    private ArrayList _messageResources = new ArrayList();
    private ValidationModel _validationModel;
    private List _additionalValidatorConfigs;

    private boolean _returnToPageDisabled = true;
    private boolean _returnToActionDisabled = true;
    private boolean _isNestedPageFlow = false;
    private boolean _isLongLivedPageFlow = false;
    private boolean _isSharedFlow = false;
    /**
     * Map of name to typename
     */
    private Map _sharedFlows = null;
    private String _controllerClassName = null;
    private String _multipartHandlerClassName = null;
    private String _memFileSize = null;
    private List _tilesDefinitionsConfigs = null;


    protected static final String DUPLICATE_ACTION_COMMENT = "Note that there is more than one action with path \"{0}\"."
            + "  Use a form-qualified action path if this is not the "
            + "one you want.";

    protected static final String PAGEFLOW_REQUESTPROCESSOR_CLASSNAME
            = PAGEFLOW_PACKAGE + ".PageFlowRequestProcessor";

    protected static final String PAGEFLOW_CONTROLLER_CONFIG_CLASSNAME
            = PAGEFLOW_PACKAGE + ".config.PageFlowControllerConfig";

    protected static final String XWORK_CONFIG_PREFIX = "xwork-pageflow";
    protected static final char STRUTS_CONFIG_SEPARATOR = '-';
    protected static final String WEBINF_DIR_NAME = "WEB-INF";
    protected static final String XWORK_CONFIG_OUTPUT_DIR = "_pageflow";
    protected static final String VALIDATOR_PLUG_IN_CLASSNAME = STRUTS_PACKAGE + ".validator.ValidatorPlugIn";
    protected static final String VALIDATOR_PATHNAMES_PROPERTY = "pathnames";
    protected static final String TILES_PLUG_IN_CLASSNAME = STRUTS_PACKAGE + ".tiles.TilesPlugin";
    protected static final String TILES_DEFINITIONS_CONFIG_PROPERTY = "definitions-config";
    protected static final String TILES_MODULE_AWARE_PROPERTY = "moduleAware";
    protected static final String NETUI_VALIDATOR_RULES_URI = '/' + WEBINF_DIR_NAME + "/beehive-netui-validator-rules.xml";
    protected static final String STRUTS_VALIDATOR_RULES_URI = '/' + WEBINF_DIR_NAME + "/validator-rules.xml";


    public XWorkModuleConfigModel(String controllerClassName) {
        super(null);
        setParentApp(this);
        _controllerClassName = controllerClassName;

        //
        // Add a reference for the default validation message resources (in beehive-netui-pageflow.jar).
        //
        /* TODO: re-add message bundle support
        MessageResourcesModel mrm = new MessageResourcesModel( this );
        mrm.setParameter( DEFAULT_VALIDATION_MESSAGE_BUNDLE );
        mrm.setKey( DEFAULT_VALIDATION_MESSAGE_BUNDLE_NAME );
        mrm.setReturnNull( true );
        _messageResources.add( mrm );
        */
    }

    private void addDisambiguatedAction(XWorkActionModel mapping) {
        if (mapping.getFormBeanType() != null) {
            String qualifiedPath = getFormQualifiedActionPath(mapping);
            XWorkActionModel qualifiedMapping = new XWorkActionModel(mapping, qualifiedPath);
            qualifiedMapping.setUnqualifiedName(mapping.getName());
            _actions.put(qualifiedPath, qualifiedMapping);
        }
    }

    /**
     * Adds a new Action to this XWorkModuleConfigModel.
     */
    public void addAction(XWorkActionModel mapping) {
        String mappingPath = mapping.getName();
        XWorkActionModel conflictingAction = (XWorkActionModel) _actions.get(mappingPath);

        if (conflictingAction != null) {
            XWorkActionModel defaultMappingForThisPath = conflictingAction;

            //
            // If the new action mapping takes no form, then it has the highest precedence, and replaces the existing
            // "natural" mapping for the given path.  Otherwise, replace the existing one if the existing one has a
            // form bean and if the new mapping's form bean type comes alphabetically before the existing one's.
            //
            if (mapping.getFormBeanType() == null
                    || (conflictingAction.getFormBeanType() != null
                    && mapping.getFormBeanType().compareTo(conflictingAction.getFormBeanType()) < 0)) {
                _actions.put(mappingPath, mapping);
                defaultMappingForThisPath = mapping;
                conflictingAction.setOverloaded(false);
            }

            addDisambiguatedAction(mapping);
            addDisambiguatedAction(conflictingAction);
            defaultMappingForThisPath.setOverloaded(true);
            defaultMappingForThisPath.setComment(DUPLICATE_ACTION_COMMENT.replaceAll("\\{0\\}", mappingPath));  // @TODO I18N
        } else {
            _actions.put(mappingPath, mapping);
        }
    }

    protected String getFormQualifiedActionPath(XWorkActionModel action) {
        assert action.getFormBeanType() != null : "action " + action.getName() + " has no form bean";
        String beanType = action.getFormBeanType();
        return action.getName() + '_' + makeFullyQualifiedBeanName(beanType);
    }

    /**
     * Implemented for {@link XWorkExceptionHandlerContainer}.
     */
    public void addException(XWorkExceptionHandlerModel exceptionModel) {
        _exceptionCatches.add(exceptionModel);
    }

    protected static String makeFullyQualifiedBeanName(String formType) {
        return formType.replace('.', '_').replace('$', '_');
    }

    protected static class ActionComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            assert o1 instanceof XWorkActionModel && o2 instanceof XWorkActionModel;

            XWorkActionModel am1 = (XWorkActionModel) o1;
            XWorkActionModel am2 = (XWorkActionModel) o2;

            assert ! am1.getName().equals(am2.getName());     // there should be no duplicate paths
            return am1.getName().compareTo(am2.getName());
        }
    }

    protected List getExceptionCatchesList() {
        return _exceptionCatches;
    }

    protected List getSortedActions() {
        ArrayList sortedActions = new ArrayList();
        sortedActions.addAll(_actions.values());
        Collections.sort(sortedActions, new ActionComparator());
        return sortedActions;
    }

    Collection getActions() {
        return _actions.values();
    }

    protected List getMessageResourcesList() {
        return _messageResources;
    }

    /**
     * Get the MessageResourcesModel for which no "key" is set (the default one used at runtime).
     */
    /* TODO: re-add message bundle support
    public MessageResourcesModel getDefaultMessageResources()
    {
        for ( java.util.Iterator ii = _messageResources.iterator(); ii.hasNext(); )  
        {
            MessageResourcesModel i = ( MessageResourcesModel ) ii.next();
            if ( i.getKey() == null ) return i;
        }
        
        return null;
    }
    */
    public boolean isReturnToPageDisabled() {
        return _returnToPageDisabled;
    }

    public boolean isReturnToActionDisabled() {
        return _returnToActionDisabled;
    }

    public void setReturnToPageDisabled(boolean disabled) {
        _returnToPageDisabled = disabled;
    }

    public void setReturnToActionDisabled(boolean disabled) {
        _returnToActionDisabled = disabled;
    }

    public void setAdditionalValidatorConfigs(List additionalValidatorConfigs) {
        if (additionalValidatorConfigs != null && ! additionalValidatorConfigs.isEmpty()) {
            _additionalValidatorConfigs = additionalValidatorConfigs;
        }
    }

    public void setValidationModel(ValidationModel validationModel) {
        if (! validationModel.isEmpty())  // if there's nothing in the validation model, we don't care about it.
        {
            _validationModel = validationModel;
        }
    }

    /**
     * Get the XWork package/namespace of this controller.
     */
    public String getNamespace() {
        int lastDot = _controllerClassName.lastIndexOf('.');
        if (lastDot == -1) return "/";
        return "/" + _controllerClassName.substring(0, lastDot).replace('.', '/');
    }

    private static void addResultType(XmlModelWriter xw, Element element, String name, String className) {
        Element resultTypeElement = xw.addElement(element, "result-type");
        resultTypeElement.setAttribute("name", name);
        resultTypeElement.setAttribute("class", className);
    }

    public void writeXml(PrintWriter writer, File mergeFile)
            throws IOException, XmlModelWriterException, FatalCompileTimeException {
        XmlModelWriter xw = new XmlModelWriter(mergeFile, "xwork",
                "-//OpenSymphony Group//XWork 1.0//EN",
                "http://www.opensymphony.com/xwork/xwork-1.0.dtd",
                getHeaderComment(mergeFile));

        Element root = xw.getDocument().getDocumentElement();

        Element packageElement = xw.addElement(root, "package");
        packageElement.setAttribute("name", getNamespace());
        packageElement.setAttribute("namespace", getNamespace());

        Element resultTypesElement = xw.addElement(packageElement, "result-types");
        addResultType(xw, resultTypesElement, PATH_RESULT, PAGEFLOW_XWORK_PACKAGE + ".PageFlowPathResult");
        addResultType(xw, resultTypesElement, NAVIGATE_TO_PAGE_RESULT, PAGEFLOW_XWORK_PACKAGE + ".NavigateToPageResult");
        addResultType(xw, resultTypesElement, NAVIGATE_TO_ACTION_RESULT, PAGEFLOW_XWORK_PACKAGE + ".NavigateToActionResult");
        addResultType(xw, resultTypesElement, RETURN_ACTION_RESULT, PAGEFLOW_XWORK_PACKAGE + ".ReturnActionResult");

        Element globalResults = xw.addElement(packageElement, "global-results");
        writeForwards(xw, globalResults);

        //
        // module metadata
        //
        writeModuleMetadataElement(xw, packageElement);

        //
        // action-mappings
        //
        writeActions(xw, packageElement);

        //
        // global-exceptions
        //
        writeExceptions(xw, packageElement);

        //
        // message-resources
        //
        //writeMessageResources( scElement );

        //
        // ValidatorPlugIn
        //
        //writeValidatorInit( scElement );

        //
        // TilesPlugin
        //
        //writeTilesInit( scElement );

        //
        // Write the file.
        //
        xw.write(writer);
    }

    private void writeActions(XmlModelWriter xw, Element parentElement) {
        List actionsList = getSortedActions();

        for (int i = 0; i < actionsList.size(); ++i) {
            XWorkActionModel am = (XWorkActionModel) actionsList.get(i);
            am.writeXML(xw, parentElement);
        }
    }

    private void writeExceptions(XmlModelWriter xw, Element parentElement) {
        List exceptionCatches = getExceptionCatchesList();

        if (exceptionCatches != null && ! exceptionCatches.isEmpty()) {
            for (int i = 0; i < exceptionCatches.size(); ++i) {
                XWorkExceptionHandlerModel ec = (XWorkExceptionHandlerModel) exceptionCatches.get(i);
                ec.writeXML(xw, parentElement);
            }
        }
    }

    protected void writeModuleMetadataElement(XmlModelWriter xw, Element parentElement) {
        Element metadataElement = xw.addElement(parentElement, "action");
        xw.addComment(metadataElement,
                "This is hopefully temporary. It's a dummy action with metadata (params) related to this module");
        metadataElement.setAttribute("name", "_moduleMetadata");

        addParam(xw, metadataElement, "controllerClassName", _controllerClassName);

        if (_isNestedPageFlow) addParam(xw, metadataElement, "nestedFlow", true);
        if (_isLongLivedPageFlow) addParam(xw, metadataElement, "longLivedFlow", true);
        if (_isSharedFlow) addParam(xw, metadataElement, "sharedFlow", true);
        if (isReturnToPageDisabled()) addParam(xw, metadataElement, "returnToPageDisabled", true);
        if (isReturnToActionDisabled()) addParam(xw, metadataElement, "returnToActionDisabled", true);

        if (_sharedFlows != null && _sharedFlows.size() > 0) {
            StringBuffer str = new StringBuffer();
            boolean first = true;

            for (java.util.Iterator i = _sharedFlows.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                if (! first) str.append(',');
                first = false;
                String name = (String) entry.getKey();
                String type = (String) entry.getValue();
                str.append(name).append('=').append(type);
            }

            addParam(xw, metadataElement, "sharedFlows", str.toString());
        }

        //
        // If there is not a default MessageResources element in the generated XML, add a special set-property
        // to communicate this to the runtime.
        //
        /*
        MessageResourcesDocument.MessageResources[] mrArray = scElement.getMessageResourcesArray();
        for ( int i = 0; i < mrArray.length; i++ )
        {
            MessageResourcesDocument.MessageResources messageResources = mrArray[i];
            if ( messageResources.getKey() == null ) return;
        }
        */
        addParam(xw, metadataElement, "missingDefaultMessages", true);
    }

    /*
    protected void writeValidatorInit( StrutsConfigDocument.StrutsConfig scElement )
    {
        if ( ( _validationModel != null && ! _validationModel.isEmpty() ) || _additionalValidatorConfigs != null )
        {
            PlugInDocument.PlugIn plugInElementToEdit = null;
            PlugInDocument.PlugIn[] existingPlugIns = scElement.getPlugInArray();
            
            for ( int i = 0; i < existingPlugIns.length; i++ )
            {
                PlugInDocument.PlugIn existingPlugIn = existingPlugIns[i];
                
                if ( VALIDATOR_PLUG_IN_CLASSNAME.equals( existingPlugIn.getClassName() ) )
                {
                    plugInElementToEdit = existingPlugIn;
                    break;
                }
            }
            
            if ( plugInElementToEdit == null )
            {
                plugInElementToEdit = scElement.addNewPlugIn();
                plugInElementToEdit.setClassName( VALIDATOR_PLUG_IN_CLASSNAME );
            }
            
            SetPropertyDocument.SetProperty[] existingSetProperties = plugInElementToEdit.getSetPropertyArray();
            
            for ( int i = 0; i < existingSetProperties.length; i++ )
            {
                if ( VALIDATOR_PATHNAMES_PROPERTY.equals( existingSetProperties[i].getProperty() ) )
                {
                    //
                    // This means that in the user's struts-merge file, there's already a "pathnames" set-property
                    // element.  We don't want to overwrite it.
                    //
                    return;
                }
            }
            
            SetPropertyDocument.SetProperty pathnamesProperty = plugInElementToEdit.addNewSetProperty();
            pathnamesProperty.setProperty( VALIDATOR_PATHNAMES_PROPERTY );
            StringBuffer pathNames = new StringBuffer();
            pathNames.append( NETUI_VALIDATOR_RULES_URI );
            pathNames.append( ",/WEB-INF/classes/" ).append( _validationModel.getOutputFileURI() );
            
            if ( _validationModel != null && ! _validationModel.isEmpty() )
            {
                pathNames.append( ',' ).append( _validationModel.getOutputFileURI() );
            }
            
            if ( _additionalValidatorConfigs != null )
            {
                for ( java.util.Iterator ii = _additionalValidatorConfigs.iterator(); ii.hasNext(); )  
                {
                    String configFile = ( String ) ii.next();
                    pathNames.append( ',' ).append( configFile );
                }
            }
            
            pathnamesProperty.setValue( pathNames.toString() );
        }
    }
    
    protected void writeTilesInit( StrutsConfigDocument.StrutsConfig scElement )
    {
        if ( _tilesDefinitionsConfigs == null || _tilesDefinitionsConfigs.isEmpty() )
        {
            return;
        }

        PlugInDocument.PlugIn plugInElementToEdit = null;
        PlugInDocument.PlugIn[] existingPlugIns = scElement.getPlugInArray();

        for ( int i = 0; i < existingPlugIns.length; i++ )
        {
            PlugInDocument.PlugIn existingPlugIn = existingPlugIns[i];

            if ( TILES_PLUG_IN_CLASSNAME.equals( existingPlugIn.getClassName() ) )
            {
                plugInElementToEdit = existingPlugIn;
                break;
            }
        }

        if ( plugInElementToEdit == null )
        {
            plugInElementToEdit = scElement.addNewPlugIn();
            plugInElementToEdit.setClassName( TILES_PLUG_IN_CLASSNAME );
        }

        boolean definitionsConfigIsSet = false;
        boolean moduleAwarePropertyIsSet = false;
        SetPropertyDocument.SetProperty[] existingSetProperties = plugInElementToEdit.getSetPropertyArray();

        for ( int i = 0; i < existingSetProperties.length; i++ )
        {
            String name = existingSetProperties[i].getProperty();

            if ( TILES_DEFINITIONS_CONFIG_PROPERTY.equals( name ) )
            {
                //
                // This means that in the user's struts-merge file, there's already a
                // "definitions-config" set-property element.  We don't want to overwrite it.
                //
                definitionsConfigIsSet = true;
            }

            if ( TILES_MODULE_AWARE_PROPERTY.equals( name ) )
            {
                // Make sure "moduleAware" is true
                moduleAwarePropertyIsSet = true;
            }
        }

        if ( !definitionsConfigIsSet )
        {
            SetPropertyDocument.SetProperty pathnamesProperty = plugInElementToEdit.addNewSetProperty();
            pathnamesProperty.setProperty( TILES_DEFINITIONS_CONFIG_PROPERTY );
            StringBuffer pathNames = new StringBuffer();
            boolean firstOne = true;

            for ( java.util.Iterator ii = _tilesDefinitionsConfigs.iterator(); ii.hasNext(); )  
            {
                String definitionsConfig = ( String ) ii.next();
                if ( ! firstOne ) pathNames.append( ',' );
                firstOne = false;
                pathNames.append( definitionsConfig );
            }
            pathnamesProperty.setValue( pathNames.toString() );
        }

        if ( !moduleAwarePropertyIsSet )
        {
            SetPropertyDocument.SetProperty pathnamesProperty = plugInElementToEdit.addNewSetProperty();
            pathnamesProperty.setProperty( TILES_MODULE_AWARE_PROPERTY );
            pathnamesProperty.setValue( "true" );
        }
    }
    */

    protected String getHeaderComment(File mergeFile)
            throws FatalCompileTimeException {
        return null;
    }

    public void setNestedPageFlow(boolean nestedPageFlow) {
        _isNestedPageFlow = nestedPageFlow;
    }

    public void setLongLivedPageFlow(boolean longLivedPageFlow) {
        _isLongLivedPageFlow = longLivedPageFlow;
    }

    public static String getOutputFilePath(String baseFileName, String containingPackage) {
        StringBuffer fileName = new StringBuffer(XWORK_CONFIG_OUTPUT_DIR);
        fileName.append('/');
        if (containingPackage != null) fileName.append(containingPackage.replace('.', '/'));
        fileName.append('/');
        fileName.append(baseFileName);
        fileName.append(".xml");
        return fileName.toString();
    }

    protected void setSharedFlow(boolean sharedFlow) {
        _isSharedFlow = sharedFlow;
    }

    protected void setSharedFlows(Map sharedFlows) {
        _sharedFlows = sharedFlows;
    }

    public String getMultipartHandlerClassName() {
        return _multipartHandlerClassName;
    }

    protected void setMultipartHandlerClassName(String multipartHandlerClassName) {
        _multipartHandlerClassName = multipartHandlerClassName;
    }

    public void setTilesDefinitionsConfigs(List tilesDefinitionsConfigs) {
        _tilesDefinitionsConfigs = tilesDefinitionsConfigs;
    }

    protected boolean isSharedFlow() {
        return _isSharedFlow;
    }

    /**
     * Get the threshold for keeping a file in memory when processing a multipart request.  An example is
     * <code>256K</code>
     */
    protected String getMemFileSize() {
        return _memFileSize;
    }

    /**
     * Set the threshold for keeping a file in memory when processing a multipart request.  An example is
     * <code>256K</code>
     */
    protected void setMemFileSize(String memFileSize) {
        _memFileSize = memFileSize;
    }

    public String getHandlerPrefix() {
        return "";
    }

    public String getDescription() {
        return _controllerClassName;
    }
}
