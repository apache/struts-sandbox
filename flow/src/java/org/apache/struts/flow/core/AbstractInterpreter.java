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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Abstract superclass for various scripting languages used by Cocoon
 * for flow control. Defines some useful behavior like the ability to
 * reload script files if they get modified (useful when doing
 * development), and passing the control to Cocoon's sitemap for
 * result page generation.
 * <p>
 * Flow intrepreters belonging to different sitemaps should be isolated. To achieve this,
 * class implements the {@link org.apache.avalon.framework.thread.SingleThreaded}. Since
 * the sitemap engine looks up the flow intepreter once at sitemap build time, this ensures
 * that each sitemap will use a different instance of this class. But that instance will
 * handle all flow calls for a given sitemap, and must therefore be thread safe.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @since March 15, 2002
 * @version CVS $Id: AbstractInterpreter.java 123711 2004-12-30 10:23:03Z cziegeler $
 */
public abstract class AbstractInterpreter implements Interpreter {

    // The instance ID of this interpreter, used to identify user scopes
    private String instanceID;

    /**
     * List of source locations that need to be resolved.
     */
    protected ArrayList needResolve = new ArrayList();

    protected ContinuationsManager continuationsMgr;

    /**
     * Whether reloading of scripts should be done. Specified through
     * the "reload-scripts" attribute in <code>flow.xmap</code>.
     */
    protected boolean reloadScripts;

    /**
     * Interval between two checks for modified script files. Specified
     * through the "check-time" XML attribute in <code>flow.xmap</code>.
     */
    protected long checkTime;
    
    public AbstractInterpreter() {
         try {
            continuationsMgr = Factory.getContinuationsManager();
        } catch (Exception e) {
            Factory.getLogger().error(e);
        }
    }

    /**
     * Set the unique ID for this interpreter, which can be used to distinguish user value scopes
     * attached to the session.
     */
    public void setInterpreterID(String interpreterID) {
        this.instanceID = interpreterID;
    }

    /**
     * Get the unique ID for this interpreter, which can be used to distinguish user value scopes
     * attached to the session.
     *
     * @return a unique ID for this interpreter
     */
    public String getInterpreterID() {
        return this.instanceID;
    }

    /**
     *  Sets the interval between when the script should be looked at to see if
     *  it needs to be reloaded
     *
     *@param  time  The interval time in milliseconds
     */
    public void setCheckTime(long time) {
        checkTime = time;
    }


    /**
     *  Sets whether to try to reload modified scripts or not
     *
     *@param  val  True to reload
     */
    public void setReloadScripts(boolean val) {
        reloadScripts = val;
    }

    /**
     * Registers a source file with the interpreter. Using this method
     * an implementation keeps track of all the script files which are
     * compiled. This allows them to reload the script files which get
     * modified on the file system.
     *
     * <p>The parsing/compilation of a script file by an interpreter
     * happens in two phases. In the first phase the file's location is
     * registered in the <code>needResolve</code> array.
     *
     * <p>The second is possible only when a Cocoon
     * <code>Environment</code> is passed to the Interpreter. This
     * allows the file location to be resolved using Cocoon's
     * <code>SourceFactory</code> class.
     *
     * <p>Once a file's location can be resolved, it is removed from the
     * <code>needResolve</code> array and placed in the
     * <code>scripts</code> hash table. The key in this hash table is
     * the file location string, and the value is a
     * DelayedRefreshSourceWrapper instance which keeps track of when
     * the file needs to re-read.
     *
     * @param source the location of the script
     *
     * @see org.apache.cocoon.environment.Environment
     * @see org.apache.cocoon.components.source.impl.DelayedRefreshSourceWrapper
     */
    public void register(String source) {
        synchronized (this) {
            needResolve.add(source);
        }
    }
}
