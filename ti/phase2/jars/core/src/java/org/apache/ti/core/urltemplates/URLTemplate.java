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

import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The class to format a URL defined by url-template-config template
 * given by values for a set of tokens.
 */
public class URLTemplate {
    private static final Logger _log = Logger.getInstance(URLTemplate.class);
    private static final char BEGIN_TOKEN_QUALIFIER = '{';
    private static final char END_TOKEN_QUALIFIER = '}';

    // The String form of the template.
    private String _template;

    // Parsed representation of the template... list of literals and tokens.
    private ArrayList /*< TemplateItem >*/ _parsedTemplate = null;
    private boolean _isParsed = false;
    private HashMap /*< String, String >*/ _tokenValuesMap = new HashMap /*< String, String >*/();

    /**
     * Create a URLTemplate from a url-template-config template.
     *
     * @param template the string form of the template from url-template-config.
     */
    public URLTemplate(String template) {
        setTemplate(template);
    }

    /**
     * Copy constructor to create a URLTemplate from an existing URLTemplate.
     *
     * <p> Note that this is not truly a complete copy because the Map
     * of the replacement values for the given tokens is not copied.
     * This copy will just have an empty map of token values so that
     * it is "cleared" and ready to format another URL. </p>
     *
     * @param template the URLTemplate to copy.
     */
    public URLTemplate(URLTemplate template) {
        setTemplate(template.getTemplate());
        _parsedTemplate = template._parsedTemplate;
        _isParsed = template._isParsed;
    }

    /**
     * Reset the String form of the template.
     *
     * <p> Should call verify after setting a new template. </p>
     *
     * @param template the string form of the template from url-template-config.
     */
    public void setTemplate(String template) {
        if ((template == null) || (template.length() == 0)) {
            throw new IllegalStateException("Template cannot be null or empty.");
        }

        if (template.equals(_template)) {
            return;
        }

        _template = template;
        _isParsed = false;
        _parsedTemplate = null;
    }

    /**
     * Retrieve the String form of the template.
     *
     * @return the string form of the template.
     */
    public String getTemplate() {
        return _template;
    }

    /**
     * Verification will ensure the URL template conforms to a valid format
     * for known tokens and contains the required tokens. It will also parse
     * the tokens and literal data into a list to improve the replacement
     * performance when constructing the final URL string.
     *
     * <p> Allow clients to define a set of required and known tokens for the
     * template verification. Tokens are expected to be qualified
     * in braces. E.g. {url:path} </p>
     *
     * <p> If the template does not contain the required tokens or if the
     * format of a known token is incorrect, this method will log the error
     * and return false. </p>
     *
     * <p> Should call verify after creating a new template. </p>
     *
     * @param knownTokens the collection of known tokens (Strings) for a valid template.
     * @param requiredTokens the collection of required tokens (Strings) in a valid template.
     * @return true if the template conforms to a valid format, otherwise return false.
     */
    public boolean verify(Collection knownTokens, Collection requiredTokens) {
        boolean valid = true;

        // For each known token, make sure there is a leading and trailing brace
        if (knownTokens != null) {
            for (java.util.Iterator ii = knownTokens.iterator(); ii.hasNext();) {
                String token = (String) ii.next();

                if ((token != null) && (token.length() > 2)) {
                    // Strip braces from the known token
                    token = token.substring(1, token.length() - 1);

                    int index = _template.indexOf(token);

                    if (index != -1) {
                        if ((_template.charAt(index - 1) != BEGIN_TOKEN_QUALIFIER) ||
                                (_template.charAt(index + token.length()) != END_TOKEN_QUALIFIER)) {
                            _log.error("Template token, " + token + ", is not correctly enclosed with braces in template: " +
                                       _template);
                            valid = false;
                        }
                    }
                }
            }
        }

        // Parse the template into tokens and literals
        parseTemplate();

        // Check if the required tokens are present
        if (requiredTokens != null) {
            for (java.util.Iterator ii = requiredTokens.iterator(); ii.hasNext();) {
                String token = (String) ii.next();
                TemplateItem requiredItem = new TemplateItem(token, true);

                if (!_parsedTemplate.contains(requiredItem)) {
                    _log.error("Required token, " + token + ", not found in template: " + _template);
                    valid = false;
                }
            }
        }

        return valid;
    }

