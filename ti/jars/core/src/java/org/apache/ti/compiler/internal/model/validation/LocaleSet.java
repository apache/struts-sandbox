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

import org.apache.ti.schema.validator11.FormDocument;
import org.apache.ti.schema.validator11.FormsetDocument;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class LocaleSet {

    private Locale _locale;
    private Map _entities = new HashMap();


    public LocaleSet() {
        _locale = null;     // default locale;
    }

    public LocaleSet(Locale locale) {
        _locale = locale;
    }

    public Locale getLocale() {
        return _locale;
    }

    public ValidatableEntity getEntity(String entityName) {
        return (ValidatableEntity) _entities.get(entityName);
    }

    public void addValidatableEntity(ValidatableEntity entity) {
        _entities.put(entity.getEntityName(), entity);
    }

    public void writeToXMLBean(FormsetDocument.Formset formset) {
        if (_locale != null) {
            formset.setLanguage(_locale.getLanguage());
            if (_locale.getCountry().length() > 0) {
                formset.setCountry(_locale.getCountry());
                if (_locale.getVariant().length() > 0) {
                    formset.setVariant(_locale.getVariant());
                }
            }
        }

        FormDocument.Form[] existingFormElements = formset.getFormArray();
        for (Iterator i = _entities.values().iterator(); i.hasNext();) {
            ValidatableEntity entity = (ValidatableEntity) i.next();
            String entityName = entity.getEntityName();

            //
            // Look for an existing  element, or create one if none matches this entity name.
            //
            FormDocument.Form formElementToUse = null;

            for (int j = 0; j < existingFormElements.length; j++) {
                FormDocument.Form formElement = existingFormElements[j];

                if (entityName.equals(formElement.getName())) {
                    formElementToUse = formElement;
                    break;
                }
            }

            if (formElementToUse == null) {
                formElementToUse = formset.addNewForm();
                formElementToUse.setName(entityName);
            }

            entity.writeToXMLBean(formElementToUse);
        }
    }
}
