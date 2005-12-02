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
package org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration;

import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.type.AnnotationType;
import org.apache.ti.compiler.internal.typesystem.type.ArrayType;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.ti.compiler.xdoclet.XDocletCompilerUtils;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.env.SourcePositionImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.type.AnnotationTypeImpl;
import xjavadoc.XDoc;
import xjavadoc.XProgramElement;
import xjavadoc.XTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class DeclarationImpl
        extends DelegatingImpl
        implements Declaration, JpfLanguageConstants {

    private static final String VALIDATION_ERROR_FORWARD_TAG_NAME = "validationErrorForward";
    private static final HashMap MODIFIERS = new HashMap();
    private static AnnotationInterfaceParser _annotationInterfaceParser =
            new AnnotationInterfaceParser(ANNOTATION_INTERFACE_PREFIX);

    static {
        MODIFIERS.put("abstract", Modifier.ABSTRACT);
        MODIFIERS.put("private", Modifier.PRIVATE);
        MODIFIERS.put("protected", Modifier.PROTECTED);
        MODIFIERS.put("public", Modifier.PUBLIC);
        MODIFIERS.put("static", Modifier.STATIC);
        MODIFIERS.put("transient", Modifier.TRANSIENT);
        MODIFIERS.put("final", Modifier.FINAL);
        MODIFIERS.put("synchronized", Modifier.SYNCHRONIZED);
        MODIFIERS.put("native", Modifier.NATIVE);

        _annotationInterfaceParser.addMemberArrayAnnotation(FORWARD_TAG_NAME, FORWARDS_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(CATCH_TAG_NAME, CATCHES_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(SIMPLE_ACTION_TAG_NAME, SIMPLE_ACTIONS_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(ACTION_OUTPUT_TAG_NAME, ACTION_OUTPUTS_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(CONDITIONAL_FORWARD_TAG_NAME, CONDITIONAL_FORWARDS_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(MESSAGE_BUNDLE_TAG_NAME, MESSAGE_BUNDLES_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(MESSAGE_ARG_TAG_NAME, MESSAGE_ARGS_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(VALIDATE_CUSTOM_RULE_TAG_NAME, VALIDATE_CUSTOM_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(VALIDATE_CUSTOM_VARIABLE_TAG_NAME, VARIABLES_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(VALIDATION_LOCALE_RULES_TAG_NAME, LOCALE_RULES_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(VALIDATABLE_PROPERTY_TAG_NAME, VALIDATABLE_PROPERTIES_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(VALIDATABLE_BEAN_TAG_NAME, VALIDATABLE_BEANS_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(RAISE_ACTION_TAG_NAME, RAISE_ACTIONS_ATTR);
        _annotationInterfaceParser.addMemberArrayAnnotation(SHARED_FLOW_REF_TAG_NAME, SHARED_FLOW_REFS_ATTR);

        _annotationInterfaceParser.addMemberAnnotation(VALIDATION_ERROR_FORWARD_TAG_NAME, VALIDATION_ERROR_FORWARD_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_REQUIRED_TAG_NAME, VALIDATE_REQUIRED_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_MIN_LENGTH_TAG_NAME, VALIDATE_MIN_LENGTH_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_MAX_LENGTH_TAG_NAME, VALIDATE_MAX_LENGTH_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_MASK_TAG_NAME, VALIDATE_MASK_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_TYPE_TAG_NAME, VALIDATE_TYPE_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_DATE_TAG_NAME, VALIDATE_DATE_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_RANGE_TAG_NAME, VALIDATE_RANGE_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_CREDIT_CARD_TAG_NAME, VALIDATE_CREDIT_CARD_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_EMAIL_TAG_NAME, VALIDATE_EMAIL_ATTR);
        _annotationInterfaceParser.addMemberAnnotation(VALIDATE_VALID_WHEN_TAG_NAME, VALIDATE_VALID_WHEN_ATTR);

        _annotationInterfaceParser.addMemberOrTopLevelAnnotation(VALIDATABLE_PROPERTY_TAG_NAME);

        //
        // Special case:
        //     validationErrorForward=@Jpf.Forward(...)
        // looks like this in our world:
        //     @Jpf.ValidationErrorForward(...)
        // Here we dynamically create a new ValidationErrorForward annotation based on Forward.
        //
        AnnotationTypeDeclarationImpl forwardAnn = (AnnotationTypeDeclarationImpl)
                _annotationInterfaceParser.getAnnotationTypeDeclImpl(ANNOTATION_INTERFACE_PREFIX + FORWARD_TAG_NAME);
        AnnotationTypeDeclarationImpl validationErrorForwardAnn =
                new AnnotationTypeDeclarationImpl(forwardAnn, VALIDATION_ERROR_FORWARD_TAG_NAME, ANNOTATION_QUALIFIER);
        _annotationInterfaceParser.addAnnotation(VALIDATION_ERROR_FORWARD_TAG_NAME, validationErrorForwardAnn);
    }

    private HashSet _modifiers;
    private AnnotationInstance[] _annotations;

    public DeclarationImpl(XProgramElement delegate) {
        super(delegate);
        ArrayList annotations = getAnnotations(delegate);
        _annotations = (AnnotationInstance[]) annotations.toArray(new AnnotationInstance[ annotations.size() ]);
    }

    public static AnnotationTypeDeclaration[] getAllAnnotations() {
        return _annotationInterfaceParser.getAllAnnotations();
    }

    public String getDocComment() {
        return getDelegateXProgramElement().getDoc().getCommentText();
    }

    public AnnotationInstance[] getAnnotationInstances() {
        return _annotations;
    }

    public Set getModifiers() {
        if (_modifiers == null) {
            HashSet modifiers = new HashSet();
            StringTokenizer tok = new StringTokenizer(getDelegateXProgramElement().getModifiers());

            while (tok.hasMoreTokens()) {
                String modifierString = tok.nextToken();
                Modifier modifier = (Modifier) MODIFIERS.get(modifierString);
                assert modifier != null : "unrecognized modifier: " + modifierString;
                modifiers.add(modifier);
            }

            _modifiers = modifiers;
        }

        return _modifiers;
    }

    public String getSimpleName() {
        String name = getDelegateXProgramElement().getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot != -1 ? name.substring(lastDot + 1) : name;
    }

    public SourcePosition getPosition() {
        return SourcePositionImpl.get(getDelegateXProgramElement());
    }

    public boolean hasModifier(Modifier modifier) {
        return getModifiers().contains(modifier);
    }

    protected XProgramElement getDelegateXProgramElement() {
        return (XProgramElement) super.getDelegate();
    }

    /**
     * Get all the annotations for the given element.
     *
     * @param element the element (class, method, etc.) to examine
     * @return an ArrayList of AnnotationInstances.
     */
    private static ArrayList getAnnotations(XProgramElement element) {
        XDoc doc = element.getDoc();
        ArrayList annotations = new ArrayList();
        List tags = doc != null ? doc.getTags() : null;
        ArrayList parentAnnotations = new ArrayList();  // hierarchy of parent annotations, e.g., Action -> Forward -> ...

        if (tags == null) return annotations;

        for (Iterator i = tags.iterator(); i.hasNext();) {
            XTag tag = (XTag) i.next();
            String tagName = tag.getName();
            AnnotationTypeDeclaration decl = _annotationInterfaceParser.getAnnotationTypeDecl(tagName);

            if (decl != null) {
                AnnotationType type = new AnnotationTypeImpl(decl);
                Collection attrNames = tag.getAttributeNames();
                HashMap elementValues = new HashMap();

                for (Iterator j = attrNames.iterator(); j.hasNext();) {
                    String attrName = (String) j.next();
                    AnnotationTypeElementDeclaration memberDecl = decl.getMember(attrName);
                    SourcePositionImpl pos = SourcePositionImpl.get(tag, attrName, element);
                    Object val = parseValue(memberDecl, tag.getAttributeValue(attrName), pos);
                    AnnotationValue value = new AnnotationValueImpl(val, pos, memberDecl);
                    elementValues.put(memberDecl, value);
                }

                AnnotationInstanceImpl ann = new AnnotationInstanceImpl(tag, element, type, elementValues);

                String memberName = _annotationInterfaceParser.getParentMemberArrayName(tagName);

                if (memberName != null) {
                    if (! addAnnotationToParent(annotations, ann, memberName, true, parentAnnotations)) {
                        annotations.add(ann);
                    }
                } else if ((memberName = _annotationInterfaceParser.getParentMemberName(tagName)) != null) {
                    if (! addAnnotationToParent(annotations, ann, memberName, false, parentAnnotations)) {
                        annotations.add(ann);
                    }
                } else {
                    annotations.add(ann);
                }

                for (int j = 0, len = parentAnnotations.size(); j < len; ++j) {
                    AnnotationInstanceImpl parentAnn = (AnnotationInstanceImpl) parentAnnotations.get(j);

                    if (parentAnn.getAnnotationType().equals(ann.getAnnotationType())) {
                        // We found an annotation of this type in the hierarchy of parent annotations.
                        // Replace it and blow away everything after it.
                        for (int k = j; k < len; ++k) {
                            parentAnnotations.remove(j);
                        }
                        break;
                    }
                }

                parentAnnotations.add(ann);
            }
        }

        return annotations;
    }

    private static boolean addAnnotationToParent(ArrayList annotations, AnnotationInstanceImpl ann, String memberArrayName,
                                                 boolean memberIsArray, ArrayList parentAnnotations) {
        if (annotations.size() == 0) {
            String annName = ann.getDelegateXTag().getName();
            if (_annotationInterfaceParser.isMemberOrTopLevelAnnotation(annName)) {
                return false;
            }

            XDocletCompilerUtils.addError(ann.getPosition(), "error.no-parent-annotation",
                    new String[]{ann.getAnnotationType().getAnnotationTypeDeclaration().getQualifiedName()});
        } else {
            AnnotationInstanceImpl foundTheRightParent = null;

            //
            // Look through the hierarchy of parent annotations, for the first one that can accept the given annotation
            // as a child.
            //
            for (int i = parentAnnotations.size() - 1; i >= 0; --i) {
                AnnotationInstanceImpl parentAnnotation = (AnnotationInstanceImpl) parentAnnotations.get(i);
                AnnotationTypeElementDeclaration elementDecl =
                        parentAnnotation.getAnnotationType().getAnnotationTypeDeclaration().getMember(memberArrayName);

                if (elementDecl != null) {
                    foundTheRightParent = parentAnnotation;

                    //
                    // Blow away everything past the found parent annotation in the hierarchy.
                    //
                    for (int j = i + 1, len = parentAnnotations.size(); j < len; ++j) {
                        parentAnnotations.remove(i + 1);
                    }

                    break;
                }
            }

            if (foundTheRightParent != null) {
                foundTheRightParent.addElementValue(memberArrayName, memberIsArray, ann, ann.getPosition());
            } else {
                String annName = ann.getDelegateXTag().getName();
                if (_annotationInterfaceParser.isMemberOrTopLevelAnnotation(annName)) {
                    return false;
                }

                XDocletCompilerUtils.addError(ann.getPosition(), "error.no-parent-annotation",
                        new String[]{ann.getAnnotationType().getAnnotationTypeDeclaration().getQualifiedName()});
            }
        }

        return true;
    }

    private static Object parseValue(AnnotationTypeElementDeclaration memberDecl, String strValue, SourcePositionImpl pos) {
        TypeInstance type = memberDecl.getReturnType();

        if (type instanceof ClassType) {
            ClassType classType = (ClassType) type;
            String typeName = classType.getClassTypeDeclaration().getQualifiedName();

            if (typeName.equals("java.lang.String")) {
                return strValue;
            } else if (typeName.equals("java.lang.Class")) {
                TypeInstance retVal = XDocletCompilerUtils.resolveType(strValue, false, pos.getOuterClass());

                if (retVal == null) {
                    XDocletCompilerUtils.addError(pos, "error.unknown-class",
                            new String[]{strValue, memberDecl.getSimpleName()});
                }

                return XDocletCompilerUtils.resolveType(strValue, true, pos.getOuterClass());
            } else {
                assert false : "unexpected type in annotation declaration: " + typeName;
            }
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            TypeInstance componentType = arrayType.getComponentType();

            // We only handle an array of strings -- nothing else at this point.
            assert componentType instanceof DeclaredType : componentType.getClass().getName();
            assert ((DeclaredType) componentType).getDeclaration().getQualifiedName().equals(String.class.getName())
                    : ((DeclaredType) componentType).getDeclaration().getQualifiedName();
            StringTokenizer tok = new StringTokenizer(strValue, ",");
            ArrayList arrayValues = new ArrayList();
            while (tok.hasMoreTokens()) {
                arrayValues.add(new AnnotationValueImpl(tok.nextToken().trim(), pos, memberDecl));
            }
            return arrayValues;
        }

        assert type instanceof PrimitiveType : type.getClass().getName();
        switch (((PrimitiveType) type).getKind().asInt()) {
            case PrimitiveType.Kind.INT_BOOLEAN:
                return Boolean.valueOf(strValue);

            case PrimitiveType.Kind.INT_BYTE:
                return new Byte(strValue);

            case PrimitiveType.Kind.INT_SHORT:
                return new Short(strValue);

            case PrimitiveType.Kind.INT_INT:
                return new Integer(strValue);

            case PrimitiveType.Kind.INT_LONG:
                return new Long(strValue);

            case PrimitiveType.Kind.INT_FLOAT:
                return new Float(strValue);

            case PrimitiveType.Kind.INT_DOUBLE:
                return new Double(strValue);
        }

        assert false : "unrecognized type: " + type.toString();
        return null;
    }
}
