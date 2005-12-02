/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.core.urltemplates;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The identified tokens and the text between the
 * matching tokens in the template are all returned.
 */
public class TemplateTokenizer implements Iterator {

    private static final String PATTERN = "\\{[\\w[\\-:_]]+\\}";
    private static final Pattern pattern = Pattern.compile(PATTERN);

    private CharSequence _template;
    private Matcher _matcher;
    private int _endPrevios = 0;

    // The current matched token value. If non-null and literal == null,
    // should be returned at the next call to next()
    private String _token;

    // The current literal string. If non-null, non-empty, should be
    // returned at the next call to next()
    private String _literal;

    /**
	 * @todo Finish documenting me!
     * 
     * Constructor that takes a {@link CharSequence}
     * @param template
     */
    public TemplateTokenizer(CharSequence template) {
        _template = template;
        _matcher = pattern.matcher(_template);
    }

    /**
     * @return Returns true if there are more literals or tokens/delimiters.
     * @see Iterator#hasNext()
     */
    public boolean hasNext() {
        if (_matcher == null) {
            return false;
        }
        if (_literal != null || _token != null) {
            return true;
        }
        if (_matcher.find()) {
            _literal = _template.subSequence(_endPrevios, _matcher.start()).toString();
            _token = _matcher.group();
            _endPrevios = _matcher.end();
        } else if (_endPrevios < _template.length()) {
            // We're at the end
            _literal = _template.subSequence(_endPrevios, _template.length()).toString();
            _endPrevios = _template.length();

            // Remove the matcher so it doesn't reset itself
            _matcher = null;
        }
        return _literal != null || _token != null;
    }

    /**
     * Returns the next literal string or token/delimiter.
     * @see Iterator#next()
     */
    public Object next() {
        String result = null;

        if (_literal != null) {
            result = _literal;
            _literal = null;
        } else if (_token != null) {
            result = _token;
            _token = null;
        }
        return result;
    }

    /**
	 * @todo Finish documenting me!
	 * 
     * Returns true if the call to next() will return a token rather
     * than a literal.
     * @return Fix me
     */
    public boolean isTokenNext() {
        return _literal == null && _token != null;
    }

    /**
     * Not supported.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
