/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.uel.elresolvers;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import org.apache.commons.lang.xwork.StringUtils;

import javax.el.ELContext;
import java.util.Map;

/**
 * Resolves references to variables in the ValueStack context (like #action)
 */
public class ValueStackContextReferenceELResolver extends AbstractELResolver {
    public ValueStackContextReferenceELResolver(Container container) {
        super(container);
    }

    public Object getValue(ELContext elContext, Object base, Object property) {
        String objectName = property.toString();
        if (StringUtils.startsWith(objectName, "#")) {
            objectName = StringUtils.removeStart(property.toString(), "#");

            Map valueStackContext = getValueStackContext(elContext);
            Object obj = valueStackContext.get(objectName);

            if (obj != null) {
                valueStackContext.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, obj.getClass());
                valueStackContext.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, objectName);
                elContext.setPropertyResolved(true);
                return obj;
            }
        }

        return null;
    }

    public void setValue(ELContext elContext, Object o, Object o1, Object o2) {
    }
}
