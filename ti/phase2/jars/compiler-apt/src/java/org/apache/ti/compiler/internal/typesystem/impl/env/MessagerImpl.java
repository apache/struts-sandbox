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
package org.apache.ti.compiler.internal.typesystem.impl.env;

import org.apache.ti.compiler.internal.typesystem.env.Messager;
import org.apache.ti.compiler.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;

public class MessagerImpl
        extends DelegatingImpl
        implements Messager {

    protected MessagerImpl(com.sun.mirror.apt.Messager delegate) {
        super(delegate);
    }

    public static Messager get(com.sun.mirror.apt.Messager delegate) {
        return delegate != null ? new MessagerImpl(delegate) : null;
    }

    public void printError(String s) {
        getDelegate().printError(s);
    }

    public void printError(SourcePosition sourcePosition, String s) {
        if (sourcePosition != null) {
            assert sourcePosition instanceof SourcePositionImpl : sourcePosition.getClass().getName();
            getDelegate().printError(((SourcePositionImpl) sourcePosition).getDelegate(), s);
        } else {
            getDelegate().printError(s);
        }
    }

    public void printWarning(String s) {
        getDelegate().printWarning(s);
    }

    public void printWarning(SourcePosition sourcePosition, String s) {
        if (sourcePosition != null) {
            assert sourcePosition instanceof SourcePositionImpl : sourcePosition.getClass().getName();
            getDelegate().printWarning(((SourcePositionImpl) sourcePosition).getDelegate(), s);
        } else {
            getDelegate().printWarning(s);
        }
    }

    public void printNotice(String s) {
        getDelegate().printNotice(s);
    }

    public void printNotice(SourcePosition sourcePosition, String s) {
        assert sourcePosition != null;
        assert sourcePosition instanceof SourcePositionImpl : sourcePosition.getClass().getName();
        getDelegate().printNotice(((SourcePositionImpl) sourcePosition).getDelegate(), s);
    }

    protected com.sun.mirror.apt.Messager getDelegate() {
        return (com.sun.mirror.apt.Messager) super.getDelegate();
    }
}
