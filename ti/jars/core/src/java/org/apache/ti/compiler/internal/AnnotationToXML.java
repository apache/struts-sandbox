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
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.ti.schema.annotations.AnnotatedElement;
import org.apache.ti.schema.annotations.AnnotationAttribute;
import org.apache.ti.schema.annotations.ProcessedAnnotation;
import org.apache.ti.schema.annotations.ProcessedAnnotationsDocument;
import org.apache.xmlbeans.XmlOptions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AnnotationToXML {

    private static final String ANNOTATIONS_FILE_PREFIX = "annotations-";

    private ProcessedAnnotationsDocument _doc;
    private TypeDeclaration _typeDecl;

    public AnnotationToXML(TypeDeclaration typeDecl) {
        _doc = ProcessedAnnotationsDocument.Factory.newInstance();
        _typeDecl = typeDecl;
        ProcessedAnnotationsDocument.ProcessedAnnotations pa = _doc.addNewProcessedAnnotations();
        pa.setTypeName(typeDecl.getQualifiedName());
    }

    public void include(MemberDeclaration memberDecl, AnnotationInstance annotation) {
        AnnotatedElement element = _doc.getProcessedAnnotations().addNewAnnotatedElement();
        String name = memberDecl instanceof TypeDeclaration
                ? ((TypeDeclaration) memberDecl).getQualifiedName()
                : memberDecl.getSimpleName();
        element.setElementName(name);
        ProcessedAnnotation xmlAnnotation = element.addNewAnnotation();
        include(xmlAnnotation, annotation);
    }

    private void include(ProcessedAnnotation xmlAnnotation, AnnotationInstance annotation) {
        xmlAnnotation.setAnnotationName(annotation.getAnnotationType().getAnnotationTypeDeclaration().getQualifiedName());

        Map elementValues = annotation.getElementValues();

        for (Iterator i = elementValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            AnnotationTypeElementDeclaration elementDecl = (AnnotationTypeElementDeclaration) entry.getKey();
            AnnotationValue annotationValue = (AnnotationValue) entry.getValue();

            String name = elementDecl.getSimpleName();
            Object value = annotationValue.getValue();
            AnnotationAttribute xmlAttr = xmlAnnotation.addNewAnnotationAttribute();
            xmlAttr.setAttributeName(name);

            if (value instanceof List) {
                for (Iterator j = ((List) value).iterator(); j.hasNext();) {
                    Object o = j.next();
                    assert o instanceof AnnotationValue : o.getClass().getName();
                    Object listVal = ((AnnotationValue) o).getValue();

                    // we only handle lists of annotations at the moment
                    assert listVal instanceof AnnotationInstance : listVal.getClass().getName();
                    include(xmlAttr.addNewAnnotationValue(), (AnnotationInstance) listVal);
                }
            } else {
                // we only support a few types at the moment
                assert value instanceof TypeInstance || value instanceof String : value.getClass().getName();
                xmlAttr.setStringValue1(value.toString());
            }
        }
    }

    public void writeXml(Diagnostics diagnostics, AnnotationProcessorEnvironment env) {
        String typeName = _typeDecl.getQualifiedName();
        int lastDot = typeName.lastIndexOf('.');
        String baseName = ANNOTATIONS_FILE_PREFIX + typeName.substring(lastDot + 1);
        String containingPackage = lastDot != -1 ? typeName.substring(0, lastDot) : null;
        String outputFilePath = XWorkModuleConfigModel.getOutputFilePath(baseName, containingPackage);
        File outputFile = new File(outputFilePath);

        try {
            XmlOptions options = new XmlOptions();
            options.setSavePrettyPrint();
            PrintWriter writer = env.getFiler().createTextFile(outputFile);
            try {
                _doc.save(writer, options);
            }
            finally {
                writer.close();
            }
        }
        catch (IOException e) {
            diagnostics.addError(_typeDecl, "error.could-not-generate", outputFilePath, e.getMessage());
        }
    }
}
