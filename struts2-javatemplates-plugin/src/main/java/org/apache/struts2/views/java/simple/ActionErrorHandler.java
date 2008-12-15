/*
 * $Id: SelectHandler.java 726340 2008-12-14 02:45:05Z musachy $
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
package org.apache.struts2.views.java.simple;

import org.apache.struts2.views.java.TagGenerator;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.util.MakeIterator;

import java.io.IOException;
import java.util.Map;
import java.util.Iterator;

public class ActionErrorHandler extends AbstractTagHandler implements TagGenerator {
    @Override
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Object errorsObj = findValue("actionErrors");

        if (errorsObj != null) {
            Iterator itt = MakeIterator.convert(errorsObj);
            if (itt.hasNext()) {
                start("ul", null);
                while (itt.hasNext()) {
                    String error = (String) itt.next();

                    //li for each error
                    start("li", null);

                    //span for error
                    Attributes attrs = new Attributes();
                    attrs.addIfExists("style", params.get("cssStyle"))
                            .add("class", params.containsKey("cssClass") ? (String) params.get("cssClass") : "errorMessage");

                    start("span", attrs);
                    characters(error);
                    end("span");
                    end("li");

                }
                end("ul");
            }
        }
    }
}
