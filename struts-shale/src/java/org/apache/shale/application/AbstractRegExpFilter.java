/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.shale.application;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.shale.faces.ShaleWebContext;

/**
 * <p>Convenience base class for <code>Command</code> implementations that
 * perform regular expression matching against a set of zero or more
 * patterns.  The default <code>Command</code> implementation will perform
 * the following algorithm.</p>
 * <ul>
 * <li>Retrieve the value to be compared by calling <code>value()</code>.</li>
 * <li>If the specified value is <code>null</code>, call <code>reject()</code>
 *     and return <code>true</code> to indicate that request procesing is
 *     complete.</li>
 * <li>If there are any exclude patterns, and the value matches one of
 *     these patterns, call <code>reject()</code> and return
 *     <code>true</code> to indicate that request processing is complete.</li>
 * <li>If there are any include patterns, and the value matches one of
 *     these patterns, call <code>accept()</code> and return
 *     <code>false</code> to indicate request processing should continue.</li>
 * <li>If there are any include patterns, and the value does not match one of
 *     these patterns, call <code>reject()</code> and return
 *     <code>true</code> to indicate that request processing is complete.</li>
 * <li>Call <code>accept()</code> and return <code>false</code> to indicate
 *     that request processing should continue.</li>
 * </ul>
 *
 * <p><strong>USAGE NOTE:</strong> - See the class JavaDocs for
 * <code>java.util.regex.Pattern</code> for the valid syntax for regular
 * expression patterns supported by this command.</p>
 *
 * <p><strong>USAGE NOTE:</strong> - Commands based on this class will only
 * be effective if used before the regular filter chain is processed.  In
 * other words, you should invoke it as part of a <code>preprocess</code>
 * chain in the <code>shale</code> catalog.</p>
 *
 * $Id$
 */

public abstract class AbstractRegExpFilter implements Command {



    // ------------------------------------------------------ Instance Variables


    /**
     * <p>Comma-delimited regular expression patterns to exclude remote host
     * names that match.</p>
     */
    private String excludes = null;


    /**
     * <p>Array of regular expression patterns for the excludes list.</p>
     */
    private Pattern excludesPatterns[] = new Pattern[0];


    /**
     * <p>Comma-delimited regular expression patterns to include remote host
     * names that match.</p>
     */
    private String includes = null;


    /**
     * <p>Array of regular expression patterns for the includes list.</p>
     */
    private Pattern includesPatterns[] = new Pattern[0];


    // -------------------------------------------------------------- Properties


    /**
     * <p>Return the comma-delimited regular expresson patterns to exclude
     * remote host names that match, if any; otherwise, return
     * <code>null</code>.</p>
     */
    public String getExcludes() { return this.excludes; }


    /**
     * <p>Set the comma-delimited regular expression patterns to exclude
     * remote host names that match, if any; or <code>null</code> for no
     * restrictions.</p>
     *
     * @param excludes New exclude pattern(s)
     */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
        this.excludesPatterns = precompile(excludes);
    }


    /**
     * <p>Return the comma-delimited regular expresson patterns to include
     * remote host names that match, if any; otherwise, return
     * <code>null</code>.</p>
     */
    public String getIncludes() { return this.includes; }


    /**
     * <p>Set the comma-delimited regular expression patterns to include
     * remote host names that match, if any; or <code>null</code> for no
     * restrictions.</p>
     *
     * @param includes New include pattern(s)
     */
    public void setIncludes(String includes) {
        this.includes = includes;
        this.includesPatterns = precompile(includes);
    }


    // --------------------------------------------------------- Command Methods


    /**
     * <p>Perform the matching algorithm described in our class Javadocs
     * against the value returned by the <code>value()</code> method.</p>
     *
     * @param context <code>ShaleWebContext</code> for this request
     */
    public boolean execute(Context context) throws Exception {

        // Acquire the value to be tested
        ShaleWebContext webContext = (ShaleWebContext) context;
        String value = value(webContext);
        if (value == null) {
            reject(webContext);
            return true;
        }

        // Check for a match on the excluded list
        if (matches(value, excludesPatterns, false)) {
            reject(webContext);
            return true;
        }

        // Check for a match on the included list
        if (matches(value, includesPatterns, true)) {
            accept(webContext);
            return false;
        }

        // Unconditionally accept this request
        accept(webContext);
        return false;

    }


    // ------------------------------------------------------- Protected Methods


    /**
     * <p>Perform whatever processing is necessary to mark this request as
     * being accepted.  The default implementation does nothing.</p>
     *
     * @param context <code>Context</code> for the current request
     */
    protected void accept(ShaleWebContext context) throws Exception {

        ;

    }


    /**
     * <p>Perform whatever processing is necessary to mark this request as
     * being rejected.  The default implementation returns a status code
     * of <code>HttpServletResponse.SC_FORBIDDEN</code>.</p>
     *
     * @param context <code>Context</code> for the current request
     */
    protected void reject(ShaleWebContext context) throws Exception {

        HttpServletResponse response = context.getResponse();
        response.sendError(response.SC_FORBIDDEN);

    }


    /**
     * <p>Return the value, from the specified context, that should be used
     * to match against the configured exclude and include patterns.  This
     * method must be implemented by a concrete subclass.</p>
     *
     * @param context <code>Context</code> for the current request
     */
    protected abstract String value(ShaleWebContext context);


    // --------------------------------------------------------- Private Methods


    /**
     * <p>Match the specified expression against the specified precompiled
     * patterns.  If there are no patterns, return the specified unrestricted
     * return value; otherwise, return <code>true</code> if the expression
     * matches one of the patterns, or <code>false</code> otherwise.</p>
     *
     * @param expr Expression to be tested
     * @param patterns Array of <code>Pattern</code> to be tested against
     * @param unrestricted Result to be returned if there are no matches
     */
    private boolean matches(String expr, Pattern patterns[],
                            boolean unrestricted) {

        // Check for the unrestricted case
        if ((patterns == null) || (patterns.length == 0)) {
            return unrestricted;
        }

        // Compare each pattern in turn for a match
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i].matcher(expr).matches()) {
                return true;
            }
        }

        // No match found, so return false
        return false;

    }


    /**
     * <p>Parse the specified string of comma-delimited (and optionally quoted,
     * if an embedded comma is required) regular expressions into an array
     * of precompiled <code>Pattern</code> instances that represent these
     * expressons.</p>
     *
     * @param expr Comma-delimited regular expressions
     */
    private Pattern[] precompile(String expr) {

        if (expr == null) {
            return new Pattern[0];
        }

        // Set up to parse the specified expression
        StreamTokenizer st =
          new StreamTokenizer(new StringReader(expr));
        st.lowerCaseMode(false);
        List list = new ArrayList();
        int type = 0;

        // Parse each included expression
        while (true) {
            try {
                type = st.nextToken();
            } catch (IOException e) {
                ; // Can not happen
            }
            if (type == st.TT_EOF) {
                break;
            } else if (type == st.TT_NUMBER) {
                list.add(Pattern.compile("" + st.nval));
            } else if (type == st.TT_WORD) {
                list.add(Pattern.compile(st.sval.trim()));
            } else {
                throw new IllegalArgumentException(expr);
            }
        }

        // Return the precompiled patterns as an array
        return (Pattern[]) list.toArray(new Pattern[list.size()]);

    }


}