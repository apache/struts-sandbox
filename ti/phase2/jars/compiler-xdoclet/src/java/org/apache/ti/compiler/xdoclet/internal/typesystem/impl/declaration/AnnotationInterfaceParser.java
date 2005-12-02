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
package org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration;

import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AnnotationInterfaceParser implements JpfLanguageConstants {

    private HashMap _memberArrayAnnotations = new HashMap();
    private HashMap _memberAnnotations = new HashMap();
    private HashSet _memberOrTopLevelAnnotations = new HashSet();
    private String _annotationInterfacePrefix;


    /**
     * Map of String intermediate-name (e.g., "ti.action") to AnnotationTypeDeclaration
     */
    private HashMap _annotations = new HashMap();
    private AnnotationTypeDeclaration[] _allAnnotations;

    public AnnotationInterfaceParser(String annotationInterfacePrefix) {
        _annotationInterfacePrefix = annotationInterfacePrefix;
        parseAnnotations();
        _allAnnotations = (AnnotationTypeDeclaration[])
                _annotations.values().toArray(new AnnotationTypeDeclaration[ _annotations.size() ]);
    }

    public void addMemberAnnotation(String tagName, String parentAttribute) {
        _memberAnnotations.put(_annotationInterfacePrefix + tagName, parentAttribute);
    }

    public void addMemberArrayAnnotation(String tagName, String parentAttribute) {
        _memberArrayAnnotations.put(_annotationInterfacePrefix + tagName, parentAttribute);
    }

    public void addMemberOrTopLevelAnnotation(String tagName) {
        _memberOrTopLevelAnnotations.add(_annotationInterfacePrefix + tagName);
    }

    public AnnotationTypeDeclaration[] getAllAnnotations() {
        return _allAnnotations;
    }

    public AnnotationTypeDeclaration getAnnotationTypeDecl(String tagName) {
        return (AnnotationTypeDeclaration) _annotations.get(tagName);
    }

    AnnotationTypeDeclarationImpl getAnnotationTypeDeclImpl(String tagName) {
        return (AnnotationTypeDeclarationImpl) _annotations.get(tagName);
    }

    void addAnnotation(String tagName, AnnotationTypeDeclarationImpl annotation) {
        _annotations.put(_annotationInterfacePrefix + tagName, annotation);
    }

    /**
     * Get the name of the parent annotation member for the given tag.
     */
    public String getParentMemberName(String tagName) {
        return (String) _memberAnnotations.get(tagName);
    }

    /**
     * Get the name of the parent annotation array member for the given tag.
     */
    public String getParentMemberArrayName(String tagName) {
        return (String) _memberArrayAnnotations.get(tagName);
    }

    public boolean isMemberOrTopLevelAnnotation(String tagName) {
        return _memberOrTopLevelAnnotations.contains(tagName);
    }

    private static StreamTokenizer getJavaTokenizer(Reader reader) {
        StreamTokenizer tok = new StreamTokenizer(reader);
        tok.eolIsSignificant(false);
        tok.lowerCaseMode(false);
        tok.parseNumbers();
        tok.slashSlashComments(true);
        tok.slashStarComments(true);
        tok.wordChars('_', '_');
        tok.wordChars('@', '@');
        tok.wordChars('[', '[');
        tok.wordChars(']', ']');
        tok.wordChars('.', '.');
        tok.wordChars('"', '"');
        tok.wordChars('-', '-');
        return tok;
    }

    private void parseAnnotations() {
        String annotationsSource = ANNOTATIONS_CLASSNAME.replace('.', '/') + ".java";
        InputStream in = DeclarationImpl.class.getClassLoader().getResourceAsStream(annotationsSource);
        assert in != null : "annotations source not found: " + annotationsSource;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        try {
            HashMap enums = new HashMap();  // String enumTypeName -> HashSet values
            StreamTokenizer tok = getJavaTokenizer(reader);


            String interfaceQualifier = null;
            String packageName = null;

            while (tok.nextToken() != StreamTokenizer.TT_EOF) {
                switch (tok.ttype) {
                    case StreamTokenizer.TT_WORD:
                        String str = tok.sval;

                        if (packageName == null && str.equals("package")) {
                            packageName = assertWord(tok);
                        } else if (str.equals("public")) {
                            str = assertWord(tok);

                            if (str.equals("interface")) {
                                interfaceQualifier = assertWord(tok) + '.';
                                assertChar(tok, '{');
                            } else if (str.equals("@interface")) {
                                AnnotationTypeDeclarationImpl ann =
                                        readAnnotation(tok, interfaceQualifier, packageName, enums);
                                _annotations.put(ann.getIntermediateName(), ann);
                            } else if (str.equals("enum")) {
                                readEnum(tok, enums);
                            }
                        } else if (str.charAt(0) == '@') {
                            if (tok.nextToken() == '(') {
                                ignoreAnnotation(tok);
                            } else {
                                tok.pushBack();
                            }
                        }
                        break;

                    case StreamTokenizer.TT_NUMBER:
                        break;

                    default:
                        char c = (char) tok.ttype;
                        if (c == '}') {
                            assert interfaceQualifier != null;
                            interfaceQualifier = null;
                        }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false : e;
        }
        finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                assert false : e;
            }
        }
    }

    private static String assertWord(StreamTokenizer tok)
            throws IOException {
        tok.nextToken();
        assert tok.ttype == StreamTokenizer.TT_WORD : tok.ttype;
        return tok.sval;
    }

    private static void assertChar(StreamTokenizer tok, char c)
            throws IOException {
        tok.nextToken();
        assert tok.ttype == c : tok.ttype;
    }

    private static AnnotationTypeDeclarationImpl readAnnotation(StreamTokenizer tok, String interfaceQualifier,
                                                                String packageName, HashMap enums)
            throws IOException {
        String annotationName = assertWord(tok);
        ArrayList memberDecls = new ArrayList();
        assertChar(tok, '{');

        while (tok.nextToken() == StreamTokenizer.TT_WORD) {
            String memberType = tok.sval;
            HashSet enumVals = (HashSet) enums.get(memberType);

            tok.nextToken();
            if (tok.ttype == '<') // ignore generics
            {
                while (tok.nextToken() != '>') {
                    assert tok.ttype != StreamTokenizer.TT_EOF;
                    assert tok.ttype != ';';
                }
                tok.nextToken();
            }
            assert tok.ttype == StreamTokenizer.TT_WORD;
            String memberName = tok.sval;
            assertChar(tok, '(');
            assertChar(tok, ')');

            Object defaultVal = null;

            if (tok.nextToken() == StreamTokenizer.TT_WORD) {
                assert tok.sval.equals("default");

                tok.nextToken();
                if (tok.ttype == '{') {
                    assertChar(tok, '}');
                    defaultVal = new ArrayList();
                } else {
                    assert tok.ttype == StreamTokenizer.TT_WORD || tok.ttype == StreamTokenizer.TT_NUMBER : tok.ttype;

                    if (tok.ttype == StreamTokenizer.TT_NUMBER) {
                        defaultVal = getNumericDefaultVal(memberType, tok.nval);
                    } else {
                        String defaultString = tok.sval;

                        if (defaultString.charAt(0) == '@') {
                            // It's a default value that is an annotation.  We ignore these for now.
                            ignoreAnnotation(tok);
                        } else {
                            if (memberType.equals("String")) {
                                assert defaultString.charAt(0) == '"' : defaultString;
                                int len = defaultString.length();
                                assert len > 1 && defaultString.charAt(len - 1) == '"' : defaultString;
                                defaultVal = defaultString.substring(0, len - 1);
                            } else if (memberType.equals("boolean")) {
                                defaultVal = Boolean.valueOf(defaultString);
                            } else if (memberType.equals("Class")) {
                                assert defaultString.endsWith(".class");
                                defaultVal = defaultString.substring(0, defaultString.indexOf(".class"));
                            } else {
                                defaultVal = readDefaultEnumVal(defaultString, memberType, enumVals);
                            }
                        }
                    }
                }

                tok.nextToken();
            }

            assert tok.ttype == ';';

            if (enumVals != null) memberType = "String";
            memberDecls.add(new AnnotationTypeElementDeclarationImpl(memberName, memberType, defaultVal, enumVals));
        }

        assert tok.ttype == '}';

        AnnotationTypeElementDeclaration[] memberArray = (AnnotationTypeElementDeclaration[])
                memberDecls.toArray(new AnnotationTypeElementDeclaration[ memberDecls.size() ]);
        return new AnnotationTypeDeclarationImpl(annotationName, interfaceQualifier, packageName, memberArray);
    }

    private static String readDefaultEnumVal(String defaultString, String memberType, HashSet enumVals) {
        int dot = defaultString.indexOf('.');
        assert dot != -1 : "expected an enum value: " + defaultString;
        String type = defaultString.substring(0, dot);
        assert type.equals(memberType) : "expected enum " + memberType + ", got " + type;
        assert enumVals != null : "no enum " + memberType
                + " defined; currently, enum must be defined before its use";
        String defaultVal = defaultString.substring(dot + 1);
        assert enumVals.contains(defaultVal) :
                "invalid enum field " + defaultVal + " on enum " + type;
        return defaultVal;
    }

    private static Object getNumericDefaultVal(String expectedType, double defaultNumber) {
        if (expectedType.equals("int")) {
            return new Integer((int) defaultNumber);
        } else if (expectedType.equals("long")) {
            return new Long((long) defaultNumber);
        } else if (expectedType.equals("float")) {
            return new Float((float) defaultNumber);
        } else if (expectedType.equals("double")) {
            return new Double(defaultNumber);
        }

        assert false : "type " + expectedType + " cannot accept value " + defaultNumber;
        return null;
    }

    private static void ignoreAnnotation(StreamTokenizer tok)
            throws IOException {
        while (tok.nextToken() != ')') {
            assert tok.ttype != StreamTokenizer.TT_EOF;
            assert tok.ttype != ';';
        }
    }

    private static void readEnum(StreamTokenizer tok, HashMap enums)
            throws IOException {
        String enumName = assertWord(tok);

        assertChar(tok, '{');
        HashSet fieldNames = new HashSet();

        while (true) {
            fieldNames.add(assertWord(tok));
            tok.nextToken();
            if (tok.ttype == '}') break;
            assert tok.ttype == ',' : tok.ttype;    // for now, we only do very simple enums.
        }

        enums.put(enumName, fieldNames);
    }
}
