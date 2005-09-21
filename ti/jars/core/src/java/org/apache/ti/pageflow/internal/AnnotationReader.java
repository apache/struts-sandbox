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
import org.apache.ti.pageflow.internal.annotationreader.AnnotationAttribute;
import org.apache.ti.pageflow.internal.annotationreader.ProcessedAnnotation;
import org.apache.ti.pageflow.internal.annotationreader.ProcessedAnnotationParser;
import org.apache.ti.pageflow.internal.annotationreader.ProcessedAnnotations;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.SourceResolver;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.Member;

import java.net.URL;

import java.util.Iterator;
import java.util.Map;

/**
 * Utility for reading XML files that describe annotations in classes.  These files are generated during Page Flow build.
 */
public class AnnotationReader
        implements Serializable {
    private static final Logger _log = Logger.getInstance(AnnotationReader.class);
    private ProcessedAnnotations _annotations;

    public AnnotationReader(Class type, SourceResolver sourceResolver) {
        String typeName = type.getName();
        InternalStringBuilder buf = new InternalStringBuilder(PageFlowConstants.PAGEFLOW_MODULE_CONFIG_GEN_DIR);
        buf.append('/');

        int lastDot = typeName.lastIndexOf('.');

        if (lastDot != -1) {
            buf.append(typeName.substring(0, lastDot + 1).replace('.', '/'));
        }

        buf.append("annotations-");
        buf.append(typeName.substring(lastDot + 1));
        buf.append(".xml");

        String annotationsXml = buf.toString();

        try {
            WebContext webContext = PageFlowActionContext.get().getWebContext();
            URL url = sourceResolver.resolve(annotationsXml, webContext);

            if (url != null) {
                InputStream in = url.openStream();
                _annotations = ProcessedAnnotationParser.parse(annotationsXml, in);

                try {
                    in.close();
                } catch (IOException e) {
                    _log.error("Could not close input stream for " + annotationsXml, e);
                }
            }
        } catch (IOException e) {
            _log.error("Error while reading annotations XML file " + annotationsXml, e);
        }
    }

    public ProcessedAnnotation getAnnotation(String declarationName, String annotationTypeName) {
        if (_annotations == null) {
            return null;
        }

        Map elements = _annotations.getAnnotatedElements();

        for (Iterator i = elements.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();

            if (name.equals(declarationName)) {
                // For now, we can be sure that there's only one element in this array.
                ProcessedAnnotation[] annotations = (ProcessedAnnotation[]) elements.get(name);
                assert annotations.length == 1 : annotations.length;

                ProcessedAnnotation pa = annotations[0];

                return pa.getAnnotationName().equals(annotationTypeName) ? pa : null;
            }
        }

        return null;
    }

    public ProcessedAnnotation getTiAnnotation(Member member, String annotationTypeName) {
        return getAnnotation(member.getName(), InternalConstants.ANNOTATION_QUALIFIER + annotationTypeName);
    }

    public ProcessedAnnotation getTiAnnotation(Class type, String annotationTypeName) {
        return getAnnotation(type.getName(), InternalConstants.ANNOTATION_QUALIFIER + annotationTypeName);
    }

    public static String getStringAttribute(ProcessedAnnotation ann, String attrName) {
        AnnotationAttribute[] attrs = ann.getAnnotationAttributes();

        for (int i = 0; i < attrs.length; i++) {
            AnnotationAttribute attr = attrs[i];

            if (attr.getAttributeName().equals(attrName)) {
                String value = attr.getStringValue();
                assert value != null : "attribute " + attrName + " did not have a String value";

                return value;
            }
        }

        return null;
    }

    public static ProcessedAnnotation[] getAnnotationArrayAttribute(ProcessedAnnotation ann, String attrName) {
        AnnotationAttribute[] attrs = ann.getAnnotationAttributes();

        for (int i = 0; i < attrs.length; i++) {
            AnnotationAttribute attr = attrs[i];

            if (attr.getAttributeName().equals(attrName)) {
                ProcessedAnnotation[] array = attr.getAnnotationValues();
                assert array != null : "attribute " + attrName + " did not have an array of annotations.";

                return array;
            }
        }

        return null;
    }
}
