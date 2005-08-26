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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * A mutable class for creating URIs that can be set to "frozen" such
 * that it becomes immutable. After this class is frozen, any calls to
 * methods to set the data components of the URI will throw
 */
public class FreezableMutableURI extends MutableURI {

    private boolean _frozen = false;

    /**
     * Constructs a <code>FreezableMutableURI</code>.
     */
    public FreezableMutableURI() {
    }

    /**
     * Constructs a <code>FreezableMutableURI</code>.
     *
     * @param uriString the string to be parsed into a URI
     * @param encoded   Flag indicating whether the string is
     *                  already encoded.
     */
    public FreezableMutableURI(String uriString, boolean encoded) throws URISyntaxException {
        super(uriString, encoded);
    }

    /**
     * Constructs a <code>FreezableMutableURI</code>.
     *
     * @param scheme   the name of the protocol to use
     * @param userInfo the username and password
     * @param host     the name of the host
     * @param port     the port number on the host
     * @param path     the file on the host
     * @param query    the query part of this URI
     * @param fragment the fragment part of this URI (internal reference in the URL)
     */
    public FreezableMutableURI(String scheme, String userInfo, String host, int port,
                               String path, String query, String fragment) {
        super(scheme, userInfo, host, port, path, query, fragment);
    }

    /**
     * Constructs a <code>FreezableMutableURI</code>.
     *
     * @param uri the initial value for this mutable URI
     */
    public FreezableMutableURI(URI uri) {
        super(uri);
    }

    /**
     * Constructs a <code>FreezableMutableURI</code>.
     * <p/>
     * <p> This is just a convenience constructor that functions the same as
     * {@link #FreezableMutableURI(URI)} constructor with
     * {@link java.net.URL#toURI()} as the argument. </p>
     * <p/>
     * <p>Note, any URL instance that complies with RFC 2396 can be converted
     * to a URI. However, some URLs that are not strictly in compliance
     * can not be converted to a URI. See {@link java.net.URL} </p>
     *
     * @param url the initial value for this mutable URI
     * @throws URISyntaxException if this URL is not formatted strictly
     *                            to RFC2396 and cannot be converted to a URI.
     * @see java.net.URL#toURI()
     */
    public FreezableMutableURI(URL url) throws URISyntaxException {
        super(url);
    }

    public final boolean isFrozen() {
        return _frozen;
    }

    /**
     * Sets a flag indicating that the URI is immutable (or not).
     *
     * @param frozen flag to indicate if the URI is now immutable or not.
     */
    public void setFrozen(boolean frozen) {
        this._frozen = frozen;
    }

    private void testFrozen() {
        if (_frozen) {
            throw new IllegalStateException("Cannot modify the URI data. This instance was set to be immutable.");
        }
    }

    /**
     * Reset the value of the <code>FreezableMutableURI</code>.
     * <p/>
     * <p> This method can also be used to clear the <code>FreezableMutableURI</code>.
     *
     * @param uriString the string to be parsed into a URI
     * @param encoded   Flag indicating whether the string is
     *                  already encoded.
     */

    public void setURI(String uriString, boolean encoded) throws URISyntaxException {
        testFrozen();
        super.setURI(uriString, encoded);
    }

    /**
     * Set the encoding used when adding unencoded parameters.
     *
     * @param encoding
     */

    public void setEncoding(String encoding) {
        testFrozen();
        super.setEncoding(encoding);
    }

    /**
     * Sets the protocol/scheme.
     *
     * @param scheme protocol/scheme
     */

    public void setScheme(String scheme) {
        testFrozen();
        super.setScheme(scheme);
    }

    /**
     * Sets the userInfo.
     *
     * @param userInfo userInfo
     */

    public void setUserInfo(String userInfo) {
        testFrozen();
        super.setUserInfo(userInfo);
    }

    /**
     * Sets the host.
     *
     * @param host host
     */

    public void setHost(String host) {
        testFrozen();
        super.setHost(host);
    }

    /**
     * Sets the port.
     *
     * @param port port
     */

    public void setPort(int port) {
        testFrozen();
        super.setPort(port);
    }

    /**
     * Sets the path.
     *
     * @param path path
     */

    public void setPath(String path) {
        testFrozen();
        super.setPath(path);
    }

    /**
     * Sets (and resets) the query string.
     * This method assumes that the query is already encoded and
     * the parameter delimiter is the '&amp;' character.
     *
     * @param query Query string
     */

    public void setQuery(String query) {
        testFrozen();
        super.setQuery(query);
    }

    /**
     * Add a parameter for the query string.
     * <p> If the encoded flag is true then this method assumes that
     * the name and value do not need encoding or are already encoded
     * correctly. Otherwise, it translates the name and value with the
     * character encoding of this URI and adds them to the set of
     * parameters for the query. If the encoding for this URI has
     * not been set, then the default encoding used is "UTF-8". </p>
     * <p> Multiple values for the same parameter can be set by
     * calling this method multiple times with the same name. </p>
     *
     * @param name    name
     * @param value   value
     * @param encoded Flag indicating whether the names and values are
     *                already encoded.
     */

    public void addParameter(String name, String value, boolean encoded) {
        testFrozen();
        super.addParameter(name, value, encoded);
    }

    /**
     * Add a parameter to the query string.
     * <p> If the encoded flag is true then this method assumes that
     * the name and value do not need encoding or are already encoded
     * correctly. Otherwise, it translates the name and value with the
     * character encoding of this URI and adds them to the set of
     * parameters for the query. If the encoding for this URI has
     * not been set, then the default encoding used is "UTF-8". </p>
     *
     * @param newParams the map of new parameters to add to the URI
     * @param encoded   Flag indicating whether the names and values are
     *                  already encoded.
     */

    public void addParameters(Map newParams, boolean encoded) {
        testFrozen();
        super.addParameters(newParams, encoded);
    }

    /**
     * Removes the given parameter.
     *
     * @param name name
     */

    public void removeParameter(String name) {
        testFrozen();
        super.removeParameter(name);
    }

    /**
     * Sets the fragment.
     *
     * @param fragment fragment
     */

    public void setFragment(String fragment) {
        testFrozen();
        super.setFragment(fragment);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FreezableMutableURI)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final FreezableMutableURI freezableMutableURI = (FreezableMutableURI) o;

        if (_frozen != freezableMutableURI._frozen) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (_frozen ? 1 : 0);
        return result;
    }
}

