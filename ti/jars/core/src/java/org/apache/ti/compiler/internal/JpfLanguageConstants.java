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
package org.apache.ti.compiler.internal;

public interface JpfLanguageConstants {

    public static final String TI_PACKAGE = "org.apache.ti";
    public static final String PAGEFLOW_PACKAGE = TI_PACKAGE + ".pageflow";
    public static final String PAGEFLOW_XWORK_PACKAGE = PAGEFLOW_PACKAGE + ".xwork";
    public static final String PAGEFLOW_INTERNAL_PACKAGE = PAGEFLOW_PACKAGE + ".internal";
    public static final String ANNOTATIONS_CLASSNAME = PAGEFLOW_PACKAGE + ".annotations.ti";

    public static final String ACTION_TAG_NAME = "action";
    public static final String SIMPLE_ACTION_TAG_NAME = "simpleAction";
    public static final String EXCEPTION_HANDLER_TAG_NAME = "exceptionHandler";
    public static final String FORWARD_TAG_NAME = "forward";
    public static final String CATCH_TAG_NAME = "handleException";
    public static final String CONTROLLER_TAG_NAME = "controller";
    public static final String MESSAGE_BUNDLE_TAG_NAME = "messageBundle";
    public static final String VIEW_PROPERTIES_TAG_NAME = "viewProperties";
    public static final String VALIDATION_LOCALE_RULES_TAG_NAME = "validationLocaleRules";
    public static final String VALIDATION_BEAN_TAG_NAME = "validationBean";
    public static final String VALIDATABLE_PROPERTY_TAG_NAME = "validatableProperty";
    public static final String VALIDATABLE_BEAN_TAG_NAME = "validatableBean";
    public static final String FORM_BEAN_TAG_NAME = "formBean";
    public static final String ACTION_OUTPUT_TAG_NAME = "actionOutput";
    public static final String CONDITIONAL_FORWARD_TAG_NAME = "conditionalForward";
    public static final String FACES_BACKING_TAG_NAME = "facesBacking";
    public static final String COMMAND_HANDLER_TAG_NAME = "commandHandler";
    public static final String RAISE_ACTION_TAG_NAME = "raiseAction";
    public static final String SHARED_FLOW_REF_TAG_NAME = "sharedFlowRef";
    public static final String SHARED_FLOW_FIELD_TAG_NAME = "sharedFlowField";
    public static final String PAGE_FLOW_FIELD_TAG_NAME = "pageFlowField";

    public static final String VALIDATE_REQUIRED_TAG_NAME = "validateRequired";
    public static final String VALIDATE_RANGE_TAG_NAME = "validateRange";
    public static final String VALIDATE_MIN_LENGTH_TAG_NAME = "validateMinLength";
    public static final String VALIDATE_MAX_LENGTH_TAG_NAME = "validateMaxLength";
    public static final String VALIDATE_CREDIT_CARD_TAG_NAME = "validateCreditCard";
    public static final String VALIDATE_EMAIL_TAG_NAME = "validateEmail";
    public static final String VALIDATE_MASK_TAG_NAME = "validateMask";
    public static final String VALIDATE_DATE_TAG_NAME = "validateDate";
    public static final String VALIDATE_TYPE_TAG_NAME = "validateType";
    public static final String VALIDATE_VALID_WHEN_TAG_NAME = "validateValidWhen";
    public static final String VALIDATE_CUSTOM_RULE_TAG_NAME = "validateCustomRule";
    public static final String MESSAGE_ARG_TAG_NAME = "messageArg";
    public static final String VALIDATE_CUSTOM_VARIABLE_TAG_NAME = "validateCustomVariable";

