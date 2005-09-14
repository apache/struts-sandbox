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

import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.env.Filer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FilerImpl implements Filer {

    private String _destDir;

    FilerImpl(AnnotationProcessorEnvironment env) {
        _destDir = (String) env.getOptions().get("-destdir");
    }

    public PrintWriter createTextFile(File file) throws IOException {
        assert ! file.isAbsolute() : file.getPath();
        File outputFile = new File(_destDir, file.getPath());
        outputFile.getParentFile().mkdirs();
        return new PrintWriter(new FileWriter(outputFile));
    }
}
