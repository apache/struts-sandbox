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
package org.apache.ti.compiler.xdoclet.internal.tools;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class AnnotationsToXDocletTask
        extends Copy {

    protected void doFileOperations() {
        try {
            AnnotationsToXDoclet atx = new AnnotationsToXDoclet();

            for (Iterator i = fileCopyMap.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String[] values = (String[]) entry.getValue();
                assert values.length == 1 : values.length;
                atx.translate(new File((String) entry.getKey()), new File(values[0]));
            }
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }
}
