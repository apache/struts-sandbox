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
package org.apache.ti.core.urls;

/**
 * A JavaBean that encapsulates the data needed to write out
 * a string form of a MutableURI.
 */
public class URIContext {

    /**
     * Character encoding used for the URI.
     */
    private boolean _useAmpEntity = true;

    /**
     * Constructs a <code>URIContext</code>.
     */
    public URIContext() {
    	// do nothing
    }

    /**
     * Indicate that the query of the URI should be written using the
     * &quot;&amp;amp;&quot; entity, rather than the '&amp;' character,
     * to separate parameters.
     *
     * @return true if a URI should have the &quot;&amp;amp;&quot; entity
     *         separating query parameters. Otherwise, false indicates that
     *         it is OK to use the '&amp;' character.
     */
    public boolean useAmpEntity() {
        return _useAmpEntity;
    }

    /**
     * Set the flag indicating that the query of the URI should be written
     * with the &quot;&amp;amp;&quot; entity, rather than the '&amp;' character,
     * to separate parameters.
     *
     * @param useAmpEntity defines whether or not to use the &quot;&amp;amp;&quot; entity
     */
    public void setUseAmpEntity(boolean useAmpEntity) {
        _useAmpEntity = useAmpEntity;
    }

}
