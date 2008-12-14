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
package org.apache.struts2.views.java;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.opensymphony.xwork2.util.TextUtils;

public class Attributes extends LinkedHashMap<String,String> {

    public Attributes add(String key, String value) {
        return add(key, value, true);
    }
    
    public Attributes add(String key, String value, boolean encode) {
        put(key, (encode ? TextUtils.htmlEncode(value) : value));
        return this;
    }
    
    public Attributes addIfExists(String attrName, Object paramValue) {
        return addIfExists(attrName, paramValue, true);
    }
    
    public Attributes addIfExists(String attrName, Object paramValue, boolean encode) {
        if (paramValue != null) {
            String val = paramValue.toString();
            if (val.trim().length() > 0)
                put(attrName, (encode ? TextUtils.htmlEncode(val) : val));
        }
        return this;
    }
    
    public Attributes addIfTrue(String attrName, Object paramValue) {
        if (paramValue != null) {
            if ((paramValue instanceof Boolean && ((Boolean)paramValue).booleanValue()) ||
                (Boolean.valueOf(paramValue.toString()).booleanValue())) {
                put(attrName, attrName);
            }
        }
        return this;
    }
    
    public Attributes addDefaultToEmpty(String attrName, Object paramValue) {
        return addDefaultToEmpty(attrName, paramValue, true);
    }
    
    public Attributes addDefaultToEmpty(String attrName, Object paramValue, boolean encode) {
        if (paramValue != null) {
            String val = paramValue.toString();
            put(attrName, (encode ? TextUtils.htmlEncode(val) : val));
        } else {
            put(attrName, "");
        }
        return this;
    }
}
