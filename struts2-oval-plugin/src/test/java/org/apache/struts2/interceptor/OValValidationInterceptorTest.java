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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OValValidationInterceptorTest extends XWorkTestCase {
    public void testSimpleField() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleField", null, null);
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = ((ValidationAware) baseActionProxy.getAction()).getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("org.apache.struts2.interceptor.SimpleField.name cannot be null"));
    }

    public void testSimpleFieldNegative() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleField", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        action.setName("123");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(0, fieldErrors.size());
    }

    public void testSimpleFieldMultipleValidators() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleField", null, null);
        SimpleField action = (SimpleField) baseActionProxy.getAction();
        action.setName("12345");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertNotNull(fieldErrors.get("firstName"));
    }

    public void testSimpleMethod() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleMethod", null, null);
        SimpleMethod action = (SimpleMethod) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertNotNull(fieldErrors.get("firstName"));
    }

    public void testSimpleFieldOGNLExpression() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldOGNL", null, null);
        SimpleFieldOGNLExpression action = (SimpleFieldOGNLExpression) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertNotNull(fieldErrors.get("firstName"));
    }

     public void testFieldsWithMultipleProfiles() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "fieldsWidthProfiles13", null, null);
        FieldsWithProfiles action = (FieldsWithProfiles) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(2, fieldErrors.size());
        assertNotNull(fieldErrors.get("firstName"));
        assertNotNull(fieldErrors.get("lastName"));
    }

    public void testSimpleFieldOGNLExpressionNegative() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldOGNL", null, null);
        SimpleFieldOGNLExpression action = (SimpleFieldOGNLExpression) baseActionProxy.getAction();
        action.setName("Meursault");
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        assertNotNull(fieldErrors);
        assertEquals(0, fieldErrors.size());
    }

    public void testSimpleFieldI18n() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldI18n", null, null);
        SimpleFieldI18n action = (SimpleFieldI18n) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("name cannot be null"));
    }

     public void testSimpleFieldI18nDefaultKey() throws Exception {
        ActionProxy baseActionProxy = actionProxyFactory.createActionProxy("oval", "simpleFieldI18nDefaultKey", null, null);
        SimpleFieldI18nDefaultKey action = (SimpleFieldI18nDefaultKey) baseActionProxy.getAction();
        baseActionProxy.execute();

        Map<String, List<String>> fieldErrors = action.getFieldErrors();

        assertNotNull(fieldErrors);
        assertEquals(1, fieldErrors.size());
        assertValue(fieldErrors, "name", Arrays.asList("notnull.field"));
    }

    private void assertValue(Map<String, List<String>> map, String key, List<String> expectedValues) {
        assertNotNull(map);
        assertNotNull(key);
        assertNotNull(expectedValues);

        List<String> values = map.get(key);
        assertNotNull(values);
        assertEquals(expectedValues, values);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new XmlConfigurationProvider("oval-test.xml"));
    }
}