    public static final String BEGIN_ACTION_NAME = "begin";
    public static final String JPF_FILE_EXTENSION = "jpf";
    public static final String FACES_BACKING_FILE_EXTENSION = "jsfb";
    public static final String JAVA_FILE_EXTENSION = "java";
    public static final String JSP_FILE_EXTENSION = "jsp";
    public static final String XJSP_FILE_EXTENSION = "jspx";
    public static final String ACTION_EXTENSION = "do";
    public static final String JPF_FILE_EXTENSION_DOT = '.' + JPF_FILE_EXTENSION;
    public static final String ACTION_EXTENSION_DOT = '.' + ACTION_EXTENSION;
    public static final String JAVA_FILE_EXTENSION_DOT = '.' + JAVA_FILE_EXTENSION;
    public static final String FACES_BACKING_FILE_EXTENSION_DOT = '.' + FACES_BACKING_FILE_EXTENSION;
    public static final String SHARED_FLOW_FILE_EXTENSION = "jpfs";
    public static final String SHARED_FLOW_FILE_EXTENSION_DOT = '.' + SHARED_FLOW_FILE_EXTENSION;
    public static final String SHARED_FLOW_CLASSNAME = "SharedFlowController";
    public static final String PAGE_FLOW_CONFIG_FILE_BASENAME = "pageflow";
    public static final String SHARED_FLOW_CONFIG_FILE_BASENAME = "sharedflow";
    public static final String FLOWCONTROLLER_BASE_CLASS = PAGEFLOW_PACKAGE + ".FlowController";
    public static final String JPF_BASE_CLASS = PAGEFLOW_PACKAGE + ".PageFlowController";
    public static final String SHARED_FLOW_BASE_CLASS = PAGEFLOW_PACKAGE + ".SharedFlowController";
    public static final String FACES_BACKING_BEAN_CLASS = PAGEFLOW_PACKAGE + ".FacesBackingBean";
    public static final String FLOW_CONTROLLER_ACTION_CLASS = PAGEFLOW_INTERNAL_PACKAGE + ".FlowControllerAction";
    public static final String WEBINF_DIR_NAME = "WEB-INF";
    public static final String WEBINF_SRC_PATH = '/' + WEBINF_DIR_NAME + "/src";
    public static final String ANNOTATION_QUALIFIER = ANNOTATIONS_CLASSNAME + '.';
    public static final String ANNOTATION_INTERFACE_PREFIX = "ti.";
    public static final String NAVIGATE_TO_ENUM = "NavigateTo";
    public static final String DEFAULT_VALIDATION_MESSAGE_BUNDLE = PAGEFLOW_PACKAGE + ".validation.defaultMessages";
    public static final String DEFAULT_VALIDATION_MESSAGE_BUNDLE_NAME = "_defaultMsgs";
    public static final String DEFAULT_SIMPLE_ACTION_FORWARD_NAME = "_defaultForward";

    public static final String NESTED_ATTR = "nested";
    public static final String LONGLIVED_ATTR = "longLived";
    public static final String VALIDATOR_VERSION_ATTR = "validatorVersion";
    public static final String VALIDATOR_MERGE_ATTR = "validatorMerge";
    public static final String CUSTOM_VALIDATOR_CONFIGS_ATTR = "customValidatorConfigs";
    public static final String TILES_DEFINITIONS_CONFIGS_ATTR = "tilesDefinitionsConfigs";
    public static final String LOGIN_REQUIRED_ATTR = "loginRequired";
    public static final String ROLES_ALLOWED_ATTR = "rolesAllowed";
    public static final String NAME_ATTR = "name";
    public static final String PATH_ATTR = "path";
    public static final String TILES_DEFINITION_ATTR = "tilesDefinition";
    public static final String OUTPUT_FORM_BEAN_TYPE_ATTR = "outputFormBeanType";
    public static final String OUTPUT_FORM_BEAN_ATTR = "outputFormBean";
    public static final String NAVIGATE_TO_ATTR = "navigateTo";
    public static final String RETURN_ACTION_ATTR = "returnAction";
    public static final String MESSAGE_ATTR = "message";
    public static final String MESSAGE_KEY_ATTR = "messageKey";
    public static final String MESSAGE_ARGS_ATTR = "messageArgs";
    public static final String ARG_ATTR = "arg";
    public static final String ARG_KEY_ATTR = "argKey";
    public static final String POSITION_ATTR = "position";
    public static final String DISPLAY_NAME_ATTR = "displayName";
    public static final String DISPLAY_NAME_KEY_ATTR = "displayNameKey";
    public static final String METHOD_ATTR = "method";
    public static final String TYPE_ATTR = "type";
    public static final String REDIRECT_ATTR = "redirect";
    public static final String EXTERNAL_REDIRECT_ATTR = "externalRedirect";
    public static final String BUNDLE_PATH_ATTR = "bundlePath";
    public static final String BUNDLE_NAME_ATTR = "bundleName";
    public static final String ACTION_OUTPUTS_ATTR = "actionOutputs";
    public static final String REQUIRED_ATTR = "required";
    public static final String USE_FORM_BEAN_ATTR = "useFormBean";
    public static final String USE_FORM_BEAN_TYPE_ATTR = "useFormBeanType";
    public static final String READONLY_ATTR = "readOnly";
    public static final String INHERIT_LOCAL_PATHS_ATTR = "inheritLocalPaths";
    public static final String RESTORE_QUERY_STRING_ATTR = "restoreQueryString";
    public static final String VALUE_ATTR = "value";
    public static final String MESSAGE_BUNDLES_ATTR = "messageBundles";
    public static final String FORWARDS_ATTR = "forwards";
    public static final String CATCHES_ATTR = "handleExceptions";
    public static final String VALIDATION_ERROR_FORWARD_ATTR = "validationErrorForward";
    public static final String DO_VALIDATION_ATTR = "doValidation";
    public static final String LANGUAGE_ATTR = "language";
    public static final String COUNTRY_ATTR = "country";
    public static final String VARIANT_ATTR = "variant";
    public static final String VALIDATE_REQUIRED_ATTR = "validateRequired";
    public static final String VALIDATE_RANGE_ATTR = "validateRange";
    public static final String VALIDATE_MIN_LENGTH_ATTR = "validateMinLength";
    public static final String VALIDATE_MAX_LENGTH_ATTR = "validateMaxLength";
    public static final String VALIDATE_CREDIT_CARD_ATTR = "validateCreditCard";
    public static final String VALIDATE_EMAIL_ATTR = "validateEmail";
    public static final String VALIDATE_MASK_ATTR = "validateMask";
    public static final String VALIDATE_DATE_ATTR = "validateDate";
    public static final String VALIDATE_TYPE_ATTR = "validateType";
    public static final String VALIDATE_VALID_WHEN_ATTR = "validateValidWhen";
    public static final String VALIDATE_CUSTOM_ATTR = "validateCustomRules";
    public static final String VALIDATABLE_PROPERTIES_ATTR = "validatableProperties";
    public static final String MESSAGE_BUNDLE_ATTR = "messageBundle";
    public static final String APPLY_TO_UNHANDLED_LOCALES_ATTR = "applyToUnhandledLocales";
    public static final String VALIDATION_BEANS_ATTR = "validationBeans";
    public static final String ACTION_ATTR = "action";
    public static final String RAISE_ACTIONS_ATTR = "raiseActions";
    public static final String MULTIPART_HANDLER_ATTR = "multipartHandler";
    public static final String SHARED_FLOW_REFS_ATTR = "sharedFlowRefs";
    public static final String PREVENT_DOUBLE_SUBMIT_ATTR = "preventDoubleSubmit";
    public static final String FORWARD_REF_ATTR = "forwardRef";
    public static final String TYPE_HINT_ATTR = "typeHint";

