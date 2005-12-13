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

import org.apache.ti.compiler.internal.typesystem.declaration.*;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.env.Filer;
import org.apache.ti.compiler.internal.typesystem.env.Messager;
import org.apache.ti.compiler.internal.typesystem.type.ArrayType;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.InterfaceType;
import org.apache.ti.compiler.internal.typesystem.type.ReferenceType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.ti.compiler.internal.typesystem.type.TypeVariable;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.ti.compiler.internal.grammar.ActionGrammar;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CompilerUtils
        implements JpfLanguageConstants {

    private static final String ERROR_STRING = "<error>";
    private static final TypeDeclaration ERROR_TYPE_DECLARATION = new ErrorTypeDeclaration();

    public static boolean isJpfAnnotation(AnnotationInstance annotation, String unqualifiedName) {
        String annotationName = getDeclaration(annotation.getAnnotationType()).getQualifiedName();
        return annotationName.equals(ANNOTATION_QUALIFIER + unqualifiedName);
    }

    public static AnnotationInstance getAnnotation(ClassDeclaration decl, String unqualifiedName, boolean inherited) {
        if (! inherited) return getAnnotation(decl, unqualifiedName);

        do {
            AnnotationInstance ann = getAnnotation(decl, unqualifiedName);
            if (ann != null) return ann;
            ClassType superType = decl.getSuperclass();
            TypeDeclaration superTypeDecl = getDeclaration(superType);
            decl = superTypeDecl instanceof ClassDeclaration ? (ClassDeclaration) superTypeDecl : null;
        } while (decl != null);

        return null;
    }

    public static AnnotationInstance getAnnotation(Declaration element, String unqualifiedName) {
        return getAnnotationFullyQualified(element, ANNOTATION_QUALIFIER + unqualifiedName);
    }

    public static AnnotationInstance getAnnotationFullyQualified(Declaration element, String fullyQualifiedName) {
        AnnotationInstance[] annotations = element.getAnnotationInstances();

        for (int ii = 0; ii < annotations.length; ii++) {
            AnnotationInstance i = annotations[ii];
            String iName = getDeclaration(i.getAnnotationType()).getQualifiedName();
            if (fullyQualifiedName.equals(iName)) return i;
        }

        return null;
    }

    public static AnnotationValue getAnnotationValue(Declaration element, String annotationName, String valueName) {
        AnnotationInstance ann = getAnnotation(element, annotationName);
        return ann != null ? getAnnotationValue(ann, valueName, true) : null;
    }

    /**
     * If the given annotation exists, assert that the given member is not null</code>, and return it; otherwise,
     * if the given annotation does not exist, return null</code>.
     */
    private static AnnotationValue assertAnnotationValue(Declaration element, String annotationName, String valueName,
                                                         boolean defaultIsNull) {
        AnnotationInstance ann = getAnnotation(element, annotationName);

        if (ann == null) {
            return null;
        } else {
            return getAnnotationValue(ann, valueName, defaultIsNull);
        }
    }

    public static String getStringValue(Declaration element, String annotationName, String memberName,
                                        boolean defaultIsNull) {
        return (String) getValue(element, annotationName, memberName, defaultIsNull);
    }

    public static AnnotationValue getAnnotationValue(AnnotationInstance annotation, String memberName,
                                                     boolean defaultIsNull) {
        Map valuesPresent = annotation.getElementValues();

        for (Iterator ii = valuesPresent.entrySet().iterator(); ii.hasNext();) {
            Map.Entry i = (Map.Entry) ii.next();
            if (memberName.equals(((AnnotationTypeElementDeclaration) i.getKey()).getSimpleName())) {
                return (AnnotationValue) i.getValue();
            }
        }

        //
        // We didn't find it.  If necessary, look for the default value.
        //
        if (defaultIsNull) return null;

        AnnotationTypeDeclaration typeDecl = annotation.getAnnotationType().getAnnotationTypeDeclaration();
        if (typeDecl == null) return null;    // type declaration is null in the case of error type

        AnnotationTypeElementDeclaration[] members = typeDecl.getAnnotationMembers();
        for (int i = 0; i < members.length; i++) {
            AnnotationTypeElementDeclaration member = members[i];
            if (memberName.equals(member.getSimpleName())) return member.getDefaultValue();

        }

        assert false : "Member " + memberName + " not found on annotation type " + getQualifiedName(annotation);
        return null;
    }

    public static List getStringArrayValue(Declaration element, String annotationName, String memberName,
                                           boolean defaultIsNull) {
        AnnotationValue value = assertAnnotationValue(element, annotationName, memberName, defaultIsNull);
        if (value == null) return null;
        ArrayList ret = new ArrayList();
        getValues(value, ret, false);
        return ret;
    }

    public static Boolean getBooleanValue(Declaration element, String annotationName, String memberName,
                                          boolean defaultIsNull) {
        AnnotationValue value = assertAnnotationValue(element, annotationName, memberName, defaultIsNull);
        return value != null ? (Boolean) value.getValue() : null;
    }

    public static String getString(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        return value != null ? (String) value.getValue() : (defaultIsNull ? null : "");
    }

    public static TypeInstance getTypeInstance(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        if (value == null) return null;
        Object typeInstance = value.getValue();
        if (isErrorString(typeInstance)) return new ErrorTypeInstance();
        return (TypeInstance) typeInstance;
    }

    public static String getEnumFieldName(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        return value != null ? getEnumFieldName(value) : null;
    }

    public static List getStringArray(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        if (value == null) return null;
        ArrayList ret = new ArrayList();
        getValues(value, ret, false);
        return ret;
    }

    public static DeclaredType getDeclaredType(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        return (DeclaredType) getReferenceType(annotation, memberName, defaultIsNull);
    }

    public static ReferenceType getReferenceType(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);

        // If the type is the string "<error>", it means that there is already an error related to the type itself.
        if (value != null && isErrorString(value.getValue())) return null;

        return value != null ? (ReferenceType) value.getValue() : null;
    }

    public static Integer getInteger(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        if (value == null) return defaultIsNull ? null : new Integer(0);
        Object result = value.getValue();

        if (result instanceof String) {
            assert isErrorString(result) : result;
            return new Integer(0);
        }

        return (Integer) value.getValue();
    }

    public static boolean isErrorString(Object str) {
        return ERROR_STRING.equals(str);
    }

    public static Long getLong(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        if (value == null) return defaultIsNull ? null : new Long(0);
        Object result = value.getValue();

        if (result instanceof String) {
            assert result.equals(ERROR_STRING) : result;
            return new Long(0);
        }

        return (Long) value.getValue();
    }

    public static Float getFloat(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        if (value == null) return defaultIsNull ? null : new Float(0);
        Object result = value.getValue();

        if (result instanceof String) {
            assert result.equals(ERROR_STRING) : result;
            return new Float(0);
        }

        return (Float) value.getValue();
    }

    public static Double getDouble(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        if (value == null) return defaultIsNull ? null : new Double(0);
        Object result = value.getValue();

        if (result instanceof String) {
            assert result.equals(ERROR_STRING) : result;
            return new Double(0);
        }

        return (Double) value.getValue();
    }

    public static Boolean getBoolean(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        if (value == null) return defaultIsNull ? null : Boolean.FALSE;
        Object result = value.getValue();

        if (result instanceof String) {
            assert result.equals(ERROR_STRING) : result;
            return Boolean.FALSE;
        }

        return (Boolean) value.getValue();
    }

    public static Object getValue(Declaration element, String annotationName, String memberName, boolean defaultIsNull) {
        AnnotationValue value = assertAnnotationValue(element, annotationName, memberName, defaultIsNull);
        return value != null ? value.getValue() : null;
    }

    public static List getAnnotationArrayValue(Declaration element, String annotationName,
                                               String memberName, boolean defaultIsNull) {
        AnnotationValue value = assertAnnotationValue(element, annotationName, memberName, defaultIsNull);
        if (value == null) return null;
        ArrayList ret = new ArrayList();
        getValues(value, ret, true);
        return ret;
    }

    public static List getAnnotationArray(AnnotationInstance annotation, String memberName,
                                          boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        return getAnnotationArray(value);
    }

    public static List getAnnotationArray(AnnotationValue value) {
        if (value == null) return null;
        ArrayList ret = new ArrayList();
        getValues(value, ret, true);
        return ret;
    }

    private static void getValues(AnnotationValue arrayValue, List translatedValues, boolean weedOutErrorType) {
        List values = (List) arrayValue.getValue();
        for (Iterator ii = values.iterator(); ii.hasNext();) {
            Object i = ii.next();
            Object value = i instanceof AnnotationValue ? ((AnnotationValue) i).getValue() : i;
            if (! weedOutErrorType || ! isErrorString(value)) translatedValues.add(value);
        }
    }

    public static String getQualifiedName(AnnotationInstance annotation) {
        return getDeclaration(annotation.getAnnotationType()).getQualifiedName();
    }

    public static String getSimpleName(AnnotationInstance annotation) {
        return getDeclaration(annotation.getAnnotationType()).getSimpleName();
    }

    public static AnnotationInstance getAnnotation(AnnotationInstance annotation, String memberName, boolean defaultIsNull) {
        AnnotationValue value = getAnnotationValue(annotation, memberName, defaultIsNull);
        return value != null ? (AnnotationInstance) value.getValue() : null;
    }

    public static MethodDeclaration getClassMethod(TypeDeclaration jclass, String methodName, String desiredAnnotation) {
        return getClassMethod(jclass, methodName, desiredAnnotation, false);
    }

    private static MethodDeclaration getClassMethod(TypeDeclaration type, String methodName, String desiredAnnotation,
                                                    boolean onlyPublicOrProtected) {
        if (! (type instanceof ClassDeclaration)) return null;

        ClassDeclaration jclass = (ClassDeclaration) type;
        MethodDeclaration[] methods = jclass.getMethods();

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            if (! onlyPublicOrProtected || method.hasModifier(Modifier.PROTECTED)
                    || method.hasModifier(Modifier.PUBLIC)) {
                if (methodName.equals(method.getSimpleName())
                        && (desiredAnnotation == null || getAnnotation(method, desiredAnnotation) != null)) {
                    return method;
                }
            }
        }

        ClassType superclass = jclass.getSuperclass();

        if (superclass != null) {
            return getClassMethod(getDeclaration(superclass), methodName, desiredAnnotation, true);
        }

        return null;
    }

    public static FieldDeclaration getClassField(TypeDeclaration jclass, String fieldName, String desiredAnnotation) {
        return getClassField(jclass, fieldName, desiredAnnotation, false);
    }

    private static FieldDeclaration getClassField(TypeDeclaration type, String fieldName, String desiredAnnotation,
                                                  boolean onlyPublicOrProtected) {
        if (! (type instanceof ClassDeclaration)) return null;

        ClassDeclaration jclass = (ClassDeclaration) type;
        FieldDeclaration[] fields = jclass.getFields();

        for (int i = 0; i < fields.length; i++) {
            FieldDeclaration field = fields[i];
            if (! onlyPublicOrProtected || field.hasModifier(Modifier.PROTECTED)
                    || field.hasModifier(Modifier.PUBLIC)) {
                if (fieldName.equals(field.getSimpleName())
                        && (desiredAnnotation == null || getAnnotation(field, desiredAnnotation) != null)) {
                    return field;
                }
            }
        }

        ClassType superclass = jclass.getSuperclass();

        if (superclass != null) {
            return getClassField(getDeclaration(superclass), fieldName, desiredAnnotation, true);
        }

        return null;
    }

    public static MethodDeclaration[] getClassMethods(TypeDeclaration jclass, String desiredAnnotation) {
        Collection results = new ArrayList();
        getClassMethods(jclass, desiredAnnotation, false, results);
        return (MethodDeclaration[]) results.toArray(new MethodDeclaration[ results.size() ]);
    }

    private static void getClassMethods(TypeDeclaration type, String desiredAnnotation, boolean onlyPublicOrPrivate,
                                        Collection results) {
        if (! (type instanceof ClassDeclaration)) return;

        ClassDeclaration jclass = (ClassDeclaration) type;
        MethodDeclaration[] methods = jclass.getMethods();

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];

            if (! onlyPublicOrPrivate || method.hasModifier(Modifier.PROTECTED)
                    || method.hasModifier(Modifier.PUBLIC)) {
                if (desiredAnnotation == null || getAnnotation(method, desiredAnnotation) != null) {
                    boolean isDuplicate = false;

                    //
                    // Make sure we're not adding a duplicate method -- one that was already overridden.
                    //
                    if (onlyPublicOrPrivate) {
                        ParameterDeclaration[] methodParams = method.getParameters();

                        for (Iterator j = results.iterator(); j.hasNext();) {
                            MethodDeclaration existingMethod = (MethodDeclaration) j.next();

                            if (existingMethod.getSimpleName().equals(method.getSimpleName())) {
                                ParameterDeclaration[] existingMethodParams = existingMethod.getParameters();

                                if (existingMethodParams.length == methodParams.length) {
                                    isDuplicate = true;

                                    for (int k = 0; k < existingMethodParams.length; ++k) {
                                        ParameterDeclaration existingMethodParam = existingMethodParams[k];
                                        ParameterDeclaration methodParam = methodParams[k];

                                        if (! existingMethodParam.getType().equals(methodParam.getType())) {
                                            isDuplicate = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (! isDuplicate) results.add(method);
                }
            }
        }

        ClassType superclass = jclass.getSuperclass();

        if (superclass != null && ! getDeclaration(superclass).getQualifiedName().startsWith("java.lang.")) {
            getClassMethods(getDeclaration(superclass), desiredAnnotation, true, results);
        }
    }

    public static Collection getClassFields(TypeDeclaration jclass) {
        Collection results = new ArrayList();
        getClassFields(jclass, false, results);
        return results;
    }

    private static void getClassFields(TypeDeclaration type, boolean onlyPublicOrPrivate,
                                       Collection results) {
        if (! (type instanceof ClassDeclaration)) return;

        ClassDeclaration jclass = (ClassDeclaration) type;
        FieldDeclaration[] fields = jclass.getFields();

        for (int i = 0; i < fields.length; i++) {
            FieldDeclaration field = fields[i];
            if (! onlyPublicOrPrivate || field.hasModifier(Modifier.PROTECTED) || field.hasModifier(Modifier.PUBLIC)) {
                results.add(field);
            }
        }

        ClassType superclass = jclass.getSuperclass();
        if (superclass != null) getClassFields(getDeclaration(superclass), true, results);
    }

    public static Collection getClassNestedTypes(TypeDeclaration jclass) {
        Collection results = new ArrayList();
        getClassNestedTypes(jclass, false, results);
        return results;
    }

    private static void getClassNestedTypes(TypeDeclaration type, boolean onlyPublicOrPrivate,
                                            Collection results) {
        if (! (type instanceof ClassDeclaration)) return;

        ClassDeclaration jclass = (ClassDeclaration) type;
        TypeDeclaration[] nestedTypes = jclass.getNestedTypes();

        for (int i = 0; i < nestedTypes.length; i++) {
            TypeDeclaration nestedType = nestedTypes[i];
            if (! onlyPublicOrPrivate || nestedType.hasModifier(Modifier.PROTECTED)
                    || nestedType.hasModifier(Modifier.PUBLIC)) {
                results.add(nestedType);
            }
        }

        ClassType superclass = jclass.getSuperclass();
        if (superclass != null) getClassNestedTypes(getDeclaration(superclass), true, results);
    }

    /**
     * Get a Class.forName-able string for the given type signature.
     *
     * @todo make this pluggable
     */
    public static String getFormClassName(TypeDeclaration jclass, AnnotationProcessorEnvironment env) {
        if (isAssignableFrom(APACHE_XMLOBJECT_CLASS_NAME, jclass, env)) {
            return XML_FORM_CLASS_NAME;
        } else {
            return getLoadableName(jclass);
        }
    }

    public static String getFormClassName(DeclaredType jclass, AnnotationProcessorEnvironment env) {
        return getFormClassName(getDeclaration(jclass), env);
    }

    public static boolean isAbsoluteURL(String path) {
        try {
            return new URI(path).getScheme() != null;
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isAssignableFrom(String className, TypeInstance type, AnnotationProcessorEnvironment env) {
        if (! (type instanceof DeclaredType)) return false;
        return isAssignableFrom(className, getDeclaration((DeclaredType) type), env);
    }

    public static boolean isAssignableFrom(TypeDeclaration base, TypeDeclaration typeDecl) {
        if (base != null && typeDecl != null) {
            if (typesAreEqual(typeDecl, base)) return true;

            if (typeDecl instanceof ClassDeclaration) {
                ClassType superclass = ((ClassDeclaration) typeDecl).getSuperclass();
                if (superclass != null && isAssignableFrom(base, getDeclaration(superclass))) return true;
            }

            InterfaceType[] superInterfaces = typeDecl.getSuperinterfaces();
            for (int i = 0; i < superInterfaces.length; i++) {
                InterfaceType superInterface = superInterfaces[i];
                if (isAssignableFrom(base, getDeclaration(superInterface))) return true;
            }
        }

        return false;
    }

    public static boolean isAssignableFrom(TypeInstance base, TypeDeclaration cl) {
        if (! (base instanceof DeclaredType)) return false;
        return isAssignableFrom(getDeclaration((DeclaredType) base), cl);
    }

    public static boolean isAssignableFrom(TypeDeclaration base, TypeInstance cl) {
        if (! (cl instanceof DeclaredType)) return false;
        return isAssignableFrom(base, getDeclaration((DeclaredType) cl));
    }

    public static boolean isAssignableFrom(String className, TypeDeclaration cl, AnnotationProcessorEnvironment env) {
        TypeDeclaration base = env.getTypeDeclaration(className);
        return isAssignableFrom(base, cl);
    }

    public static boolean isOfClass(TypeInstance type, String className, AnnotationProcessorEnvironment env) {
        if (! (type instanceof DeclaredType)) return false;
        return typesAreEqual(getDeclaration((DeclaredType) type), env.getTypeDeclaration(className));
    }

    public static boolean typesAreEqual(TypeDeclaration t1, TypeDeclaration t2) {
        assert t1 != null;
        if (t2 == null) return false;
        return t1.getQualifiedName().equals(t2.getQualifiedName());
    }

    public static TypeDeclaration getOuterClass(MemberDeclaration classMember) {
        return classMember instanceof ClassDeclaration
                ? (ClassDeclaration) classMember
                : classMember.getDeclaringType();
    }

    public static TypeDeclaration getOutermostClass(MemberDeclaration classMember) {
        TypeDeclaration containingClass;
        while ((containingClass = classMember.getDeclaringType()) != null) {
            classMember = containingClass;
        }

        assert classMember instanceof ClassDeclaration : classMember.getClass().getName();
        return (ClassDeclaration) classMember;
    }

    public static boolean hasDefaultConstructor(TypeDeclaration jclass) {
        if (! (jclass instanceof ClassDeclaration)) return false;

        ConstructorDeclaration[] constructors = ((ClassDeclaration) jclass).getConstructors();

        for (int i = 0; i < constructors.length; i++) {
            ConstructorDeclaration ctor = constructors[i];
            if (ctor.getParameters().length == 0) return true;
        }

        return false;
    }

    private static Declaration findElement(Collection elements, String elementName) {
        for (Iterator ii = elements.iterator(); ii.hasNext();) {
            Object element = ii.next();
            Declaration decl = (Declaration) element;
            if (decl.getSimpleName().equals(elementName)) return decl;
        }

        return null;
    }

    public static FieldDeclaration findField(TypeDeclaration jclass, String fieldName) {
        return (FieldDeclaration) findElement(getClassFields(jclass), fieldName);
    }

    public static ClassDeclaration findInnerClass(TypeDeclaration jclass, String innerClassName) {
        return (ClassDeclaration) findElement(getClassNestedTypes(jclass), innerClassName);
    }

    public static String getEnumFieldName(AnnotationValue enumMember) {
        if (enumMember == null || enumMember.getValue() == null)
            return "";
        else
            return enumMember.getValue().toString();
    }

    /**
     * Get the qualified name of the given class, with '$' used to separate inner classes; the returned string can be
     * used with Class.forName().
     */
    public static String getLoadableName(TypeDeclaration jclass) {
        TypeDeclaration containingClass = jclass.getDeclaringType();

        if (containingClass == null) {
            return jclass.getQualifiedName();
        } else {
            return getLoadableName(containingClass) + '$' + jclass.getSimpleName();
        }
    }

    public static String getLoadableName(DeclaredType jclass) {
        return getLoadableName(getDeclaration(jclass));
    }

    public static File getSourceFile(TypeDeclaration decl, boolean mustBeNonNull) {
        decl = getOutermostClass(decl);
        SourcePosition position = decl.getPosition();
        if (mustBeNonNull) assert position != null : "no source file for " + decl.toString();
        return position != null ? position.file() : null;
    }

    public static boolean hasActionMethodSignature(MethodDeclaration method, AnnotationProcessorEnvironment env) {
        if (! ActionGrammar.hasActionMethodReturnType(method, env)) {
            return false;
        }

        ParameterDeclaration[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return true;
        } else if (parameters.length == 1) {
            TypeInstance argType = parameters[0].getType();
            if (! (argType instanceof DeclaredType)) {
                return false;
            }
            TypeDeclaration argTypeDecl = getDeclaration((DeclaredType) argType);
            boolean isClass = argTypeDecl instanceof ClassDeclaration;

            if (isClass && ! hasDefaultConstructor(argTypeDecl)) {
                return false;
            }

            if (! argTypeDecl.hasModifier(Modifier.PUBLIC)) {
                return false;
            }

            if (isClass && argTypeDecl.getDeclaringType() != null && ! argTypeDecl.hasModifier(Modifier.STATIC)) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public static class ExtendedAnnotationProcessorEnvironment
            implements AnnotationProcessorEnvironment {

        private AnnotationProcessorEnvironment _env;
        private boolean _useEqualsToCompareAnnotations;
        private HashMap _attributes;

        public ExtendedAnnotationProcessorEnvironment(AnnotationProcessorEnvironment env,
                                                      boolean useEqualsToCompareAnnotations) {
            _env = env;
            _useEqualsToCompareAnnotations = useEqualsToCompareAnnotations;
        }

        public boolean useEqualsToCompareAnnotations() {
            return _useEqualsToCompareAnnotations;
        }

        public Map getOptions() {
            return _env.getOptions();
        }

        public Messager getMessager() {
            return _env.getMessager();
        }

        public Filer getFiler() {
            return _env.getFiler();
        }

        public TypeDeclaration[] getSpecifiedTypeDeclarations() {
            return _env.getSpecifiedTypeDeclarations();
        }

        public TypeDeclaration getTypeDeclaration(String s) {
            return _env.getTypeDeclaration(s);
        }

        public Declaration[] getDeclarationsAnnotatedWith(AnnotationTypeDeclaration annotationTypeDeclaration) {
            return _env.getDeclarationsAnnotatedWith(annotationTypeDeclaration);
        }

        public void setAttribute(String propertyName, Object value) {
            if (_attributes == null) _attributes = new HashMap();
            _attributes.put(propertyName, value);
        }

        public Object getAttribute(String propertyName) {
            return _attributes != null ? _attributes.get(propertyName) : null;
        }
    }

    public static boolean annotationsAreEqual(AnnotationInstance a1, AnnotationInstance a2, boolean allowExactDuplicates,
                                              AnnotationProcessorEnvironment env) {
        assert a1 != null;
        if (a2 == null) return false;

        //
        // TODO: This entire method is a workaround for a bug in APT where an annotation may not equal itelf.
        // If this behavior changes, we want to rely on equals(), not this deep comparison, which is more expensive
        // and wrong if the two annotations 'look' exactly the same.
        //
        if (! allowExactDuplicates
                && env instanceof ExtendedAnnotationProcessorEnvironment
                && ((ExtendedAnnotationProcessorEnvironment) env).useEqualsToCompareAnnotations()) {
            return a1.equals(a2);
        }

        Map vals1 = a1.getElementValues();
        Map vals2 = a2.getElementValues();

        if (vals1.size() != vals2.size()) return false;


        Iterator ents1 = vals1.entrySet().iterator();
        Iterator ents2 = vals2.entrySet().iterator();
        while (ents1.hasNext()) {
            Map.Entry entry1 = (Map.Entry) ents1.next();
            Map.Entry entry2 = (Map.Entry) ents2.next();

            if (! ((AnnotationTypeElementDeclaration) entry1.getKey()).getSimpleName().equals(((AnnotationTypeElementDeclaration) entry2.getKey()).getSimpleName())) return false;
            Object val1 = ((AnnotationValue) entry1.getValue()).getValue();
            Object val2 = ((AnnotationValue) entry2.getValue()).getValue();

            if (val1 instanceof Collection) {
                if (! (val2 instanceof Collection)) return false;
                Collection list1 = (Collection) val1;
                Collection list2 = (Collection) val2;
                if (list1.size() != list2.size()) return false;
                Iterator j1 = list1.iterator();
                Iterator j2 = list2.iterator();

                while (j1.hasNext()) {
                    Object o1 = ((AnnotationValue) j1.next()).getValue();
                    Object o2 = ((AnnotationValue) j2.next()).getValue();

                    if (o1 instanceof AnnotationInstance) {
                        if (! (o2 instanceof AnnotationInstance)) return false;
                        if (! annotationsAreEqual((AnnotationInstance) o1, (AnnotationInstance) o2, allowExactDuplicates, env)) return false;
                    } else {
                        if (! o1.equals(o2)) return false;
                    }
                }
            } else if (val1 instanceof AnnotationInstance) {
                if (! (val2 instanceof AnnotationInstance)) return false;
                if (! annotationsAreEqual((AnnotationInstance) val1, (AnnotationInstance) val2, allowExactDuplicates, env)) return false;
            } else if (! val1.equals(val2)) {
                return false;
            }
        }

        return true;
    }

    public static class BeanPropertyDescriptor {

        private String _propertyName;
        private String _type;

        public BeanPropertyDescriptor(String propertyName, String type) {
            _propertyName = propertyName;
            _type = type;
        }

        public String getPropertyName() {
            return _propertyName;
        }

        public String getType() {
            return _type;
        }
    }

    public static class BeanPropertyDeclaration
            extends BeanPropertyDescriptor {

        private MethodDeclaration _getter;


        public BeanPropertyDeclaration(String propertyName, String type, MethodDeclaration getter) {
            super(propertyName, type);
            _getter = getter;
        }

        public MethodDeclaration getGetter() {
            return _getter;
        }
    }

    public static BeanPropertyDeclaration getBeanProperty(MethodDeclaration method) {
        if (method.hasModifier(Modifier.PUBLIC) && ! method.hasModifier(Modifier.STATIC)) {
            String returnType = method.getReturnType().toString();

            if (! returnType.equals("void") && method.getParameters().length == 0) {
                String methodName = method.getSimpleName();
                String propertyName = null;

                if (methodName.startsWith(GETTER_PREFIX) && methodName.length() > GETTER_PREFIX.length()) {
                    propertyName = methodName.substring(GETTER_PREFIX.length());
                } else if (methodName.startsWith(BOOLEAN_GETTER_PREFIX) && returnType.equals("boolean")
                        && methodName.length() > BOOLEAN_GETTER_PREFIX.length()) {
                    propertyName = methodName.substring(BOOLEAN_GETTER_PREFIX.length());
                }

                if (propertyName != null) {
                    //
                    // If the first two letters are uppercase, we don't change the first character to lowercase.
                    // This is so that something like getURI has a property name of 'URI' (see JavaBeans spec).
                    //
                    if (propertyName.length() == 1) {
                        propertyName = propertyName.toLowerCase();
                    } else if (! Character.isUpperCase(propertyName.charAt(1))) {
                        propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
                    }

                    return new BeanPropertyDeclaration(propertyName, returnType, method);
                }
            }
        }

        return null;
    }

    public static Collection getBeanProperties(ClassDeclaration type, boolean getInheritedProperties) {
        MethodDeclaration[] methods = getInheritedProperties ? getClassMethods(type, null) : type.getMethods();
        ArrayList ret = new ArrayList();

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];

            if (method.hasModifier(Modifier.PUBLIC)) {
                BeanPropertyDeclaration bpd = getBeanProperty(method);
                if (bpd != null) ret.add(bpd);
            }
        }

        return ret;
    }

    public static boolean isPageFlowClass(ClassDeclaration jclass, AnnotationProcessorEnvironment env) {
        return getAnnotation(jclass, CONTROLLER_TAG_NAME) != null && isAssignableFrom(JPF_BASE_CLASS, jclass, env);
    }

    public static String removeFileExtension(String uri) {
        int lastDot = uri.lastIndexOf('.');
        return uri.substring(0, lastDot);
    }

    public static TypeDeclaration inferTypeFromPath(String webappRelativePath, AnnotationProcessorEnvironment env) {
        assert webappRelativePath.startsWith("/") : webappRelativePath;
        String className = removeFileExtension(webappRelativePath.substring(1));
        return env.getTypeDeclaration(className.replace('/', '.'));
    }

    public static TypeDeclaration getDeclaration(DeclaredType type) {
        return type != null ? type.getDeclaration() : ERROR_TYPE_DECLARATION;
    }

    private static class ErrorTypeInstance
            implements TypeInstance {

        public String toString() {
            return ERROR_STRING;
        }
    }

    private static class ErrorTypeDeclaration
            implements TypeDeclaration {

        private static final InterfaceType[] SUPERINTERFACES = new InterfaceType[0];
        private static final FieldDeclaration[] FIELDS = new FieldDeclaration[0];
        private static final MethodDeclaration[] METHODS = new MethodDeclaration[0];
        private static final TypeDeclaration[] NESTED_TYPES = new TypeDeclaration[0];
        private static final AnnotationInstance[] ANNOTATIONS = new AnnotationInstance[0];

        public PackageDeclaration getPackage() {
            throw new IllegalStateException("not implemented ");
        }

        public String getQualifiedName() {
            return ERROR_STRING;
        }

        /*
        public Collection getFormalTypeParameters()
        {
            return Collections.EMPTY_LIST;
        }
        */

        public InterfaceType[] getSuperinterfaces() {
            return SUPERINTERFACES;
        }

        public FieldDeclaration[] getFields() {
            return FIELDS;
        }

        public MethodDeclaration[] getMethods() {
            return METHODS;
        }

        public TypeDeclaration[] getNestedTypes() {
            return NESTED_TYPES;
        }

        public TypeDeclaration getDeclaringType() {
            return null;
        }

        public String getDocComment() {
            throw new IllegalStateException("not implemented ");
        }

        public AnnotationInstance[] getAnnotationInstances() {
            return ANNOTATIONS;
        }

        /*
        public Annotation getAnnotation( Class s )
        {
            throw new IllegalStateException( "not implemented " );
        }
        */

        public Set getModifiers() {
            return Collections.EMPTY_SET;
        }

        public String getSimpleName() {
            return getQualifiedName();
        }

        public SourcePosition getPosition() {
            throw new IllegalStateException("not implemented ");
        }

        public boolean hasModifier(Modifier modifier) {
            return false;
        }

        /*
        public void accept( DeclarationVisitor declarationVisitor )
        {
            throw new IllegalStateException( "not implemented " );
        }
        */
    }

    /**
     * This is the same logic that we have in the runtime, in PageFlowRequestProcessor.  Can't share the code, though.
     */
    public static boolean isAbsoluteURI(String uri) {
        //
        // This method needs to be fast, so it can't use java.net.URI.
        //
        if (uri.length() == 0 || uri.charAt(0) == '/') return false;

        for (int i = 0, len = uri.length(); i < len; ++i) {
            char c = uri.charAt(i);

            if (c == ':') {
                return true;
            } else if (c == '/') {
                return false;
            }
        }

        return false;
    }

    public static TypeInstance getArrayBaseType(ArrayType arrayType) {
        TypeInstance baseType = arrayType;

        do {
            baseType = ((ArrayType) baseType).getComponentType();
        } while (baseType instanceof ArrayType);

        return baseType;
    }

    public static final class Mutable {

        private Object _val = null;

        public Mutable() {
        }

        public Mutable(Object val) {
            _val = val;
        }

        public void set(Object val) {
            _val = val;
        }

        public Object get() {
            return _val;
        }
    }

    public static TypeInstance getGenericBoundsType(TypeInstance type) {
        if (type instanceof TypeVariable) {
            ReferenceType[] bounds = ((TypeVariable) type).getDeclaration().getBounds();
            return bounds.length > 0 ? bounds[0] : type;
        }

        return type;
    }

    public static File getFileRelativeToSourceFile(TypeDeclaration outerClass, String relativePath,
                                                   AnnotationProcessorEnvironment env)
            throws FatalCompileTimeException {
        assert relativePath.length() > 0;

        //
        // If it starts with '/', just find the webapp-relative file.
        //
        if (relativePath.charAt(0) == '/') return getWebappRelativeFile(relativePath, true, env);

        //
        // Look for the file relative to the source directory of the given class.
        //
        File sourceFile = getSourceFile(outerClass, false);
        File desiredParentDir = sourceFile.getAbsoluteFile().getParentFile();
        File retVal = new File(desiredParentDir, relativePath);

        //
        // If we still haven't found it, construct a webapp-relative path and look for the file relative to the webapp.
        //
        if (! retVal.exists()) {
            PackageDeclaration jpfPackage = outerClass.getPackage();
            return getWebappRelativeFile(getPathRelativeToPackage(relativePath, jpfPackage), true, env);
        }

        return retVal;
    }

    public static String getPathRelativeToPackage(String relativePath, PackageDeclaration packageDecl) {
        if (packageDecl != null) {
            String packageName = packageDecl.getQualifiedName();
            if (packageName.length() > 0) return '/' + packageName.replace('.', '/') + '/' + relativePath;
        }

        return '/' + relativePath;
    }

    public static File getWebappRelativeFile(String webappRelativePath, boolean lookInSourceRoots,
                                             AnnotationProcessorEnvironment env)
            throws FatalCompileTimeException {
        //
        // Look for the file out in the web-addressable portion of the webapp.
        //
        assert webappRelativePath.startsWith("/") : webappRelativePath;
        String[] webContentRoots = getWebContentRoots(env);

        for (int i = 0; i < webContentRoots.length; i++) {
            String webContentRoot = webContentRoots[i];
            File retVal = new File(webContentRoot + webappRelativePath);
            if (retVal.exists()) return retVal;
        }

        //
        // If appropriate, look for the file under all the source roots.
        //
        if (lookInSourceRoots) {
            String[] webSourceRoots = getWebSourceRoots(env);

            for (int i = 0; i < webSourceRoots.length; i++) {
                String webSourceRoot = webSourceRoots[i];
                File webSourceRelativeFile = new File(webSourceRoot + webappRelativePath);
                if (webSourceRelativeFile.exists()) return webSourceRelativeFile;
            }
        }

        return null;
    }

    public static String[] getWebSourceRoots(AnnotationProcessorEnvironment env)
            throws FatalCompileTimeException {
        return (String[]) getOption("-sourcepath", true, env);
    }

    public static String[] getWebContentRoots(AnnotationProcessorEnvironment env)
            throws FatalCompileTimeException {
        return (String[]) getOption("-Aweb.content.root", true, env);
    }

    private static Object getOption(String optionName, boolean isList, AnnotationProcessorEnvironment env)
            throws MissingOptionException {
        Object cached = env.getAttribute(optionName);
        if (cached != null) return cached;

        Map options = env.getOptions();
        String value = (String) options.get(optionName);

        if (value == null) {
            // TODO: there appears to be a bug in APT where both the key/value are contained in the key
            String aptOption = optionName + '=';
            for (Iterator i = options.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();

                if (key.startsWith(aptOption)) {
                    value = key.substring(aptOption.length());
                    break;
                }
            }
        }

        if (value == null) throw new MissingOptionException(optionName);

        Object retVal = value;

        if (isList) {
            String[] values = ((String) retVal).trim().split(File.pathSeparator);
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            retVal = values;
        }

        env.setAttribute(optionName, retVal);
        return retVal;
    }
    
    public static AnnotationInstance getActionAnnotation(MethodDeclaration method, TypeDeclaration containingType,
                                                         AnnotationProcessorEnvironment env) {
        AnnotationInstance actionAnnotation = CompilerUtils.getAnnotation(method, ACTION_TAG_NAME);
        if (actionAnnotation != null) {
            return actionAnnotation;
        }
        
        if (hasActionMethodSignature(method, env)) {
            SynthesizedAnnotation annotation =
                    new SynthesizedAnnotation(containingType, ACTION_TAG_NAME, method.getPosition(), env);
            AnnotationInstance forwardAnnotation = CompilerUtils.getAnnotation(method, FORWARD_TAG_NAME);
            if (forwardAnnotation != null) {
                annotation.addMemberValue(FORWARDS_ATTR, forwardAnnotation, true);
            }
            return annotation;
        }
        
        return null;
    }
}
