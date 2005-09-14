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
package org.apache.ti.compiler.internal.model.validation;

import org.apache.ti.schema.validator11.FieldDocument;
import org.apache.ti.schema.validator11.FormDocument;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ValidatableEntity {

    private String _entityName;
    private Map _fields = new HashMap();


    public ValidatableEntity(String entityName) {
        _entityName = entityName;
    }

    protected String getEntityName() {
        return _entityName;
    }

    public void addField(ValidatableField field) {
        _fields.put(field.getPropertyName(), field);
    }

    public ValidatableField getField(String fieldName) {
        return (ValidatableField) _fields.get(fieldName);
    }

    public void writeToXMLBean(FormDocument.Form formElement) {
        assert formElement.getName().equals(_entityName);

        FieldDocument.Field[] existingFieldElements = formElement.getFieldArray();
        for (Iterator i = _fields.values().iterator(); i.hasNext();) {
            ValidatableField field = (ValidatableField) i.next();
            FieldDocument.Field fieldElementToUse = null;
            String fieldPropertyName = field.getPropertyName();

            //
            // Look for an existing field element to update, or create one if none matches this field's property name.
            //
            for (int j = 0; j < existingFieldElements.length; j++) {
                FieldDocument.Field existingFieldElement = existingFieldElements[j];

                if (fieldPropertyName.equals(existingFieldElement.getProperty())) {
                    fieldElementToUse = existingFieldElement;
                    break;
                }
            }

            if (fieldElementToUse == null) {
                fieldElementToUse = formElement.addNewField();
                fieldElementToUse.setProperty(fieldPropertyName);
            }

            field.writeToXMLBean(fieldElementToUse);
        }
    }
}
