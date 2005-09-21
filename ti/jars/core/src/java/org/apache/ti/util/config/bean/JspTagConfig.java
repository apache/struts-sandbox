/**
 Copyright 2004 The Apache Software Foundation.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 $Header:$
 */
package org.apache.ti.util.config.bean;


/**
 *
 */
public class JspTagConfig {
    private static final DocType DEFAULT_DOC_TYPE = DocType.HTML4_LOOSE_QUIRKS;
    private static final IdJavascript DEFAULT_ID_JAVASCRIPT = IdJavascript.DEFAULT;
    private DocType _docType;
    private IdJavascript _idJavascript;
    private String _treeImageLocation;

    public JspTagConfig() {
        _docType = DEFAULT_DOC_TYPE;
        _idJavascript = DEFAULT_ID_JAVASCRIPT;
    }

    public JspTagConfig(DocType docType, IdJavascript idJavascript, String treeImageLocation) {
        this();

        if (docType != null) {
            _docType = docType;
        }

        if (idJavascript != null) {
            _idJavascript = idJavascript;
        }

        _treeImageLocation = treeImageLocation;
    }

    public DocType getDocType() {
        return _docType;
    }

    public IdJavascript getIdJavascript() {
        return _idJavascript;
    }

    public String getTreeImageLocation() {
        return _treeImageLocation;
    }
}
