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

import org.mozilla.javascript.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.beanutils.DynaBean;

/**
 * Wraps Java objects by adding support for function extensions, which are
 * functions that extend existing Java objects at the Rhino level.
 */
public class SugarWrapFactory extends WrapFactory {
    
    private List functionRegistry = new ArrayList();
    private Map functionMappings = new HashMap();
    
    public SugarWrapFactory() {
        super();
        
        // Add default methods
        addExtensionFunctions(CollectionExtensions.class);
        addExtensionFunctions(ListExtensions.class);
        addExtensionFunctions(FileExtensions.class);
        addExtensionFunctions(InputStreamExtensions.class);
    }
    
    public void addExtensionFunction(Class cls, String name, Method func) {
        int modifier = func.getModifiers();
        if (Modifier.isStatic(modifier) && Modifier.isPublic(modifier)) {
            ExtensionEntry entry = new ExtensionEntry(cls, name, func);
            functionRegistry.add(entry);
        } else {
            throw new IllegalArgumentException("Method "+func+" must be static and public");
        }
    }
    
    public void addExtensionFunctions(Class holder) {
        Method[] methods = holder.getDeclaredMethods();
        for (int x=0; x<methods.length; x++) {
            int modifier = methods[x].getModifiers();
            if (Modifier.isStatic(modifier) && Modifier.isPublic(modifier)) {
                String name = methods[x].getName();
                Class target = methods[x].getParameterTypes()[0];
                ExtensionEntry entry = new ExtensionEntry(target, name, methods[x]);
                functionRegistry.add(entry);
            }
        }
        
    }

    /**
     * Wrap Java object as Scriptable instance to allow full access to its
     * methods and fields from JavaScript.
     * <p>
     * {@link #wrap(Context, Scriptable, Object, Class)} and
     * {@link #wrapNewObject(Context, Scriptable, Object)} call this method
     * when they can not convert <tt>javaObject</tt> to JavaScript primitive
     * value or JavaScript array.
     * @param cx the current Context for this thread
     * @param scope the scope of the executing script
     * @param javaObject the object to be wrapped
     * @param staticType type hint. If security restrictions prevent to wrap
                object based on its class, staticType will be used instead.
     * @return the wrapped value which shall not be null
     */
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
        Object javaObject, Class staticType) {
            
        Map map = getExtensionFunctions(javaObject.getClass());
        Scriptable wrap = null;
        if (javaObject instanceof Map) {
            wrap = new ScriptableMap(scope, javaObject, staticType, map);
        } else if (javaObject instanceof DynaBean) {
            wrap = new ScriptableDynaBean(scope, javaObject, staticType, map);
        } else if (javaObject instanceof List) {
            wrap = new ScriptableList(scope, javaObject, staticType, map);
        } else {
            wrap = new JavaObjectWrapper(scope, javaObject, staticType, map);
        }
        return wrap;
    }
    
    private Map getExtensionFunctions(Class cls) {
        Map map = (Map)functionMappings.get(cls);
        ExtensionEntry entry;
        if (map == null) {
            map = new HashMap();
            for (Iterator i = functionRegistry.iterator(); i.hasNext(); ) {
                entry = (ExtensionEntry)i.next();
                if (entry.clazz.isAssignableFrom(cls)) {
                    map.put(entry.name, entry.function);
                }
            }
        }
        return map;
    }
    
    /**
     *  Test driver.  Also used for generating javascript javadocs.
     */
    public static final void main(String[] args) throws Exception {
        Context cx = Context.enter();
        try {
            cx.setWrapFactory(new SugarWrapFactory());
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            //Scriptable scope = cx.initStandardObjects();
            Scriptable scope = new ImporterTopLevel(cx);

        
            FileReader reader = new FileReader(args[0]);
            
            // Set up "arguments" in the global scope to contain the command
            // line arguments after the name of the script to execute
            Object[] array = args;
            if (args.length > 0) {
                int length = args.length - 1;
                array = new Object[length];
                System.arraycopy(args, 1, array, 0, length);
            }
            Scriptable argsObj = cx.newArray(scope, array);
            scope.put("arguments", scope, argsObj);
            Object result = cx.evaluateReader(scope, reader, args[0], 1, null);
        
            // Convert the result to a string and print it.
            System.err.println(cx.toString(result));
        } finally {
            // Exit from the context.
            Context.exit();
        }     
    }
    
    class ExtensionEntry {
        public Class clazz;
        public String name;
        public Method function;
        
        public ExtensionEntry(Class cls, String name, Method func) {
            this.clazz = cls;
            this.name = name;
            this.function = func;
        }
    }

}
