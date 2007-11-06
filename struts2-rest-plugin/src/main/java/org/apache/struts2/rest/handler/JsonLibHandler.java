/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
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
package org.apache.struts2.rest.handler;

import java.io.*;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Handles JSON content using json-lib
 */
public class JsonLibHandler implements ContentTypeHandler {

    public void toObject(Reader in, Object target) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int len = 0;
        while ((len = in.read(buffer)) > 0) {
            sb.append(buffer, 0, len);
        }
        JSONObject jsonObject = JSONObject.fromObject(sb.toString());
        JSONObject.toBean(jsonObject, target, new JsonConfig());
    }

    public String fromObject(Object obj, String resultCode, Writer stream) throws IOException {
        if (obj != null) {
            JSONObject jsonObject = JSONObject.fromObject(obj);
            stream.write(jsonObject.toString());
        }
        return null;


    }

    public String getContentType() {
        return "text/javascript";
    }
    
    public String getExtension() {
        return "json";
    }
}