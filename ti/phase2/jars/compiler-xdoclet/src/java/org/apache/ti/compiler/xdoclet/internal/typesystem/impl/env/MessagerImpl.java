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
package org.apache.ti.compiler.xdoclet.internal.typesystem.impl.env;

import org.apache.ti.compiler.internal.typesystem.env.Messager;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.ti.compiler.xdoclet.PageFlowDocletTask;
import xjavadoc.SourceClass;

public class MessagerImpl
        implements Messager {

    private String _sourceFilePath;

    protected MessagerImpl(SourceClass sourceClass) {
        _sourceFilePath = sourceClass.getFile().getPath();
    }

    public void printError(String s) {
        PageFlowDocletTask.get().addError(s, _sourceFilePath, -1);
    }

    public void printError(SourcePosition sourcePosition, String s) {
        assert sourcePosition != null;
        PageFlowDocletTask.get().addError(s, sourcePosition);
    }

    public void printWarning(String s) {
        PageFlowDocletTask.get().addWarning(s, _sourceFilePath, -1);
    }

    public void printWarning(SourcePosition sourcePosition, String s) {
        assert sourcePosition != null;
        PageFlowDocletTask.get().addWarning(s, sourcePosition);
    }

    public void printNotice(String s) {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public void printNotice(SourcePosition sourcePosition, String s) {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }
}
