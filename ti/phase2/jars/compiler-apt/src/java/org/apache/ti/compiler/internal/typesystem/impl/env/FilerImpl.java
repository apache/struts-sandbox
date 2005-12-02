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

import org.apache.ti.compiler.internal.typesystem.env.Filer;
import org.apache.ti.compiler.internal.typesystem.impl.DelegatingImpl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FilerImpl
        extends DelegatingImpl
        implements Filer {

    protected FilerImpl(com.sun.mirror.apt.Filer delegate) {
        super(delegate);
    }

    public static Filer get(com.sun.mirror.apt.Filer delegate) {
        return new FilerImpl(delegate);
    }

    public PrintWriter createTextFile(File file)
            throws IOException {
        return getDelegate().createTextFile(com.sun.mirror.apt.Filer.Location.CLASS_TREE, "", file, null);
    }

    protected com.sun.mirror.apt.Filer getDelegate() {
        return (com.sun.mirror.apt.Filer) super.getDelegate();
    }
}


