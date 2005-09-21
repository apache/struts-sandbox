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
package org.apache.ti.compiler.internal.grammar;

import org.apache.ti.compiler.internal.AnnotationGrammar;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.LocalFileEntityResolver;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A type that requires a valid XML file.  Can accept a specific XML schema, and will fall back to DTD-checking.
 */
public class ValidXmlFileType
        extends WebappPathType {
    private String _schemaFileName;
    private static Map _parseResults = Collections.synchronizedMap(new HashMap());

    public ValidXmlFileType(String schemaFileName, String requiredRuntimeVersion, AnnotationGrammar parentGrammar,
                            FlowControllerInfo fcInfo) {
        super(false, requiredRuntimeVersion, parentGrammar, fcInfo);
        _schemaFileName = schemaFileName;
    }

    protected boolean checkAnyExtension() {
        return true;
    }

    protected boolean doFatalError() {
        return true;
    }

    protected boolean ignoreDirectories() {
        return false;
    }

    protected boolean allowFileInPageFlowSourceDir() {
        return true;
    }

    protected void runAdditionalChecks(File file, AnnotationValue value) {
        //
        // We cache the results of parsing the file until the file is actually modified,
        // so we don't end up continually re-parsing it.
        //
        ParseResults prevResults = (ParseResults) _parseResults.get(file.getPath());
        long lastModTime = file.lastModified();

        if ((prevResults == null) || (lastModTime > prevResults.getFileModTime())) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                LocalFileEntityResolver entityResolver = LocalFileEntityResolver.getInstance();

                // If a schema was specified, we'll validate against that; otherwise, we'll just use the DTD.
                if (_schemaFileName != null) {
                    InputSource schemaInput = entityResolver.resolveLocalEntity(_schemaFileName);
                    assert schemaInput != null : "could not get schema resource for " + _schemaFileName;
                    factory.setNamespaceAware(true);
                    factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                                         "http://www.w3.org/2001/XMLSchema");
                    factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaInput);
                }

                factory.setValidating(true);

                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(LocalFileEntityResolver.getInstance());

                ParseResults results = new ParseResults(lastModTime);
                Validator handler = new Validator(results);
                builder.setErrorHandler(handler);

                Document doc = builder.parse(file);

                if ((doc.getDoctype() == null) && (_schemaFileName == null)) {
                    // If the doctype is null, then we don't want to add errors -- there was no DTD identified.
                    results = new ParseResults(lastModTime);
                }

                _parseResults.put(file.getPath(), results);
                addErrorDiagnostics(file, results, value);
            } catch (SAXParseException e) {
                _parseResults.put(file.getPath(), new ParseResults(lastModTime, e));
                addDiagnostic(file, e, value, true);

                return;
            } catch (Exception e) {
                _parseResults.put(file.getPath(), new ParseResults(lastModTime, e));
                addError(value, "error.xml-read-error", new Object[] { file.getPath(), e.getClass().getName(), e.getMessage() });

                return;
            }
        } else {
            addErrorDiagnostics(file, prevResults, value);
        }
    }

    private void addErrorDiagnostics(File file, ParseResults results, AnnotationValue value) {
        List errors = results.getErrors();

        for (Iterator i = errors.iterator(); i.hasNext();) {
            Exception e = (Exception) i.next();

            if (e instanceof SAXParseException) {
                addDiagnostic(file, (SAXParseException) e, value, true);
            } else {
                addError(value, "error.xml-read-error", new Object[] { file.getPath(), e.getClass().getName(), e.getMessage() });
            }
        }

        List warnings = results.getWarnings();

        for (Iterator i = warnings.iterator(); i.hasNext();) {
            Exception e = (Exception) i.next();
            assert e instanceof SAXParseException : e.getClass().getName();
            addDiagnostic(file, (SAXParseException) e, value, false);
        }
    }

    private void addDiagnostic(File file, SAXParseException err, AnnotationValue value, boolean isError) {
        if ((err.getColumnNumber() != -1) && (err.getLineNumber() != -1)) {
            Object[] args = { file.getPath(), new Integer(err.getLineNumber()), new Integer(err.getColumnNumber()), err.getMessage() };

            if (isError) {
                addError(value, "error.xml-parse-error", args);
            } else {
                addWarning(value, "error.xml-parse-error", args);
            }
        } else if (err.getLineNumber() != -1) {
            Object[] args = { file.getPath(), new Integer(err.getLineNumber()), err.getMessage() };

            if (isError) {
                addError(value, "error.xml-parse-error-nocolumn", args);
            } else {
                addWarning(value, "error.xml-parse-error-nocolumn", args);
            }
        } else {
            Object[] args = { file.getPath(), err.getMessage() };

            if (isError) {
                addError(value, "error.xml-parse-error-nolinecolumn", args);
            } else {
                addWarning(value, "error.xml-parse-error-nolinecolumn", args);
            }
        }
    }

    private static class ParseResults {
        private long _fileModTime;
        private List _errors = null;
        private List _warnings = null;

        public ParseResults(long fileModTime) {
            _fileModTime = fileModTime;
        }

        public ParseResults(long fileModTime, Exception ex) {
            _fileModTime = fileModTime;
            addError(ex);
        }

        public long getFileModTime() {
            return _fileModTime;
        }

        public void setFileModTime(long fileModTime) {
            _fileModTime = fileModTime;
        }

        public void addError(Exception e) {
            if (_errors == null) {
                _errors = new ArrayList();
            }

            _errors.add(e);
        }

        public void addWarning(Exception e) {
            if (_warnings == null) {
                _warnings = new ArrayList();
            }

            _warnings.add(e);
        }

        public List getErrors() {
            return (_errors != null) ? _errors : Collections.EMPTY_LIST;
        }

        public List getWarnings() {
            return (_warnings != null) ? _warnings : Collections.EMPTY_LIST;
        }
    }

    private static class Validator
            extends DefaultHandler {
        private ParseResults _results;

        public Validator(ParseResults results) {
            _results = results;
        }

        public ParseResults getResults() {
            return _results;
        }

        public void error(SAXParseException ex) throws SAXException {
            _results.addError(ex);
        }

        public void fatalError(SAXParseException ex) throws SAXException {
            _results.addError(ex);
        }

        public void warning(SAXParseException ex) throws SAXException {
            _results.addWarning(ex);
        }
    }
}