    public static final String MIN_INT_ATTR = "minInt";
    public static final String MAX_INT_ATTR = "maxInt";
    public static final String MIN_FLOAT_ATTR = "minFloat";
    public static final String MAX_FLOAT_ATTR = "maxFloat";
    public static final String CHARS_ATTR = "chars";
    public static final String STRICT_ATTR = "strict";
    public static final String REGEX_ATTR = "regex";
    public static final String PATTERN_ATTR = "pattern";
    public static final String PROPERTY_NAME_ATTR = "propertyName";
    public static final String LOCALE_RULES_ATTR = "localeRules";
    public static final String VALIDATABLE_BEANS_ATTR = "validatableBeans";
    public static final String KEY_ATTR = "key";
    public static final String SIMPLE_ACTIONS_ATTR = "simpleActions";
    public static final String CONDITION_ATTR = "condition";
    public static final String CONDITIONAL_FORWARDS_ATTR = "conditionalForwards";
    public static final String RULE_ATTR = "rule";
    public static final String VARIABLES_ATTR = "variables";

    public static final String FORWARD_CLASS_NAME = PAGEFLOW_PACKAGE + ".Forward";
    public static final String PAGEFLOW_FORM_CLASS_NAME = PAGEFLOW_PACKAGE + ".FormData";
    public static final String BEA_XMLOBJECT_CLASS_NAME = "com.bea.xml.XmlObject";
    public static final String APACHE_XMLOBJECT_CLASS_NAME = "org.apache.xmlbeans.XmlObject";
    public static final String XML_FORM_CLASS_NAME = PAGEFLOW_PACKAGE + ".internal.XmlBeanActionForm";
    public static final String STRING_CLASS_NAME = String.class.getName();
    public static final String THROWABLE_CLASS_NAME = Throwable.class.getName();
    public static final String OBJECT_CLASS_NAME = Object.class.getName();
    public static final String SERIALIZABLE_CLASS_NAME = "java.io.Serializable";

    public static final String NAVIGATE_TO_CURRENT_PAGE_STR = "currentPage";
    public static final String NAVIGATE_TO_PREVIOUS_PAGE_STR = "previousPage";
    public static final String NAVIGATE_TO_PREVIOUS_ACTION_STR = "previousAction";

    public static final String MULTIPART_HANDLER_DISABLED_STR = "disabled";
    public static final String MULTIPART_HANDLER_MEMORY_STR = "memory";
    public static final String MULTIPART_HANDLER_DISK_STR = "disk";

    public static final String STRUTS_PACKAGE = "org.apache.struts";
    public static final String COMMONS_MULTIPART_HANDLER_CLASSNAME = STRUTS_PACKAGE + ".upload.CommonsMultipartRequestHandler";

    public static final String VALIDATOR_VERSION_ONE_ZERO_STR = "oneZero";
    public static final String VALIDATOR_VERSION_ONE_ONE_STR = "oneOne";

    public static final String ARRAY_TYPE_SUFFIX = "[]";
    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";

    public static final String RUNTIME_VERSION_ATTRIBUTE = "PageFlow-Runtime-Version";

    public static final String VERSION_8_SP2_STRING = "2";
    public static final String VERSION_9_0_STRING = "3";

    public static class ExtraInfoKeys {

        public static final Integer flowControllerInfo = new Integer(0);
        public static final Integer facesBackingInfo = new Integer(1);
        public static final Integer overlappingPageFlowFiles = new Integer(2);

        private Integer _val;

        public ExtraInfoKeys(Integer val) {
            _val = val;
        }

        public boolean equals(Object val) {
            return _val.equals(val);
        }

        public Integer getVal() {
            return _val;
        }
    }
}
