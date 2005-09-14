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
package org.apache.ti.compiler.xdoclet;

import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.WrapperFactory;
import xjavadoc.XClass;
import xjavadoc.XJavaDoc;
import xjavadoc.XPackage;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class XDocletCompilerUtils {

    private static final ResourceBundle MESSAGES =
            ResourceBundle.getBundle("org.apache.ti.compiler.xdoclet.Messages");

    public static void addError(SourcePosition sourcePosition, String messageKey, String[] args) {
        assert sourcePosition != null;
        String message = getMessage(messageKey, args);
        PageFlowDocletTask.get().addError(message, sourcePosition);
    }

    public static void addWarning(SourcePosition sourcePosition, String messageKey, String[] args) {
        assert sourcePosition != null;
        String message = getMessage(messageKey, args);
        PageFlowDocletTask.get().addWarning(message, sourcePosition);
    }

    public static String getMessage(String messageKey, String[] args) {
        String message = MESSAGES.getString(messageKey);
        if (args != null) message = MessageFormat.format(message, args);
        return message;
    }

    public static TypeDeclaration resolveTypeDeclaration(String typeName) {
        assert ! typeName.endsWith("[]") : "array type not allowed here: " + typeName;
        return (TypeDeclaration) resolveTypeInstanceOrTypeDecl(typeName, true, null, false);
    }

    private static XClass getXClass(String typeName, XJavaDoc xJavaDoc) {
        assert ! typeName.endsWith("[]") : "array type not allowed here: " + typeName;

        XClass type = xJavaDoc.getXClass(typeName);

        //
        // This may be an inner class, which needs a '$' instead of a '.'.
        //
        if (isUnknownClass(type)) {
            int lastDot = typeName.lastIndexOf('.');

            if (lastDot != -1) {
                return getXClass(typeName.substring(0, lastDot) + '$' + typeName.substring(lastDot + 1), xJavaDoc);
            }
        }

        return type;
    }

    private static boolean isUnknownClass(XClass xclass) {
        return xclass == null || xclass.getClass().getName().equals("xjavadoc.UnknownClass");
    }

    public static TypeInstance resolveType(String typeName, boolean allowErrorType, XClass currentClass) {
        return (TypeInstance) resolveTypeInstanceOrTypeDecl(typeName, allowErrorType, currentClass, true);
    }

    private static Object resolveTypeInstanceOrTypeDecl(String typeName, boolean allowUnknownType, XClass currentClass,
                                                        boolean returnTypeInstance) {
        int arrayDimensions = 0;

        if (typeName.endsWith(".class")) typeName = typeName.substring(0, typeName.length() - 6);

        while (typeName.endsWith("[]")) {
            typeName = typeName.substring(0, typeName.length() - 2);
            ++arrayDimensions;
        }

        if (currentClass == null) currentClass = PageFlowDocletSubTask.get().getCurrentSourceClass();
        XJavaDoc xJavaDoc = currentClass.getXJavaDoc();

        XClass originalResolvedType = getXClass(typeName, xJavaDoc);
        XClass attemptedResolvedType = originalResolvedType;

        if (isUnknownClass(attemptedResolvedType)) {
            attemptedResolvedType = getXClass("java.lang." + typeName, xJavaDoc);
        }

        if (isUnknownClass(attemptedResolvedType)) {
            // See if it was an imported class.
            List importedClasses = currentClass.getImportedClasses();
            String dotPrepended = '.' + typeName;

            for (Iterator i = importedClasses.iterator(); i.hasNext();) {
                XClass importedClass = (XClass) i.next();
                if (importedClass.getQualifiedName().endsWith(dotPrepended)) {
                    attemptedResolvedType = getXClass(importedClass.getQualifiedName(), xJavaDoc);
                    break;
                }
            }
        }

        if (isUnknownClass(attemptedResolvedType)) {
            // See if it was in an imported package.
            List importedPackages = currentClass.getImportedPackages();
            String dotPrepended = '.' + typeName;

            for (Iterator i = importedPackages.iterator(); i.hasNext();) {
                XPackage importedPackage = (XPackage) i.next();
                XClass implicitImportedClass = getXClass(importedPackage.getName() + dotPrepended, xJavaDoc);
                if (! isUnknownClass(implicitImportedClass)) {
                    attemptedResolvedType = implicitImportedClass;
                    break;
                }
            }
        }

        if (isUnknownClass(attemptedResolvedType)) {
            // Try it with the full outer classname appended.
            String outerClassName = currentClass.getQualifiedName();
            attemptedResolvedType = getXClass(outerClassName + '.' + typeName, xJavaDoc);
        }

        if (isUnknownClass(attemptedResolvedType)) {
            // Finally, it may be of the form <outer-class-short-name>.<inner-class-name>
            String outerClassName = currentClass.getQualifiedName();
            int lastDot = outerClassName.lastIndexOf('.');
            outerClassName = lastDot != -1 ? outerClassName.substring(0, lastDot) : outerClassName;
            attemptedResolvedType = getXClass(outerClassName + '.' + typeName, xJavaDoc);
        }

        if (isUnknownClass(attemptedResolvedType)) {
            if (! allowUnknownType) return null;
            if (returnTypeInstance) return WrapperFactory.get().getTypeInstance(originalResolvedType);
            return WrapperFactory.get().getTypeDeclaration(originalResolvedType);
        }

        if (returnTypeInstance) return WrapperFactory.get().getTypeInstance(attemptedResolvedType, arrayDimensions);
        return WrapperFactory.get().getTypeDeclaration(attemptedResolvedType);
    }
}
