/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.flow.sugar;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

import java.io.Serializable;

import java.util.Map;

/**
 * Wrap a DynaBean for JavaScript.  Currently only supports named,
 * and not mapped or indexed properties for Javascript shortcuts.  
 * Mapped and indexed properties are still available through their 
 * get() methods.  
 */
public class ScriptableDynaBean extends JavaObjectWrapper implements Scriptable, Wrapper, Serializable {

    private DynaBean bean;

    public ScriptableDynaBean() {
        super();
    }

    public ScriptableDynaBean(DynaBean bean) {
        this.bean = bean;
    }
    
    public ScriptableDynaBean(Scriptable scope, Object javaObject, Class staticType, Map functions) {
        super(scope, javaObject, staticType, functions);
        if (javaObject instanceof DynaBean) {
            this.bean = (DynaBean)javaObject;
        } else {
            throw new IllegalArgumentException("Passed object "+javaObject+" is not an instance of DynaBean");
        }
    }

    public String getClassName() {
        return "DynaBean";
    }

    public boolean has(String name, Scriptable start) {
        return (super.has(name, start) || has(name));
    }

    /**
     * no numeric properties
     */
    public boolean has(int index, Scriptable start) {
        return false;
    }

    public Object get(String name, Scriptable start) {
        if (super.has(name, start)) {
            return super.get(name, start);
        } else if (has(name)) {
            return wrap(this.bean.get(name), start);
        } else {
            return Scriptable.NOT_FOUND;
        }
    }
    
    private boolean has(String name) {
        try {
            Object val = this.bean.get(name);
            return (val != null);
        } catch (IllegalArgumentException ex) {
            // Do nothing as we don't care if it complains
        }
        return false;
    }

    public Object get(int index, Scriptable start) {
        return NOT_FOUND;
    }

    public void put(String name, Scriptable start, Object value) {
        if (value instanceof NativeJavaObject) {
            value = ((NativeJavaObject)value).unwrap();
        }
        bean.set(name, value);
    }

    public void put(int index, Scriptable start, Object value) {
    }

    public void delete(String id) {
        bean.set(id, null);
    }

    public void delete(int index) {
    }

    public Scriptable getPrototype() {
        return prototype;
    }

    public void setPrototype(Scriptable prototype) {
        this.prototype = prototype;
    }

    public Scriptable getParentScope() {
        return parent;
    }

    public void setParentScope(Scriptable parent) {
        this.parent = parent;
    }

    /**
     *  The id array that is returned is of class DynaProperties,
     *  rather than the perhaps expected String.
     */
    public Object[] getIds() {
        DynaProperty[] props = this.bean.getDynaClass().getDynaProperties();
        String[] ids = new String[props.length];
        for (int x=0; x < props.length; x++) {
            ids[x] = props[x].getName();
        }
        return ids;
    }

    public Object getDefaultValue(Class typeHint) {
        return this.bean.toString();
    }

    public boolean hasInstance(Scriptable value) {
        Scriptable proto = value.getPrototype();
        while (proto != null) {
            if (proto.equals(this)) 
                return true;
            proto = proto.getPrototype();
        }

        return false;
    }

    /**
     * Return the DynaBean that is wrapped by this class.
     */
    public Object unwrap() {
        return this.bean;
    }

}
