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

import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.env.Filer;
import org.apache.ti.compiler.internal.typesystem.env.Messager;
import org.apache.ti.compiler.xdoclet.PageFlowDocletSubTask;
import org.apache.ti.compiler.xdoclet.XDocletCompilerUtils;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.AnnotationTypeDeclarationImpl;
import xdoclet.DocletContext;
import xjavadoc.SourceClass;
import xjavadoc.XDoc;
import xjavadoc.XJavaDoc;

import java.util.HashMap;
import java.util.Map;

public class AnnotationProcessorEnvironmentImpl
        extends DelegatingImpl
        implements AnnotationProcessorEnvironment {

    private static final Declaration[] EMPTY_DECLARATION_ARRAY = new Declaration[0];

    private PageFlowDocletSubTask _subtask;
    private SourceClass _sourceClass;
    private HashMap _attributes;
    private Map _options;

    protected AnnotationProcessorEnvironmentImpl(DocletContext delegate, PageFlowDocletSubTask subtask,
                                                 SourceClass sourceClass, Map options) {
        super(delegate);
        _subtask = subtask;
        _sourceClass = sourceClass;
        _options = options != null ? options : new HashMap();
    }

    public static AnnotationProcessorEnvironment get(DocletContext delegate, PageFlowDocletSubTask subtask,
                                                     SourceClass sc, Map options) {
        return delegate != null ? new AnnotationProcessorEnvironmentImpl(delegate, subtask, sc, options) : null;
    }

    public Map getOptions() {
        return _options;
    }

    public Messager getMessager() {
        return new MessagerImpl(_sourceClass);
    }

    public Filer getFiler() {
        return new FilerImpl(this);
    }

    public TypeDeclaration[] getSpecifiedTypeDeclarations() {
        /*
        if ( _specifiedTypeDeclarations == null )
        {
            Collection delegateCollection = getXJavaDoc().getSourceClasses();
            TypeDeclaration[] array = new TypeDeclaration[delegateCollection.size()];
            int j = 0;
            for ( Iterator i = delegateCollection.iterator(); i.hasNext(); )
            {
                array[j++] = WrapperFactory.get().getTypeDeclaration( ( SourceClass ) i.next() );
            }
            _specifiedTypeDeclarations = array;
        }

        return _specifiedTypeDeclarations;
        */
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public TypeDeclaration getTypeDeclaration(String s) {
        return XDocletCompilerUtils.resolveTypeDeclaration(s);
    }

    public Declaration[] getDeclarationsAnnotatedWith(AnnotationTypeDeclaration decl) {
        //
        // Note: for now we only examine the single public source class for any of the given annotations.
        //
        XDoc doc = _sourceClass.getDoc();

        if (doc != null) {
            assert decl instanceof AnnotationTypeDeclarationImpl : decl.getClass().getName();
            if (doc.getTag(((AnnotationTypeDeclarationImpl) decl).getIntermediateName()) != null) {
                return new Declaration[]{WrapperFactory.get().getTypeDeclaration(_sourceClass)};
            }
        }

        return EMPTY_DECLARATION_ARRAY;
    }

    public DocletContext getDelegateDocletContext() {
        return (DocletContext) super.getDelegate();
    }

    protected final XJavaDoc getXJavaDoc() {
        return _subtask.getXJavaDoc();
    }

    public void setAttribute(String propertyName, Object value) {
        if (_attributes == null) _attributes = new HashMap();
        _attributes.put(propertyName, value);
    }

    public Object getAttribute(String propertyName) {
        return _attributes != null ? _attributes.get(propertyName) : null;
    }
}
