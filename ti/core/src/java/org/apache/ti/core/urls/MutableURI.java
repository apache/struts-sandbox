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

import org.apache.ti.core.URLCodec;
import org.apache.ti.util.internal.InternalStringBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Mutable class for creating URIs.
 * <p/>
 * <p> There is little checking that an instance of this class produces a legal
 * URI reference as defined by <a href="http://www.ietf.org/rfc/rfc2396.txt">
 * <i>RFC&nbsp;2396: Uniform Resource Identifiers (URI): Generic Syntax</i></a>.
 * The minimal checking for syntax is on constructors that take a String
 * representation or the URI, a {@link URI}, or a {@link URL}.
 * To avoid the cost of continually checking the syntax, it is up to the
 * user to ensure that the components are set correctly. </p>
 * <p/>
 * <p> The setters of this class also assume that the data components are
 * already encoded correctly for the given encoding of this URI, unless noted
 * otherwise as in the methods to add unecoded parameters to the query.
 * Then this class will handle the encoding.
 * See {@link #addParameter( String name, String value, boolean encoded )}
 * and {@link #addParameters( Map newParams, boolean encoded )}
 * </p>
 * <p/>
 * <p> There is a static convenience method in this class so callers can
 * easily encode unencoded components before setting it in this object. </p>
 * <p/>
 * TODO... We need to implement some conditions for opaque URIs like mailto, etc.
 * to determine what to do about values of path when (path == null) => opaque and
 * URI.getPath() would return "" for non-opaue URIs.
 */
public class MutableURI {

    /**
     * Value used to set the port as undefined.
     */
    public static final int UNDEFINED_PORT = -1;

    /**
     * Value used to set the encoding as undefined.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Character encoding used for the URI.
     */
    private String _encoding;

    /**
     * Protocol scheme.
     */
    private String _scheme;

    /**
     * User information "may consist of a user name and, optionally,
     * scheme-specific information about how to gain authorization to
     * access the server.
     */
    private String _userInfo;

    /**
     * Host
     */
    private String _host;

    /**
     * Port
     */
    private int _port = UNDEFINED_PORT;

    /**
     * Path
     */
    private String _path;

    /**
     * Query parameters
     */
    private LinkedHashMap/*< String, List< String > >*/ _params;

    /**
     * Fragment
     */
    private String _fragment;

    /* Separators for query parameters */
    private static final String AMP_ENTITY = "&amp;";
    private static final String AMP_CHAR = "&";

    private static final Map/*< String, List< String > >*/ EMPTY_MAP =
            Collections.unmodifiableMap(new HashMap/*< String, List< String > >*/());

    /**
     * Constructs a <code>MutableURI</code>.
     */
    public MutableURI() {
    }

    /**
     * Constructs a <code>MutableURI</code>.
     *
     * @param uriString the string to be parsed into a URI
     * @param encoded   Flag indicating whether the string is
     *                  already encoded.
     */
    public MutableURI(String uriString, boolean encoded) throws URISyntaxException {
        assert uriString != null : "The uri cannot be null.";

        if (uriString == null) {
            throw new IllegalArgumentException("The URI cannot be null.");
        }

        URI uri = null;

        if (encoded) {
            // Get (parse) the components using java.net.URI
            uri = new URI(uriString);
        } else {
            // Parse, then encode this string into its components using URI
            uri = encodeURI(uriString);
        }

        setURI(uri);
    }

    /**
     * Constructs a <code>MutableURI</code>. Assumes the individual components
     * are already encoded and escaped.
     *
     * @param scheme   the name of the protocol to use
     * @param userInfo the username and password
     * @param host     the name of the host
     * @param port     the port number on the host
     * @param path     the file on the host
     * @param query    the query part of this URI
     * @param fragment the fragment part of this URI (internal reference in the URL)
     */
    public MutableURI(String scheme, String userInfo, String host, int port,
                      String path, String query, String fragment) {
        setScheme(scheme);
        setUserInfo(userInfo);
        setHost(host);
        setPort(port);
        setPath(path);
        setQuery(query);
        setFragment(fragment);
    }

    /**
     * Constructs a <code>MutableURI</code>.
     *
     * @param uri the initial value for this mutable URI
     */
    public MutableURI(URI uri) {
        assert uri != null : "The URI cannot be null.";

        if (uri == null) {
            throw new IllegalArgumentException("The URI cannot be null.");
        }

        setURI(uri);
    }

    /**
     * Constructs a <code>MutableURI</code>.
     * <p/>
     * <p> This is just a convenience constructor that functions the same as
     * {@link #MutableURI(URI)} constructor with
     * {@link java.net.URL#toURI()} as the argument. </p>
     * <p/>
     * <p>Note, any URL instance that complies with RFC 2396 can be converted
     * to a URI. However, some URLs that are not strictly in compliance
     * can not be converted to a URI. See {@link java.net.URL} </p>
     *
     * @param url the initial value for this mutable URI
     * @throws URISyntaxException if this URL is not formatted strictly
     *                            to RFC2396 and cannot be converted to a URI.
     */
    public MutableURI(URL url) throws URISyntaxException {
        assert url != null : "The URL cannot be null.";

        if (url == null) {
            throw new IllegalArgumentException("The URL cannot be null.");
        }

        URI uri = new URI(url.toString());
        setURI(uri);
    }

    /**
     * Set the value of the <code>MutableURI</code>.
     * <p/>
     * <p> This method can also be used to clear the <code>MutableURI</code>. </p>
     *
     * @param uriString the string to be parsed into a URI
     * @param encoded   Flag indicating whether the string is
     *                  already encoded.
     */
    public void setURI(String uriString, boolean encoded) throws URISyntaxException {
        if (uriString == null) {
            setScheme(null);
            setUserInfo(null);
            setHost(null);
            setPort(UNDEFINED_PORT);
            setPath(null);
            setQuery(null);
            setFragment(null);
        } else {
            URI uri = null;

            if (encoded) {
                // Get (parse) the components using java.net.URI
                uri = new URI(uriString);
            } else {
                // Parse, then encode this string into its components using URI
                uri = encodeURI(uriString);
            }

            setURI(uri);
        }
    }

    /**
     * Set the value of the <code>MutableURI</code>.
     * <p/>
     * <p> This method can also be used to clear the <code>MutableURI</code>. </p>
     *
     * @param uri the URI
     */
    public void setURI(URI uri) {
        setScheme(uri.getScheme());
        setUserInfo(uri.getRawUserInfo());
        setHost(uri.getHost());
        setPort(uri.getPort());
        setPath(uri.getRawPath());
        setQuery(uri.getRawQuery());
        setFragment(uri.getRawFragment());
    }

    /**
     * Set the encoding used when adding unencoded parameters.
     *
     * @param encoding
     */
    public void setEncoding(String encoding) {
        _encoding = encoding;
    }

    /**
     * Returns the character encoding that is used when adding unencoded parameters.
     *
     * @return encoding
     */
    public String getEncoding() {
        return _encoding;
    }

    /**
     * Sets the protocol/scheme.
     *
     * @param scheme protocol/scheme
     */
    public void setScheme(String scheme) {
        _scheme = null;
        if (scheme != null && scheme.length() > 0) {
            _scheme = scheme;
        }
    }

    /**
     * Returns the protocol/scheme. If no protocol was previously set,
     * returns null.
     *
     * @return protocol/scheme
     */
    public String getScheme() {
        return _scheme;
    }

    /**
     * Sets the userInfo. Assumes this component is already escaped.
     *
     * @param userInfo userInfo
     */
    public void setUserInfo(String userInfo) {
        _userInfo = null;
        if (userInfo != null && userInfo.length() > 0) {
            _userInfo = userInfo;
        }
    }

    /**
     * Returns the userInfo. If no host was previously set, returns
     * null.
     *
     * @return userInfo
     */
    public String getUserInfo() {
        return _userInfo;
    }

    /**
     * Sets the host.
     *
     * @param host host
     */
    public void setHost(String host) {
        _host = null;
        if (host != null && host.length() > 0) {
            //
            // Here's some very minimal support for IPv6 addresses.
            // If the literal IPv6 address is not enclosed in square brackets
            // then add them.
            //
            boolean needBrackets = ((host.indexOf(':') >= 0)
                    && !host.startsWith("[")
                    && !host.endsWith("]"));

            if (needBrackets) {
                _host = '[' + host + ']';
            } else {
                _host = host;
            }
        }

        if (_host == null) {
            setUserInfo(null);
            setPort(UNDEFINED_PORT);
        }
    }

    /**
     * Returns the host. If no host was previously set, returns
     * null.
     *
     * @return host
     */
    public String getHost() {
        return _host;
    }

    /**
     * Sets the port.
     *
     * @param port port
     */
    public void setPort(int port) {
        assert (port >= 0 && port <= 65535) || (port == UNDEFINED_PORT)
                : "Invalid port" ;

        if ((port > 65535) || (port < 0 && port != UNDEFINED_PORT)) {
            throw new IllegalArgumentException("A port must be between 0 and 65535 or equal to "
                    + UNDEFINED_PORT + ".");
        }

        _port = port;
    }

    /**
     * Returns the port. If no port was previously set, returns
     * null.
     *
     * @return port
     */
    public int getPort() {
        return _port;
    }

    /**
     * Sets the path. Assumes this component is already escaped.
     *
     * @param path path
     */
    public void setPath(String path) {
        // Note that an empty path is OK
        if (path == null) {
            _path = null;
            setQuery(null);
            setFragment(null);
        } else {
            _path = path;
        }
    }

    /**
     * Returns the path.
     *
     * @return path
     */
    public String getPath() {
        return _path;
    }

    /**
     * Sets (and resets) the query string.
     * This method assumes that the query is already encoded and
     * escaped.
     *
     * @param query Query string
     */
    public void setQuery(String query) {
        _params = null;

        if (query == null || query.length() == 0) {
            return;
        }

        for (StringTokenizer tok = new StringTokenizer(query, "&"); tok.hasMoreElements();) {
            String queryItem = tok.nextToken();

            if (queryItem.startsWith("amp;")) {
                queryItem = queryItem.substring(4);
            }

            int eq = queryItem.indexOf('=');
            if (eq != -1) {
                addParameter(queryItem.substring(0, eq), queryItem.substring(eq + 1), true);
            } else {
                addParameter(queryItem, null, true);
            }
        }
    }

    /**
     * Returns the query string (encoded/escaped).
     * <p/>
     * <p> The context states whether or not to use the default delimiter,
     * usually the &quot;&amp;amp;&quot; entity, to separate the parameters.
     * Otherwise, the &quot;&amp;&quot; character is used. </p>
     *
     * @param uriContext has property indicating if we use the HTML Amp entity
     *                   to separate the query parameters.
     * @return encoded query string.
     */
    public String getQuery(URIContext uriContext) {
        if (_params == null || _params.isEmpty()) {
            return null;
        }

        String paramSeparator = AMP_ENTITY;
        if (uriContext == null) {
            uriContext = getDefaultContext();
        }

        if (!uriContext.useAmpEntity()) {
            paramSeparator = AMP_CHAR;
        }

        InternalStringBuilder query = new InternalStringBuilder(64);
        boolean firstParam = true;
        for (Iterator i = _params.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();

            for (Iterator j = ((List) _params.get(name)).iterator(); j.hasNext();) {
                String value = (String) j.next();

                if (firstParam) {
                    firstParam = false;
                } else {
                    query.append(paramSeparator);
                }

                query.append(name);

                if (value != null) {
                    query.append('=').append(value);
                }
            }
        }

        return query.toString();
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
        if (name == null) throw new IllegalArgumentException("A parameter name may not be null.");

        if (!encoded) {
            name = encode(name);
            value = encode(value);
        }

        if (_params == null) _params = new LinkedHashMap/*< String, List< String > >*/();
        List/*< String >*/ values = (List) _params.get(name);

        if (values == null) {
            values = new ArrayList/*< String >*/();
            _params.put(name, values);
        }

        values.add(value);
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
        if (newParams == null) {
            throw new IllegalArgumentException("Cannot add null map of parameters.");
        }

        if (newParams.size() == 0) {
            return;
        }

        if (_params == null) {
            _params = new LinkedHashMap/*< String, List< String > >*/();
        }

        Iterator keys = newParams.keySet().iterator();
        while (keys.hasNext()) {
            String name = (String) keys.next();
            String encodedName = name;

            if (!encoded) {
                encodedName = encode(name);
            }

            List/*< String >*/ values = (List) _params.get(encodedName);
            if (values == null) {
                values = new ArrayList/*< String >*/();
                _params.put(encodedName, values);
            }

            Object newValue = newParams.get(name);
            if (newValue == null) {
                values.add(null);
            } else if (newValue instanceof String) {
                addValue(values, (String) newValue, encoded);
            } else if (newValue instanceof String[]) {
                String newValues[] = (String[]) newValue;
                for (int i = 0; i < newValues.length; i++) {
                    addValue(values, newValues[i], encoded);
                }
            } else if (newValue instanceof List) {
                Iterator newValues = ((List) newValue).iterator();
                while (newValues.hasNext()) {
                    addValue(values, newValues.next().toString(), encoded);
                }
            } else /* Convert other objects to a string */ {
                addValue(values, newValue.toString(), encoded);
            }
        }
    }

    private void addValue(List/*< String >*/ list, String value, boolean encoded) {
        if (!encoded) {
            value = encode(value);
        }

        list.add(value);
    }

    /**
     * Returns the value of the parameter. If the parameter has
     * several values, returns the first value.
     *
     * @param name name of the parameter
     * @return value of the given parameter name (or just the first in the list
     *         if there are multiple values for the given name)
     */
    public String getParameter(String name) {
        if (_params == null) {
            return null;
        }

        List/*< String >*/ values = (List) _params.get(name);
        if (values != null && values.size() > 0) {
            return (String) values.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns the values of the given parameter.
     *
     * @param name name of the parameter
     * @return an unmodifable {@link java.util.List} of values for the given parameter name
     */
    public List/*< String >*/ getParameters(String name) {
        if (_params == null) {
            return Collections.EMPTY_LIST;
        } else {
            List/*< String >*/ values = (List) _params.get(name);

            if (values == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections.unmodifiableList(values);
            }
        }
    }

    /**
     * Returns an unmodifiable Map of all parameters.
     *
     * @return an unmodifiable {@link java.util.Map} of names and values for all parameters
     */
    public Map/*< String, List< String > >*/ getParameters() {
        if (_params == null) {
            return EMPTY_MAP;
        } else {
            return Collections.unmodifiableMap(_params);
        }
    }

    /**
     * Removes the given parameter.
     *
     * @param name name
     */
    public void removeParameter(String name) {
        if (_params == null) {
            return;
        }

        _params.remove(name);
    }

    /**
     * Sets the fragment.
     *
     * @param fragment fragment
     */
    public void setFragment(String fragment) {
        _fragment = null;
        if (fragment != null && fragment.length() > 0) {
            _fragment = fragment;
        }
    }

    /**
     * Returns the fragment.
     *
     * @return fragment
     */
    public String getFragment() {
        return _fragment;
    }

    /**
     * Tells whether or not this URI is absolute.
     * <p/>
     * <p> A URI is absolute if, and only if, it has a scheme component. </p>
     *
     * @return <tt>true</tt> if, and only if, this URI is absolute
     */
    public boolean isAbsolute() {
        return getScheme() != null;
    }

    /**
     * Returns a string form of this URI. The {@link URIContext}
     * encapsulates the data needed to write out the string form.
     * <p/>
     * <p> E.g. Defines if the &quot;&amp;amp;&quot; entity or the
     * '&amp;' character should be used to separate quary parameters. </p>
     *
     * @param uriContext data required to write out the string form.
     * @return the URI as a <code>String</code>
     */
    public String getURIString(URIContext uriContext) {
        InternalStringBuilder buf = new InternalStringBuilder(128);

        // Append the scheme
        if (getScheme() != null) {
            buf.append(getScheme()).append(':');
        }

        // Append the user info, host and, port
        if (getHost() != null) {
            buf.append("//");

            if (getUserInfo() != null) {
                buf.append(getUserInfo());
                buf.append('@');
            }

            buf.append(getHost());

            if (getPort() != UNDEFINED_PORT) {
                buf.append(':').append(getPort());
            }
        }

        // Append the path.
        if (getPath() != null) {
            if (isAbsolute()) {
                // absolute URI so
                appendEnsureSeparator(buf, getPath());
            } else {
                buf.append(getPath());
            }
        }

        // Append the parameters (the query)
        if (_params != null && _params.size() > 0) {
            buf.append('?');
            buf.append(getQuery(uriContext));
        }

        // Append the fragment
        if (getFragment() != null && getFragment().length() > 0) {
            buf.append('#').append(getFragment());
        }

        String url = buf.toString();

        return url;
    }

    /**
     * Returns a default <code>URIContext</code>.
     *
     * @return the URIContext with default data.
     */
    public static URIContext getDefaultContext() {
        URIContext uriContext = new URIContext();
        uriContext.setUseAmpEntity(true);

        return uriContext;
    }

    private static void appendEnsureSeparator(InternalStringBuilder buf, String token) {
        if (token != null && token.length() > 0) {
            if (buf.charAt(buf.length() - 1) != '/' && token.charAt(0) != '/') {
                buf.append('/');
            }
            if (buf.charAt(buf.length() - 1) == '/' && token.charAt(0) == '/') {
                token = token.substring(1);
            }
            buf.append(token);
        }
    }

    /**
     * Convenience method to encode unencoded components of a URI.
     *
     * @param url      the string to be encoded by {@link URLCodec}
     * @param encoding the character encoding to use
     * @return the encoded string
     */
    public static String encode(String url, String encoding) {
        String encodedURL = null;
        try {
            encodedURL = URLCodec.encode(url, encoding);
        } catch (java.io.UnsupportedEncodingException e) {
            // try utf-8 as a default encoding
            try {
                encodedURL = URLCodec.encode(url, DEFAULT_ENCODING);
            } catch (java.io.UnsupportedEncodingException ignore) {
            }
        }
        return encodedURL;
    }

    /**
     * Convenience method to encode unencoded components of a URI.
     * This implementation uses the value of the character encoding
     * field of this instance.
     *
     * @param url the string to be encoded by {@link URLCodec}
     * @return the encoded string
     */
    public String encode(String url) {
        return encode(url, _encoding);
    }

    /**
     * Determines if the passed-in Object is equivalent to this MutableURI.
     *
     * @param object the Object to test for equality.
     * @return true if object is a MutableURI with all values equal to this
     *         MutableURI, false otherwise
     */

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (object == null || !object.getClass().equals(this.getClass())) {
            return false;
        }

        MutableURI testURI = (MutableURI) object;

        if ((_scheme == testURI.getScheme() || (_scheme != null && _scheme.equalsIgnoreCase(testURI.getScheme()))) &&
                (_userInfo == testURI.getUserInfo() || (_userInfo != null && _userInfo.equals(testURI.getUserInfo()))) &&
                (_host == testURI.getHost() || (_host != null && _host.equalsIgnoreCase(testURI.getHost()))) &&
                _port == testURI.getPort() &&
                (_path == testURI.getPath() || (_path != null && _path.equals(testURI.getPath()))) &&
                (_fragment == testURI.getFragment() || (_fragment != null && _fragment.equals(testURI.getFragment()))) &&
                (_encoding == testURI.getEncoding() || (_encoding != null && _encoding.equals(testURI.getEncoding())))) {
            Map/*< String, List< String > >*/ params = getParameters();
            Map/*< String, List< String > >*/ testParams = testURI.getParameters();

            if (params == testParams || (params != null && params.equals(testParams))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a hash code value for the object.
     * <p> Implemented in conjunction with equals() override. </p>
     * <p> This is a mutable class implying that we're basing the hash
     * code on the member data that can change. Therefor it's important
     * not to use this class as a key in a hashtable as it would still
     * appear with an enumeration but not when calling contains.
     * I.E. The object could get lost in the hashtable. A call for the
     * hashcode would return a different value than when it was first
     * placed in the hashtable. </p>
     * <p/>
     * <p> With this in mind, we simply return the same value to support
     * the rules of equality. </p>
     *
     * @return a hash code value for this object.
     */

    public int hashCode() {
        return 0;
    }

    /**
     * Parse a URI reference, as a <code>String</code>, into its
     * components and use {@link java.net.URI} to encode the
     * components correctly. This comes from the parsing
     * algorithm of the Apache Commons HttpClient code for
     * its URI class.
     *
     * @param original the original character sequence
     * @throws URISyntaxException If an error occurs.
     */
    protected static URI encodeURI(String original) throws URISyntaxException {
        if (original == null) {
            throw new IllegalArgumentException("URI-Reference required");
        }

        String scheme = null;
        String authority = null;
        String path = null;
        String query = null;
        String fragment = null;
        String tmp = original.trim();
        int length = tmp.length();
        int from = 0;

        // The test flag whether the URI is started from the path component.
        boolean isStartedFromPath = false;
        int atColon = tmp.indexOf(':');
        int atSlash = tmp.indexOf('/');

        if (atColon < 0 || (atSlash >= 0 && atSlash < atColon)) {
            isStartedFromPath = true;
        }

        int at = indexFirstOf(tmp, isStartedFromPath ? "/?#" : ":/?#", from);

        if (at == -1) {
            at = 0;
        }

        // Parse the scheme.
        if (at < length && tmp.charAt(at) == ':') {
            scheme = tmp.substring(0, at).toLowerCase();
            from = ++at;
        }

        // Parse the authority component.
        if (0 <= at && at < length) {
            if (tmp.charAt(at) == '/') {
                if (at + 2 < length && tmp.charAt(at + 1) == '/') {
                    // the temporary index to start the search from
                    int next = indexFirstOf(tmp, "/?#", at + 2);
                    if (next == -1) {
                        next = (tmp.substring(at + 2).length() == 0) ? at + 2 : tmp.length();
                    }
                    authority = tmp.substring(at + 2, next);
                    from = at = next;
                }
            } else if (scheme != null && tmp.indexOf('/', at + 1) < 0) {
                int next = tmp.indexOf('#', at);
                if (next == -1) {
                    next = length;
                }
                String ssp = tmp.substring(at, next);
                if (next != length) {
                    fragment = tmp.substring(next + 1);
                }
                return new URI(scheme, ssp, fragment);
            }
        }

        // Parse the path component.
        if (from < length) {
            int next = indexFirstOf(tmp, "?#", from);
            if (next == -1) {
                next = length;
            }
            path = tmp.substring(from, next);
            at = next;
        }

        // Parse the query component.
        if (0 <= at && at + 1 < length && tmp.charAt(at) == '?') {
            int next = tmp.indexOf('#', at + 1);
            if (next == -1) {
                next = length;
            }
            query = tmp.substring(at + 1, next);
            at = next;
        }

        // Parse the fragment component.
        if (0 <= at && at + 1 <= length && tmp.charAt(at) == '#') {
            if (at + 1 == length) { // empty fragment
                fragment = "";
            } else {
                fragment = tmp.substring(at + 1);
            }
        }

        // Use java.net.URI to encode components and return.
        return new URI(scheme, authority, path, query, fragment);
    }

    /**
     * Get the earliest index, searching for the first occurrance of
     * any one of the given delimiters.
     *
     * @param s      the string to be indexed
     * @param delims the delimiters used to index
     * @param offset the from index
     * @return the earlier index if there are delimiters
     */
    protected static int indexFirstOf(String s, String delims, int offset) {
        if (s == null || s.length() == 0) {
            return -1;
        }
        if (delims == null || delims.length() == 0) {
            return -1;
        }

        // check boundaries
        if (offset < 0) {
            offset = 0;
        } else if (offset > s.length()) {
            return -1;
        }

        // s is never null
        int min = s.length();
        char[] delim = delims.toCharArray();
        for (int i = 0; i < delim.length; i++) {
            int at = s.indexOf(delim[i], offset);
            if (at >= 0 && at < min) {
                min = at;
            }
        }

        return (min == s.length()) ? -1 : min;
    }
}
