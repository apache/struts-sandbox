/*
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
package org.apache.struts2.jst;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.commons.js2j.SugarWrapFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Script;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.inject.Inject;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;


/**
 * Result that uses Trimpath's Javascript Templates to render HTML
 */
public class JSTResult extends StrutsResultSupport {
    private ServletContext servletContext;
    private ScriptableObject rootScope;
    private Script processTemplateScript;
    private String contentType = "text/html";
    private String defaultEncoding;

    /**
     * Constructs a result and loads the common scripts
     *
     * @param ctx The servlet context
     * @param enc The default encoding for the templates
     */
    @Inject
    public JSTResult(@Inject ServletContext ctx,
                     @Inject(StrutsConstants.STRUTS_I18N_ENCODING) String enc) {
        this.servletContext = ctx;
        this.defaultEncoding = enc;

        // Find the embedded trimpath file
        String jst = null;
        try {
            jst = readStream(getClass().getResourceAsStream("/trimpath-template-1.0.38.js"));
        } catch (IOException e) {
            throw new RuntimeException("Missing or invalid trimpath template file", e);
        }

        // Loads the root context
        Context cx = Context.enter();
        try {
            rootScope = cx.initStandardObjects();

            // Load trimpath into the root scope
            cx.evaluateString(rootScope, jst, "trimpath", 1, null);

            // Compile the script used for processing templates
            processTemplateScript = cx.compileString("var template = TrimPath.parseTemplate(userTemplate, null);\n" +
                    "            if (template != null)\n" +
                    "                template.process(context, null);", "generate-template", 1, null);
        } finally {
            // Exit from the context.
            Context.exit();
        }
    }

    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        String output = processTemplate(finalLocation, invocation);
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType(getContentType());
        Writer writer = response.getWriter();
        writer.write(output);
        writer.flush();
    }

    String processTemplate(String finalLocation, ActionInvocation invocation) throws IOException {
        String template = readTemplate(finalLocation);
        String output = null;

        Context cx = Context.enter();
        try {

            // Create a new scope for this request
            Scriptable scope = cx.newObject(rootScope);
            scope.setPrototype(rootScope);
            scope.setParentScope(null);

            // Use this nice wrap faactory that adds support, among other things, for Maps as if they were Javascript maps
            cx.setWrapFactory(new SugarWrapFactory());

            // Set the user template and context into the scope
            Object wrappedOut = Context.javaToJS(template, scope);
            ScriptableObject.putProperty(scope, "userTemplate", wrappedOut);
            Object wrappedStack = Context.javaToJS(new ValueStackMap(invocation.getStack()), scope);
            ScriptableObject.putProperty(scope, "context", wrappedStack);

            // Process the template
            Object result = processTemplateScript.exec(cx, scope);

            // Convert the result to a String
            output = Context.toString(result);
        } finally {
            Context.exit();
        }
        return output;
    }

    /**
     * Reads a template from the servlet context, uses traditional file management with optional caching
     *
     * @param finalLocation The template path, relative to the servlet context
     * @return The template as a String
     * @throws IOException If the template cannot be read
     */
    private String readTemplate(String finalLocation) throws IOException {
        InputStream in = FileManager.loadFile(servletContext.getResource(finalLocation));
        return readStream(in);
    }

    /**
     * Read an input stream into a String
     *
     * @param in The input stream
     * @return The string result
     * @throws IOException If the stream cannot be read
     */
    private String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        Reader reader = new BufferedReader(new InputStreamReader(in, getEncoding()));
        int len = 0;
        while ((len = reader.read(buffer)) > 0) {
            sb.append(buffer, 0, len);
        }
        return sb.toString();
    }

    /**
     * Retrieve the encoding for this template.
     *
     * @return The encoding for loading templates
     */
    protected String getEncoding() {
        String encoding = defaultEncoding;
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    /**
     * @return The content type for the response
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType The http result content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Wraps the value stack in a Map, so Javascript can access it easily
     */
    static class ValueStackMap extends HashMap<String,Object> {
        private ValueStack stack;

        public ValueStackMap(ValueStack stack) {
            this.stack = stack;
        }

        @Override
        public Object get(Object o) {
            Object result = super.get(o);
            if (result == null)
                return stack.findValue(o.toString());
            else
                return result;
        }

        @Override
        public boolean containsKey(Object o) {
            boolean result = super.containsKey(o);
            if (!result)
                return stack.findValue(o.toString()) != null;
            else
                return result;
        }
    }
}
