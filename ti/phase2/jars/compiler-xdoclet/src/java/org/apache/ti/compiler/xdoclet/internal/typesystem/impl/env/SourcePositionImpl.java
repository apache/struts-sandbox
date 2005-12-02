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

import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.ti.compiler.xdoclet.XDocletUtils;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.DelegatingImpl;
import xjavadoc.SourceClass;
import xjavadoc.XClass;
import xjavadoc.XProgramElement;
import xjavadoc.XTag;

import java.io.File;

public class SourcePositionImpl
        extends DelegatingImpl
        implements SourcePosition {

    private SourceClass _outerClass;
    private int _line = -1;
    private String _memberName;

    protected SourcePositionImpl(XProgramElement element, SourceClass outerClass) {
        super(element);
        _outerClass = outerClass;
    }

    protected SourcePositionImpl(XTag tag, SourceClass outerClass) {
        super(tag);
        _outerClass = outerClass;
        _line = tag.getLineNumber();
    }

    protected SourcePositionImpl(XTag tag, String memberName, SourceClass outerClass) {
        super(tag);
        _outerClass = outerClass;
        _line = tag.getLineNumber();
        _memberName = memberName;
    }

    public static SourcePosition get(XProgramElement element) {
        if (element == null) return null;
        XClass outerClass = XDocletUtils.getOutermostClass(element);
        return outerClass instanceof SourceClass ? new SourcePositionImpl(element, (SourceClass) outerClass) : null;
    }

    public static SourcePosition get(XTag tag, XProgramElement element) {
        if (element == null) return null;
        XClass outerClass = XDocletUtils.getOutermostClass(element);
        return outerClass instanceof SourceClass ? new SourcePositionImpl(tag, (SourceClass) outerClass) : null;
    }

    public static SourcePositionImpl get(XTag tag, String memberName, XProgramElement element) {
        if (element == null) return null;
        XClass outerClass = XDocletUtils.getOutermostClass(element);
        return outerClass instanceof SourceClass ? new SourcePositionImpl(tag, memberName, (SourceClass) outerClass) : null;
    }

    public File file() {
        return new File(_outerClass.getFile().getPath());
    }

    public int line() {
        return _line;
    }

    public int column() {
        assert false : "NYI";
        return 0;
    }

    public XClass getOuterClass() {
        return _outerClass;
    }

    public String getMemberName() {
        return _memberName;
    }
}
