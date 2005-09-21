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

import org.apache.ti.compiler.internal.model.XWorkModuleConfigModel;
import org.apache.ti.compiler.internal.model.XmlModelWriter;
import org.apache.ti.compiler.internal.model.XmlModelWriterException;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AnnotationToXML {
    private static final String ANNOTATIONS_FILE_PREFIX = "annotations-";
    private XmlModelWriter _xw;
    private TypeDeclaration _typeDecl;

    public AnnotationToXML(TypeDeclaration typeDecl) throws IOException, XmlModelWriterException {
        _typeDecl = typeDecl;

        String typeName = typeDecl.getQualifiedName();
        StringBuffer comment = new StringBuffer(" Generated from ");
        comment.append(typeName);
        comment.append(" on ").append(new Date().toString()).append(' ');
        _xw = new XmlModelWriter(null, "processed-annotations", null, null, comment.toString());
        _xw.addElementWithText(_xw.getRootElement(), "type-name", typeName);
    }

    public void include(MemberDeclaration memberDecl, AnnotationInstance annotation) {
        String name = (memberDecl instanceof TypeDeclaration) ? ((TypeDeclaration) memberDecl).getQualifiedName()
                                                              : memberDecl.getSimpleName();
        Element annotatedElement = _xw.addElement(_xw.getRootElement(), "annotated-element");
        _xw.addElementWithText(annotatedElement, "element-name", name);

        Element xmlAnnotation = _xw.addElement(annotatedElement, "annotation");
        include(xmlAnnotation, annotation);
    }

    private void include(Element xmlAnnotation, AnnotationInstance annotation) {
        String annotationName = annotation.getAnnotationType().getAnnotationTypeDeclaration().getQualifiedName();
        _xw.addElementWithText(xmlAnnotation, "annotation-name", annotationName);

        Map elementValues = annotation.getElementValues();

        for (Iterator i = elementValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            AnnotationTypeElementDeclaration elementDecl = (AnnotationTypeElementDeclaration) entry.getKey();
            AnnotationValue annotationValue = (AnnotationValue) entry.getValue();

            String name = elementDecl.getSimpleName();
            Object value = annotationValue.getValue();
            Element xmlAttr = _xw.addElement(xmlAnnotation, "annotation-attribute");
            _xw.addElementWithText(xmlAttr, "attribute-name", name);

            if (value instanceof List) {
                for (Iterator j = ((List) value).iterator(); j.hasNext();) {
                    Object o = j.next();
                    assert o instanceof AnnotationValue : o.getClass().getName();

                    Object listVal = ((AnnotationValue) o).getValue();

                    // we only handle lists of annotations at the moment
                    assert listVal instanceof AnnotationInstance : listVal.getClass().getName();
                    include(_xw.addElement(xmlAttr, "annotation-value"), (AnnotationInstance) listVal);
                }
            } else {
                // we only support a few types at the moment
                assert value instanceof TypeInstance || value instanceof String : value.getClass().getName();
                _xw.addElementWithText(xmlAttr, "string-value", value.toString());
            }
        }
    }

    public void writeXml(Diagnostics diagnostics, AnnotationProcessorEnvironment env)
            throws IOException, XmlModelWriterException {
        String outputFilePath = getFilePath(_typeDecl);
        File outputFile = new File(outputFilePath);
        PrintWriter writer = env.getFiler().createTextFile(outputFile);

        try {
            _xw.simpleFastWrite(writer);
        } finally {
            writer.close();
        }
    }

    public static String getFilePath(TypeDeclaration typeDecl) {
        String typeName = typeDecl.getQualifiedName();
        int lastDot = typeName.lastIndexOf('.');
        typeName = typeName.substring(lastDot + 1);

        return XWorkModuleConfigModel.getOutputFilePath(ANNOTATIONS_FILE_PREFIX + typeName,
                                                        typeDecl.getPackage().getQualifiedName());
    }
}
