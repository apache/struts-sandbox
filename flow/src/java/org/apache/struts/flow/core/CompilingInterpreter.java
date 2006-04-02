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
package org.apache.struts.flow.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.struts.flow.core.source.Source;
import org.apache.struts.flow.core.source.SourceResolver;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

/**
 * This is a base class for all interpreters compiling the scripts
 *
 * @author <a href="mailto:ovidiu@apache.org">Ovidiu Predescu</a>
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: CompilingInterpreter.java 55850 2004-10-28 13:43:12Z vgritsenko $
 */
public abstract class CompilingInterpreter
        extends AbstractInterpreter {

    /**
     * A source resolver
     */
    protected SourceResolver sourceresolver;

    /**
     * Mapping of String objects (source uri's) to ScriptSourceEntry's
     */
    protected Map compiledScripts = new HashMap();

    
    public CompilingInterpreter() {
        super();
    }
    
    public void setSourceResolver(SourceResolver r) {
        this.sourceresolver = r;
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.compiledScripts != null) {
            Iterator i = this.compiledScripts.values().iterator();
            while (i.hasNext()) {
                ScriptSourceEntry current = (ScriptSourceEntry)i.next();
                this.sourceresolver.release(current.getSource());
            }
            this.compiledScripts = null;
        }
       this.sourceresolver = null;
    }

    /**
     * TODO - This is a little bit strange, the interpreter calls
     * get Script on the ScriptSourceEntry which in turn
     * calls compileScript on the interpreter. I think we
     * need more refactoring here.
     */
    protected abstract Script compileScript(Context context,
                                            Scriptable scope,
                                            Source source) throws Exception;

    protected class ScriptSourceEntry {
        final private Source source;
        private Script script;
        private long compileTime;

        public ScriptSourceEntry(Source source) {
            this.source = source;
        }

        public ScriptSourceEntry(Source source, Script script, long t) {
            this.source = source;
            this.script = script;
            this.compileTime = t;
        }

        public Source getSource() {
            return source;
        }

        public Script getScript(Context context, Scriptable scope,
                                boolean refresh, CompilingInterpreter interpreter)
        throws Exception {
            if (refresh) {
                source.refresh();
            }
            if (script == null || compileTime < source.getLastModified()) {
                script = interpreter.compileScript(context, scope, source);
                compileTime = source.getLastModified();
            }
            return script;
        }
    }
}
