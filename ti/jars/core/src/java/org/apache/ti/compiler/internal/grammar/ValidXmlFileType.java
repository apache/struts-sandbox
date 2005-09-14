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
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ValidXmlFileType
        extends WebappPathType {

    private SchemaType _schema;
    private static Map _parseResults = Collections.synchronizedMap(new HashMap());

    public ValidXmlFileType(SchemaType schema, String requiredRuntimeVersion, AnnotationGrammar parentGrammar,
                            FlowControllerInfo fcInfo) {
        super(false, requiredRuntimeVersion, parentGrammar, fcInfo);
        _schema = schema;
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
        try {
            //
            // We cache the results of parsing the file until the file is actually modified,
            // so we don't end up continually re-parsing it.
            //
            ParseResults prevResults = (ParseResults) _parseResults.get(file.getPath());

            if (prevResults == null || file.lastModified() > prevResults.getFileModTime()) {
                try {
                    XmlOptions options = new XmlOptions();
                    options.setDocumentType(_schema);
                    XmlObject xml = XmlObject.Factory.parse(file, options);
                    List errorListener = new ArrayList();
                    options.setErrorListener(errorListener);

                    if (!xml.validate(options)) {
                        assert !errorListener.isEmpty();

                        XmlError err = (XmlError) errorListener.get(0);
                        assert err != null;

                        throw new XmlException(err.getMessage(), null, err);
                    }
                }
                catch (Exception e) {
                    _parseResults.put(file.getPath(), new ParseResults(file.lastModified(), e));
                    throw e;
                }

                _parseResults.put(file.getPath(), new ParseResults(file.lastModified(), null));
            } else {
                Exception e = prevResults.getException();

                if (e != null) {
                    throw e;
                }
            }
        }
        catch (XmlException e) {
            addErrorDiagnostic(e.getError(), value);
        }
        catch (Exception e) {
            addError(value, "error.xml-read-error", new Object[]{file.getPath(), e.getMessage()});
        }
    }

    private void addErrorDiagnostic(XmlError err, AnnotationValue value) {
        if (err.getColumn() != -1 && err.getLine() != -1) {
            Object[] args =
                    {
                            err.getSourceName(),
                            new Integer(err.getLine()),
                            new Integer(err.getColumn()),
                            err.getMessage()
                    };

            addError(value, "error.xml-parse-error", args);
        } else if (err.getLine() != -1) {
            Object[] args =
                    {
                            err.getSourceName(),
                            new Integer(err.getLine()),
                            err.getMessage()
                    };

            addError(value, "error.xml-parse-error-nocolumn", args);
        } else {
            Object[] args =
                    {
                            err.getSourceName(),
                            err.getMessage()
                    };

            addError(value, "error.xml-parse-error-nolinecolumn", args);
        }
    }

    private static class ParseResults {

        private long _fileModTime;
        private Exception _exception;

        public ParseResults(long fileModTime, Exception exception) {
            _fileModTime = fileModTime;
            _exception = exception;
        }

        public long getFileModTime() {
            return _fileModTime;
        }

        public void setFileModTime(long fileModTime) {
            _fileModTime = fileModTime;
        }

        public Exception getException() {
            return _exception;
        }

        public void setException(Exception exception) {
            _exception = exception;
        }
    }
}
