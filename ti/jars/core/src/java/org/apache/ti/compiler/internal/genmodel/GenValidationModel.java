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
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.MergedControllerAnnotation;
import org.apache.ti.compiler.internal.model.validation.ValidationModel;
import org.apache.ti.compiler.internal.model.validation.ValidatorConstants;
import org.apache.ti.compiler.internal.model.validation.ValidatorRule;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GenValidationModel
        extends ValidationModel
        implements JpfLanguageConstants, ValidatorConstants {

    private static final ValidatorRuleFactory VALIDATOR_RULE_FACTORY = new DefaultValidatorRuleFactory();

    private GenXWorkModuleConfigModel _strutsApp;
    private File _mergeFile;
    private AnnotationProcessorEnvironment _env;


    public GenValidationModel(ClassDeclaration jclass, GenXWorkModuleConfigModel strutsApp, AnnotationProcessorEnvironment env)
            throws FatalCompileTimeException {
        MergedControllerAnnotation mca = strutsApp.getFlowControllerInfo().getMergedControllerAnnotation();
        _strutsApp = strutsApp;
        setValidatorVersion(mca.getValidatorVersion());
        addRulesFromBeans(jclass);
        addRulesFromActions(jclass, mca);
        addRulesFromClass(mca);
        String mergeFileName = mca.getValidatorMerge();
        _mergeFile = strutsApp.getMergeFile(mergeFileName);
        _env = env;
    }

    private void addRulesFromBeans(ClassDeclaration jclass) {
        //
        // Read validation rules from public static inner classes (beans).
        //
        Collection innerTypes = CompilerUtils.getClassNestedTypes(jclass);

        for (Iterator ii = innerTypes.iterator(); ii.hasNext();) {
            TypeDeclaration innerType = (TypeDeclaration) ii.next();
            if (innerType instanceof ClassDeclaration
                    && innerType.hasModifier(Modifier.PUBLIC)
                    && innerType.hasModifier(Modifier.STATIC)) {
                addRulesFromBeanClass((ClassDeclaration) innerType);
            }
        }
    }

    private void addRulesFromBeanClass(ClassDeclaration beanClass) {
        Collection properties = CompilerUtils.getBeanProperties(beanClass, true);

        for (Iterator ii = properties.iterator(); ii.hasNext();) {
            CompilerUtils.BeanPropertyDeclaration property = (CompilerUtils.BeanPropertyDeclaration) ii.next();
            MethodDeclaration getter = property.getGetter();
            String propertyName = property.getPropertyName();

            if (getter != null) {
                //
                // Parse validation annotations on each getter.
                //
                AnnotationInstance[] annotations = getter.getAnnotationInstances();

                if (annotations != null) {
                    String formName = beanClass.getQualifiedName();

                    for (int i = 0; i < annotations.length; i++) {
                        AnnotationInstance ann = annotations[i];

                        if (CompilerUtils.isJpfAnnotation(ann, VALIDATABLE_PROPERTY_TAG_NAME)) {
                            //
                            // Add field rules from the Jpf.ValidationLocaleRules annotation.
                            //
                            addRulesFromAnnotation(ann, formName, propertyName);
                        }
                    }
                }
            }
        }
    }


    private void addRulesFromAnnotation(AnnotationInstance validationFieldAnn, String entityName, String propertyName) {
        //
        // Add rules from the FieldValidationRules annotations in the "localeRules" member.
        //
        Collection localeRulesAnnotations =
                CompilerUtils.getAnnotationArray(validationFieldAnn, LOCALE_RULES_ATTR, false);
        String displayName = CompilerUtils.getString(validationFieldAnn, DISPLAY_NAME_ATTR, true);
        String displayNameKey = CompilerUtils.getString(validationFieldAnn, DISPLAY_NAME_KEY_ATTR, true);
        RuleInfo ruleInfo = new RuleInfo(entityName, propertyName, displayName, displayNameKey);


        for (Iterator ii = localeRulesAnnotations.iterator(); ii.hasNext();) {
            AnnotationInstance ann = (AnnotationInstance) ii.next();
            addFieldRules(ann, ruleInfo, false);
        }

        addFieldRules(validationFieldAnn, ruleInfo, true);
    }

    private void addRulesFromActions(ClassDeclaration jclass, MergedControllerAnnotation mca) {
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(jclass, ACTION_TAG_NAME);

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            AnnotationInstance actionAnnotation = CompilerUtils.getAnnotation(method, ACTION_TAG_NAME);
            assert actionAnnotation != null;
            addRulesFromActionAnnotation(actionAnnotation, method.getSimpleName());

            ParameterDeclaration[] parameters = method.getParameters();
            if (parameters.length > 0) {
                TypeInstance type = parameters[0].getType();

                if (type instanceof ClassType) {
                    ClassDeclaration classDecl = ((ClassType) type).getClassTypeDeclaration();
                    if (classDecl.getDeclaringType() == null) addRulesFromBeanClass(classDecl);
                }
            }
        }


        Collection simpleActions = mca.getSimpleActions();

        if (simpleActions != null) {
            for (Iterator ii = simpleActions.iterator(); ii.hasNext();) {
                AnnotationInstance simpleAction = (AnnotationInstance) ii.next();
                String actionName = CompilerUtils.getString(simpleAction, NAME_ATTR, true);
                assert actionName != null;  //checker should enforce this.
                addRulesFromActionAnnotation(simpleAction, actionName);
            }
        }
    }

    private void addRulesFromActionAnnotation(AnnotationInstance actionAnnotation, String actionName) {
        Collection validatablePropertyAnnotations =
                CompilerUtils.getAnnotationArray(actionAnnotation, VALIDATABLE_PROPERTIES_ATTR, false);

        for (Iterator ii = validatablePropertyAnnotations.iterator(); ii.hasNext();) {
            AnnotationInstance validationFieldAnnotation = (AnnotationInstance) ii.next();
            String propertyName = CompilerUtils.getString(validationFieldAnnotation, PROPERTY_NAME_ATTR, true);
            assert propertyName != null;            // TODO: checker must enforce this
            assert ! propertyName.equals("");     // TODO: checker must enforce this

            //
            // Add the rules, and associate them with the action path ("/" + the action name).
            //
            String actionPath = '/' + actionName;   // Struts validator needs the slash in front
            addRulesFromAnnotation(validationFieldAnnotation, actionPath, propertyName);
        }
    }

    private void addRulesFromClass(MergedControllerAnnotation mca) {
        Collection validationBeanAnnotations = mca.getValidatableBeans();

        for (Iterator ii = validationBeanAnnotations.iterator(); ii.hasNext();) {
            AnnotationInstance validationBeanAnnotation = (AnnotationInstance) ii.next();
            DeclaredType beanType = CompilerUtils.getDeclaredType(validationBeanAnnotation, TYPE_ATTR, true);
            assert beanType != null;    // checker should enforce this

            Collection validationFieldAnnotations =
                    CompilerUtils.getAnnotationArray(validationBeanAnnotation, VALIDATABLE_PROPERTIES_ATTR, false);

            for (Iterator i2 = validationFieldAnnotations.iterator(); i2.hasNext();) {
                AnnotationInstance validationFieldAnnotation = (AnnotationInstance) i2.next();
                String propName = CompilerUtils.getString(validationFieldAnnotation, PROPERTY_NAME_ATTR, true);
                assert propName != null;            // checker should enforce this
                assert ! propName.equals("");     // TODO: get checker to enforce this

                //
                // Add the rules -- associate them with the classname of the bean type.
                //
                String formName = CompilerUtils.getDeclaration(beanType).getQualifiedName();
                addRulesFromAnnotation(validationFieldAnnotation, formName, propName);
            }
        }
    }

    /**
     * Add field rules from either a Jpf.ValidationField or a Jpf.ValidationLocaleRules annotation.
     */
    private void addFieldRules(AnnotationInstance rulesContainerAnnotation, RuleInfo ruleInfo,
                               boolean applyToAllLocales) {
        //
        // First parse the locale from the wrapper annotation.  This will apply to all rules inside.
        //
        Locale locale = null;

        if (! applyToAllLocales) {
            String language = CompilerUtils.getString(rulesContainerAnnotation, LANGUAGE_ATTR, true);

            //
            // If there's no language specified, then this rule will only apply for the default ruleset
            // (i.e., if there are explicit rules for the requested locale, this rule will not be run).
            //
            if (language != null) {
                String country = CompilerUtils.getString(rulesContainerAnnotation, COUNTRY_ATTR, true);
                String variant = CompilerUtils.getString(rulesContainerAnnotation, VARIANT_ATTR, true);

                language = language.trim();
                if (country != null) country = country.trim();
                if (variant != null) variant = variant.trim();

                if (country != null && variant != null) locale = new Locale(language, country, variant);
                else if (country != null) locale = new Locale(language, country);
                else
                    locale = new Locale(language);
            }
        }

        Map valuesPresent = rulesContainerAnnotation.getElementValues();

        for (Iterator ii = valuesPresent.entrySet().iterator(); ii.hasNext();) {
            Map.Entry entry = (Map.Entry) ii.next();
            AnnotationValue value = (AnnotationValue) entry.getValue();
            Object val = value.getValue();

            if (val instanceof AnnotationInstance) {
                addFieldRuleFromAnnotation(ruleInfo, (AnnotationInstance) val, locale, applyToAllLocales);
            } else if (val instanceof List) {
                List annotations = CompilerUtils.getAnnotationArray(value);

                for (Iterator i3 = annotations.iterator(); i3.hasNext();) {
                    AnnotationInstance i = (AnnotationInstance) i3.next();
                    addFieldRuleFromAnnotation(ruleInfo, i, locale, applyToAllLocales);
                }
            }
        }

        setEmpty(false);  // this ValidationModel is only "empty" if there are no rules.
    }

    private void addFieldRuleFromAnnotation(RuleInfo ruleInfo, AnnotationInstance annotation, Locale locale,
                                            boolean applyToAllLocales) {

        ValidatorRule rule = getFieldRule(ruleInfo.getEntityName(), ruleInfo.getFieldName(), annotation);

        if (rule != null) {
            if (applyToAllLocales) {
                addFieldRuleForAllLocales(ruleInfo, rule);
            } else {
                addFieldRule(ruleInfo, rule, locale);
            }
        }
    }

    private static ValidatorRule getFieldRule(String entityName, String propertyName, AnnotationInstance ruleAnnotation) {
        ValidatorRule rule = VALIDATOR_RULE_FACTORY.getFieldRule(entityName, propertyName, ruleAnnotation);

        if (rule != null) {
            //
            // message/message-key
            //
            rule.setMessage(CompilerUtils.getString(ruleAnnotation, MESSAGE_ATTR, true));
            rule.setMessageKey(CompilerUtils.getString(ruleAnnotation, MESSAGE_KEY_ATTR, true));
            rule.setBundle(CompilerUtils.getString(ruleAnnotation, BUNDLE_NAME_ATTR, true));
            if (rule.getMessage() != null) assert rule.getMessageKey() == null;   // TODO: checker should enforce

            //
            // args
            //
            addMessageArgs(rule, ruleAnnotation);
        }

        return rule;
    }

    protected static void addMessageArgs(ValidatorRule rule, AnnotationInstance annotation) {
        List messageArgs =
                CompilerUtils.getAnnotationArray(annotation, MESSAGE_ARGS_ATTR, true);

        if (messageArgs != null) {
            int inferredPosition = 0;
            for (Iterator ii = messageArgs.iterator(); ii.hasNext();) {
                AnnotationInstance ann = (AnnotationInstance) ii.next();
                String arg = CompilerUtils.getString(ann, ARG_ATTR, true);
                String bundle = CompilerUtils.getString(ann, BUNDLE_NAME_ATTR, true);
                Integer position = CompilerUtils.getInteger(ann, POSITION_ATTR, true);

                if (position == null) {
                    position = new Integer(inferredPosition);
                }

                if (arg != null) {
                    rule.setArg(arg, false, bundle, position);
                } else {
                    String argKey = CompilerUtils.getString(ann, ARG_KEY_ATTR, true);
                    if (argKey != null) rule.setArg(argKey, true, bundle, position);
                }

                inferredPosition++;
            }
        }
    }

    protected String getHeaderComment(File mergeFile)
            throws FatalCompileTimeException {
        return _strutsApp.getHeaderComment(mergeFile);
    }

    public void writeToFile()
            throws FileNotFoundException, XmlException, IOException, FatalCompileTimeException {
        String outputFilePath = getOutputFileURI();
        File outputFile = new File(outputFilePath);
        PrintWriter writer = _env.getFiler().createTextFile(outputFile);
        try {
            writeXml(writer, _mergeFile);
        }
        finally {
            writer.close();
        }
    }

    public String getOutputFileURI() {
        return _strutsApp.getOutputFileURI(_strutsApp.getValidationFilePrefix());
    }
}
