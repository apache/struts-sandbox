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
package org.apache.ti.compiler.internal.model;


/**
 * Represents an Action Output (NetUI, not a Struts concept) that will be attached to a forward ({@link XWorkResultModel}).
 */
public class ActionOutputModel {

    String _name;
    String _type;
    boolean _isNullable;

    protected ActionOutputModel() {
    }

    public ActionOutputModel(String name, String type, boolean isNullable) {
        _name = name;
        _type = type;
        _isNullable = isNullable;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }

    public boolean getNullable() {
        return _isNullable;
    }

    public void setName(String name) {
        assert name != null;
        _name = name;
    }

    public void setType(String type) {
        assert type != null;
        _type = type;
    }

    public void setNullable(boolean nullable) {
        _isNullable = nullable;
    }
}
