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
package org.apache.ti.script.common.bundle;

import org.apache.ti.util.internal.InternalStringBuilder;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 */
class ResourceBundleNode
        extends BundleNode {

    private ResourceBundle _bundle;

    ResourceBundleNode(ResourceBundle resourceBundle) {
        _bundle = resourceBundle;
    }

    public boolean containsKey(String key) {
        return _bundle != null && _bundle.getString(key) != null;
    }

    public String getString(String key) {
        return _bundle != null ? _bundle.getString(key) : null;
    }

    public Enumeration getKeys() {
        return _bundle != null ? _bundle.getKeys() : null;
    }

    public String toString() {
        InternalStringBuilder sb = new InternalStringBuilder();
        sb.append("ResourceBundleNode ");
        Enumeration keys = getKeys();
        if (keys != null) {
            boolean first = true;
            sb.append("{");
            while (keys.hasMoreElements()) {
                if (!first)
                    sb.append(",");
                else
                    first = false;

                String key = (String) keys.nextElement();
                sb.append(key);
                sb.append("=");
                sb.append(getString(key));
            }
            sb.append("}");
        }
        return sb.toString();
    }
}
