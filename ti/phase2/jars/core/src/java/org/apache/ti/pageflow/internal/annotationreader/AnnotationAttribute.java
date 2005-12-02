/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.pageflow.internal.annotationreader;


/**
 *
 */
public class AnnotationAttribute {
    private String _attributeName;
    private String _stringValue;
    private ProcessedAnnotation[] _annotationValues;

    public AnnotationAttribute() {
    }

    public AnnotationAttribute(String attributeName, String value) {
        _attributeName = attributeName;
        _stringValue = value;
    }

    public AnnotationAttribute(String attributeName, ProcessedAnnotation[] annotationValues) {
        _attributeName = attributeName;
        _annotationValues = annotationValues;
    }

    public String getAttributeName() {
        return _attributeName;
    }

    public void setAttributeName(String attributeName) {
        _attributeName = attributeName;
    }

    public String getStringValue() {
        return _stringValue;
    }

    public void setStringValue(String value) {
        _stringValue = value;
    }

    public ProcessedAnnotation[] getAnnotationValues() {
        return _annotationValues;
    }

    public void setAnnotationValues(ProcessedAnnotation[] annotationValues) {
        _annotationValues = annotationValues;
    }
}
