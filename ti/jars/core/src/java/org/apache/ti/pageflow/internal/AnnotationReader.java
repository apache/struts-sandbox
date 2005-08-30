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
package org.apache.ti.pageflow.internal;

import org.apache.commons.chain.web.WebContext;
import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.schema.annotations.AnnotatedElement;
import org.apache.ti.schema.annotations.AnnotationAttribute;
import org.apache.ti.schema.annotations.ProcessedAnnotation;
import org.apache.ti.schema.annotations.ProcessedAnnotationsDocument;
import org.apache.ti.util.SourceResolver;
import org.apache.ti.util.logging.Logger;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Member;
import java.net.URL;

/**
 * Utility for reading XML files that describe annotations in classes.  These files are generated during Page Flow build.
 */
public class AnnotationReader
        implements Serializable {

    private static final Logger _log = Logger.getInstance(AnnotationReader.class);

    private ProcessedAnnotationsDocument.ProcessedAnnotations _annotations;

    public AnnotationReader(Class type, SourceResolver sourceResolver) {
        String annotationsXml =
                PageFlowConstants.PAGEFLOW_MODULE_CONFIG_GEN_DIR + "/jpf-annotations-"
                + type.getName().replace('.', '-') + ".xml";

        try {
            WebContext webContext = PageFlowActionContext.get().getWebContext();
            URL url = sourceResolver.resolve(annotationsXml, webContext);

            if (url != null) {
                ProcessedAnnotationsDocument doc = ProcessedAnnotationsDocument.Factory.parse(url);
                _annotations = doc.getProcessedAnnotations();
            }
        } catch (XmlException e) {
            _log.error("Error while parsing annotations XML file " + annotationsXml, e);
        } catch (IOException e) {
            _log.error("Error while reading annotations XML file " + annotationsXml, e);
        }
    }

    public ProcessedAnnotation getAnnotation(String declarationName, String annotationTypeName) {
        if (_annotations == null) return null;

        AnnotatedElement[] elements = _annotations.getAnnotatedElementArray();

        for (int i = 0; i < elements.length; i++) {
            AnnotatedElement element = elements[i];
            if (element.getElementName().equals(declarationName)) {
                // For now, we can be sure that there's only one element in this array.
                assert element.getAnnotationArray().length == 1 : element.getAnnotationArray().length;
                ProcessedAnnotation pa = element.getAnnotationArray(0);
                return pa.getAnnotationName().equals(annotationTypeName) ? pa : null;
            }
        }

        return null;
    }

    public ProcessedAnnotation getJpfAnnotation(Member member, String annotationTypeName) {
        return getAnnotation(member.getName(), InternalConstants.ANNOTATION_QUALIFIER + annotationTypeName);
    }

    public ProcessedAnnotation getJpfAnnotation(Class type, String annotationTypeName) {
        return getAnnotation(type.getName(), InternalConstants.ANNOTATION_QUALIFIER + annotationTypeName);
    }

    public static String getStringAttribute(ProcessedAnnotation ann, String attrName) {
        AnnotationAttribute[] attrs = ann.getAnnotationAttributeArray();

        for (int i = 0; i < attrs.length; i++) {
            AnnotationAttribute attr = attrs[i];

            if (attr.getAttributeName().equals(attrName)) {
                String value = attr.getStringValue1();
                assert value != null : "attribute " + attrName + " did not have a String value";
                return value;
            }
        }

        return null;
    }

    public static ProcessedAnnotation[] getAnnotationArrayAttribute(ProcessedAnnotation ann, String attrName) {
        AnnotationAttribute[] attrs = ann.getAnnotationAttributeArray();

        for (int i = 0; i < attrs.length; i++) {
            AnnotationAttribute attr = attrs[i];

            if (attr.getAttributeName().equals(attrName)) {
                ProcessedAnnotation[] array = attr.getAnnotationValueArray();
                assert array != null : "attribute " + attrName + " did not have an array of annotations.";
                return array;
            }
        }

        return null;
    }
}
