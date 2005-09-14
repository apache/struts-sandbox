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
package org.apache.ti.compiler.internal;

import org.apache.ti.compiler.internal.grammar.CommandHandlerGrammar;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.util.HashMap;
import java.util.Map;


public class FacesBackingChecker
        extends BaseChecker
        implements JpfLanguageConstants {

    public FacesBackingChecker(AnnotationProcessorEnvironment env, FacesBackingInfo fbInfo, Diagnostics diags) {
        super(env, fbInfo, diags);
    }

    public Map onCheck(ClassDeclaration jclass)
            throws FatalCompileTimeException {
        if (! CompilerUtils.isAssignableFrom(FACES_BACKING_BEAN_CLASS, jclass, getEnv())) {
            getDiagnostics().addError(jclass, "error.does-not-extend-base", FACES_BACKING_BEAN_CLASS);
            return null;
        }

        ClassDeclaration[] packageClasses = jclass.getPackage().getClasses();
        ClassDeclaration jpfClass = null;

        for (int i = 0; i < packageClasses.length; i++) {
            ClassDeclaration classDecl = packageClasses[i];
            if (CompilerUtils.isPageFlowClass(classDecl, getEnv())) jpfClass = classDecl;
        }

        FlowControllerInfo fcInfo = new FlowControllerInfo(jpfClass);
        fcInfo.startBuild(getEnv(), jpfClass);

        CommandHandlerGrammar chg =
                new CommandHandlerGrammar(getEnv(), getDiagnostics(), getRuntimeVersionChecker(), jpfClass, fcInfo);
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(jclass, COMMAND_HANDLER_TAG_NAME);

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            getFBSourceFileInfo().addCommandHandler(method.getSimpleName());
            chg.check(CompilerUtils.getAnnotation(method, COMMAND_HANDLER_TAG_NAME), null, method);
        }

        Map checkResultMap = new HashMap();
        checkResultMap.put(JpfLanguageConstants.ExtraInfoKeys.facesBackingInfo, getSourceFileInfo());
        return checkResultMap;
    }

    protected FacesBackingInfo getFBSourceFileInfo() {
        return (FacesBackingInfo) super.getSourceFileInfo();
    }
}
