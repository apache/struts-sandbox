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
package org.apache.ti.compiler.apt;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.File;

public class PageFlowAptTask extends Javac {

    private String _factoryPath;
    private Reference _factoryPathRef;
    private String _webContentDir;

    public void setFactorypath(String factoryPath) {
        System.err.println("here!");
        _factoryPath = factoryPath;
    }

    public void setWebcontentdir(String webContentDir) {
        _webContentDir = webContentDir;
    }

    protected void scanDir(File srcDir, File destDir, String paths[]) {
        // Pass a bogus destdir, so it thinks all the files are out-of-date. We're passing
        // -nocompile to apt, and we don't care if the files were already compiled.
        super.scanDir(srcDir, new File("__notexist"), paths);
    }

    public void setFactoryPathRef(Reference ref) {
        _factoryPathRef = ref;
    }

    public void execute() throws BuildException {
        if (_webContentDir == null) {
            throw new BuildException(getClass().getName() + ": webcontentdir attribute must be set!");
        }
        setExecutable("apt");
        createCompilerArg().setValue("-Aweb.content.root=" + _webContentDir);
        createCompilerArg().setValue("-nocompile");
        if (_factoryPath != null) {
            createCompilerArg().setValue("-factorypath");
            createCompilerArg().setValue(_factoryPath);
        } else if (_factoryPathRef != null) {
            createCompilerArg().setValue("-factorypath");
            Path path = new Path(getProject());
            path.setRefid(_factoryPathRef);
            createCompilerArg().setPath(path);
        }

        setFork(true);
        super.execute();
    }
}

