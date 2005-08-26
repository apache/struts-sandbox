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

import org.apache.ti.schema.annotations.ProcessedAnnotation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CachedSharedFlowRefInfo {

    public static class SharedFlowFieldInfo {

        public Field field;
        public String sharedFlowName;
    }

    /**
     * The SharedFlowController-initialized member fields -- may or may not be present.
     */
    private SharedFlowFieldInfo[] _sharedFlowMemberFields;

    protected void initSharedFlowFields(AnnotationReader annReader, Field[] fields) {
        List/*< SharedFlowFieldInfo >*/ sharedFlowFields = null;

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            ProcessedAnnotation sharedFlowFieldAnn = annReader.getJpfAnnotation(field, "sharedFlowField");

            if (sharedFlowFieldAnn != null) {
                if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
                if (sharedFlowFields == null) sharedFlowFields = new ArrayList/*< SharedFlowFieldInfo >*/();
                SharedFlowFieldInfo info = new SharedFlowFieldInfo();
                info.field = field;
                info.sharedFlowName = AnnotationReader.getStringAttribute(sharedFlowFieldAnn, "name");
                sharedFlowFields.add(info);
            }
        }

        if (sharedFlowFields != null) {
            _sharedFlowMemberFields = (SharedFlowFieldInfo[]) sharedFlowFields.toArray(new SharedFlowFieldInfo[sharedFlowFields.size()]);
        }
    }

    public SharedFlowFieldInfo[] getSharedFlowMemberFields() {
        return _sharedFlowMemberFields;
    }
}
