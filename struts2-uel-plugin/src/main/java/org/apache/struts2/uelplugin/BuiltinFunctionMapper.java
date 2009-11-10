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
package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang.xwork.StringUtils;

import javax.el.FunctionMapper;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Builtin function availabe from UEL. Available functions are</p>
 * <ul>
 * <li>getText(String):Looks up a text from resource bundles using the parameter as a key</li>
 * </ul>
 */
public class BuiltinFunctionMapper extends FunctionMapper {
    private static final Map<String, Method> BUILTIN_FUNCTIONS = new HashMap<String, Method>() {
        {
            try {
                Method getText = BuiltinFunctionMapper.class.getMethod("getText", new Class[]{String.class});
                put("getText", getText);
            } catch (NoSuchMethodException e) {
                //this should never happen
                throw new RuntimeException(e);
            }
        }
    };


    public Method resolveFunction(String prefix, String localName) {
        return StringUtils.isBlank(prefix) ? BUILTIN_FUNCTIONS.get(localName) : null;
    }

    public static String getText(String key) {
        ValueStack stack = ActionContext.getContext().getValueStack();
        Object action = stack.findValue("#action");

        if (action != null && action instanceof LocaleProvider) {
            TextProvider textProvider = new TextProviderFactory().createInstance(action.getClass(), (LocaleProvider) action);
            return textProvider.getText(key);
        }

        return null;
    }
}