    private void parseTemplate() {
        if (_isParsed) {
            return;
        }

        _parsedTemplate = new ArrayList /*< TemplateItem >*/();

        TemplateTokenizer tokenizer = new TemplateTokenizer(getTemplate());

        for (; tokenizer.hasNext();) {
            boolean isToken = tokenizer.isTokenNext();
            String tokenOrLiteral = (String) tokenizer.next();

            if (tokenOrLiteral.equals("")) {
                continue;
            }

            TemplateItem item = new TemplateItem(tokenOrLiteral, isToken);
            _parsedTemplate.add(item);
        }

        _isParsed = true;
    }

    /**
     * Replace a set of tokens in the template with a corresponding set of values.
     * This assumes that there is an ordered one-to-one relationship. Tokens are
     * expected to be qualified in braces. E.g. {url:path}
     */
    public void substitute(Map /*< String, String >*/ tokensAndValues) {
        if (tokensAndValues != null) {
            _tokenValuesMap.putAll(tokensAndValues);
        }
    }

    /**
     * Replace a single token in the template with a corresponding String value.
     * Tokens are expected to be qualified in braces. E.g. {url:path}
     */
    public void substitute(String token, String value) {
        _tokenValuesMap.put(token, value);
    }

    /**
     * Replace a single token in the template with a corresponding int value.
     * Tokens are expected to be qualified in braces. E.g. {url:port}
     */
    public void substitute(String token, int value) {
        String valueStr = Integer.toString(value);
        _tokenValuesMap.put(token, valueStr);
    }

    /**
     * Return the String representation of the URL after replacing
     * the tokens in the template with their associated values. If
     * there is no value for a token, the token is discarded/removed.
     * I.E. It will not be part of the returned String.
     *
     * @return the url
     */
    public String toString() {
        return format(true);
    }

    /**
     * Return the String representation of the URL after replacing
     * the tokens in the template with their associated values. If
     * there is no value for a token, the token is discarded/removed.
     * I.E. It will not be part of the returned String.
     *
     * @return the url
     */
    public String format() {
        return format(true);
    }

    /**
     * Return the String representation of the URL after replacing
     * the tokens in the template with their associated values.
     * If the boolean argument is <code>true</code>, then the unset
     * template tokens are removed. Otherwise, do not cleanup
     * the unset tokens.
     *
     * @param removeUnsetTokens flag to tell URLTemplate to remove
     *        or leave the unset tokens in the URL.
     * @return the url
     */
    public String format(boolean removeUnsetTokens) {
        // template should already have been parsed with a call to
        if (!_isParsed) {
            // Parse the template into tokens and literals
            parseTemplate();
        }

        InternalStringBuilder result = new InternalStringBuilder(_template.length() + 16);

        for (java.util.Iterator ii = _parsedTemplate.iterator(); ii.hasNext();) {
            TemplateItem item = (TemplateItem) ii.next();

            if (item.isToken()) {
                if (_tokenValuesMap.containsKey(item.getValue())) {
                    appendToResult(result, (String) _tokenValuesMap.get(item.getValue()));
                } else {
                    // No value for the token.
                    if (!removeUnsetTokens) {
                        // treat the token as a literal
                        appendToResult(result, item.getValue());
                    }
                }
            } else {
                appendToResult(result, item.getValue());
            }
        }

        if ((result.length() > 0) && (result.charAt(result.length() - 1) == '?')) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    // check to make sure we don't end up with "//" between components
    // of the URL
    protected void appendToResult(InternalStringBuilder result, String value) {
        if ((value == null) || (value.length() == 0)) {
            return;
        }

        if ((result.length() > 0) && (result.charAt(result.length() - 1) == '/') && (value.charAt(0) == '/')) {
            result.deleteCharAt(result.length() - 1);
        }

        result.append(value);
    }

    protected class TemplateItem {
        private String value;
        private boolean isToken = false;

        public TemplateItem(String value, boolean isToken) {
            assert value != null : "TemplateItem value cannot be null.";
            this.value = value;
            this.isToken = isToken;
        }

        public String getValue() {
            return value;
        }

        public boolean isToken() {
            return isToken;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof TemplateItem)) {
                return false;
            }

            final TemplateItem templateItem = (TemplateItem) o;

            if (isToken != templateItem.isToken()) {
                return false;
            }

            if (!value.equals(templateItem.getValue())) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            int result;
            result = value.hashCode();
            result = (29 * result) + (isToken ? 1 : 0);

            return result;
        }
    }
}
