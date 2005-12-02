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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

public class AnnotationsToXDoclet {

    private static final String[] QUALIFIED_ANNOTATIONS =
            {
                    "@org.apache.ti.pageflow.annotations."
            };

    private static void usage() {
        System.err.println("usage: " + AnnotationsToXDoclet.class.getName() + " input-file output-file");
        System.err.println("                            -or-");
        System.err.println("usage: " + AnnotationsToXDoclet.class.getName()
                + " -input-dir <root of input dir> -output-dir <root of output dir> -extensions <comma-separated list>");
    }

    public static void main(String[] args)
            throws IOException, FileNotFoundException {
        // Recurse
        if (args.length == 6) {
            if (! args[0].equals("-input-dir") || ! args[2].equals("-output-dir") || ! args[4].equals("-extensions")) {
                usage();
                System.exit(1);
            }

            File inputDir = new File(args[1]);
            if (! inputDir.exists()) {
                System.err.println(inputDir + " is not a directory");
                System.exit(2);
            }

            File outputDir = new File(args[3]);
            if (! outputDir.exists()) {
                System.err.println(outputDir + " is not a directory");
                System.exit(2);
            }

            String[] extensions = args[5].split(",");
            new AnnotationsToXDoclet().translateRecursive(inputDir, outputDir, extensions);
            return;
        }

        // ...or, do a single file translation.
        if (args.length != 2) {
            usage();
            System.exit(1);
        }

        File input = new File(args[0]);
        File output = new File(args[1]);

        if (input.equals(output)) {
            System.err.println("input file " + input + " must be different from output file");
            System.exit(2);
        }

        if (! input.canRead()) {
            System.err.println("cannot read " + input);
            System.exit(2);
        }

        if (output.exists() && ! output.canWrite()) {
            System.err.println("cannot write to " + output);
            System.exit(2);
        }

        new AnnotationsToXDoclet().translate(input, output);
    }

    public void translateRecursive(File inputDir, File outputDir, String[] extensions)
            throws IOException, FileNotFoundException {
        File[] children = inputDir.listFiles();

        outputDir.mkdirs();

        for (int i = 0; i < children.length; i++) {
            File child = children[i];

            if (child.isFile()) {
                for (int j = 0; j < extensions.length; j++) {
                    String extension = extensions[j];
                    if (child.getName().endsWith(extension)) {
                        translate(child, new File(outputDir, child.getName()));
                    }
                }
            } else if (child.isDirectory()) {
                File childOutputDir = new File(outputDir, child.getName());
                translateRecursive(child, childOutputDir, extensions);
            }
        }
    }

    public void translate(File input, File output)
            throws IOException, FileNotFoundException {
        System.err.println(input + " -> " + output);
        output.getAbsoluteFile().getParentFile().mkdirs();
        FileReader in = new FileReader(input);
        PrintWriter out = null;

        try {
            out = new PrintWriter(new FileWriter(output));
            StreamTokenizer tok = getJavaTokenizer(in);
            boolean addedSpace = false;
            StringBuffer indentBuffer = new StringBuffer();
            String indent = "";

            while (tok.nextToken() != StreamTokenizer.TT_EOF) {
                boolean wasSpace = false;
                boolean wasChar = false;

                switch (tok.ttype) {
                    case StreamTokenizer.TT_WORD:
                        if (indentBuffer != null) {
                            indent = indentBuffer.toString();
                            indentBuffer = null;
                        }
                        String str = tok.sval;
                        if (str.startsWith("@ti.viewProperties")) {
                            ignoreUntil(tok, ")");
                        } else if (str.startsWith("@ti.") || str.startsWith("@org.apache.ti")) {
                            out.println("/**");
                            ArrayList tags = new ArrayList();
                            translateAnnotation(tok, out, str, indent, tags);
                            for (int i = 0; i < tags.size(); ++i) {
                                out.print(indent);
                                out.print(" * ");
                                out.println((String) tags.get(i));
                            }
                            out.print(indent);
                            out.print(" */");
                        } else if (str.equals("import")) {
                            filterImport(tok, out);
                        } else {
                            out.print(str);
                            if (str.length() == 1) {
                                char c = str.charAt(0);
                                if (c == '\'' || c == '"') wasChar = true;
                            }
                        }
                        break;

                    case StreamTokenizer.TT_NUMBER:
                        assert false : tok.nval;   // parseNumbers() was set to false on the tokenizer.
                        break;

                    default:
                        char c = (char) tok.ttype;
                        wasChar = true;
                        if (! addedSpace || c != ' ') out.print(c);
                        wasSpace = Character.isWhitespace(c);
                        if (! wasSpace && indentBuffer != null) {
                            indent = indentBuffer.toString();
                            indentBuffer = null;
                        }
                        if (indentBuffer != null) indentBuffer.append(c);
                        if (c == '\n') indentBuffer = new StringBuffer();
                }

                if (! wasChar) {
                    out.print(' ');
                    addedSpace = true;
                } else {
                    addedSpace = false;
                }
            }
        }
        finally {
            in.close();
            if (out != null) out.close();
        }
    }

    private void ignoreUntil(StreamTokenizer tok, String str)
            throws IOException {
        while (! getToken(tok).equals(str)) {
        }
    }

    private void filterImport(StreamTokenizer tok, PrintWriter out)
            throws IOException {
        String importName = getToken(tok);
        if (! importName.startsWith("org.apache.ti.pageflow.annotations")) {
            out.print("import ");
            out.print(importName);
        } else {
            expectToken(tok, ";");
        }
    }

    private void translateAnnotation(StreamTokenizer tok, PrintWriter out, String firstToken, String indent,
                                     ArrayList tags)
            throws IOException {
        for (int i = 0; i < QUALIFIED_ANNOTATIONS.length; i++) {
            String qualifiedAnnotation = QUALIFIED_ANNOTATIONS[i];
            if (firstToken.startsWith(qualifiedAnnotation)) {
                firstToken = '@' + firstToken.substring(qualifiedAnnotation.length());
            }
        }

        String nextToken = getToken(tok);

        if (! nextToken.equals("(")) {
            tok.pushBack();
            tags.add(firstToken);
            return;
        } else {
            StringBuffer tag = new StringBuffer(firstToken);
            int thisTagPos = tags.size();
            tags.add("");

            while (! (nextToken = getToken(tok)).equals(")")) {
                if (nextToken.equals(",")) nextToken = getToken(tok);
                String attrName = nextToken;
                expectToken(tok, "=");
                String value = getToken(tok);
                int pos;

                if (value.charAt(0) == '@') {
                    if (attrName.equals("validationErrorForward")) {
                        // Special case:
                        //     validationErrorForward=@ti.forward(...)
                        // goes to
                        //     @ti.validationErrorForward(...)
                        value = "@ti.validationErrorForward";
                    }

                    translateAnnotation(tok, out, value, indent, tags);
                    value = null;
                } else if (value.equals("{")) {
                    StringBuffer stringArray = null;

                    while (! (nextToken = getToken(tok)).equals("}")) {
                        if (nextToken.equals(",")) nextToken = getToken(tok);

                        if (nextToken.charAt(0) == '@') {
                            translateAnnotation(tok, out, nextToken, indent, tags);
                        } else {
                            // We're expecting a string array element here.
                            assert nextToken.length() > 1 && nextToken.charAt(0) == '"'
                                    && nextToken.charAt(nextToken.length() - 1) == '"' : nextToken;
                            if (stringArray == null) {
                                stringArray = new StringBuffer("\"");
                            } else {
                                stringArray.append(',');
                            }
                            stringArray.append(nextToken.substring(1, nextToken.length() - 1));
                        }
                    }

                    value = stringArray != null ? stringArray.append('"').toString() : null;
                } else if (value.equals("true") || value.equals("false")) {
                    value = '"' + value + '"';
                } else if (value.endsWith(".class")) {
                    value = '"' + value.substring(0, value.length() - 6) + '"';
                } else if ((pos = value.indexOf("ti.NavigateTo.")) != -1) {
                    value = '"' + value.substring(pos + 14) + '"';
                } else if ((pos = value.indexOf("ti.validatorVersion.")) != -1) {
                    value = '"' + value.substring(pos + 20) + '"';
                } else if (isNumber(value)) {
                    value = '"' + value + '"';
                } else {
                    assert value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"' : value;
                    if (value.charAt(0) != '"') value = '"' + value;
                    if (value.charAt(value.length() - 1) != '"') value += '"';
                }

                if (value != null) {
                    tag.append(' ').append(attrName).append('=').append(value);
                }
            }

            tags.set(thisTagPos, tag.toString());
        }
    }

    private static boolean isNumber(String str) {
        char firstChar = str.charAt(0);
        if (firstChar != '-' && ! Character.isDigit(firstChar) && firstChar != '.') return false;

        for (int i = 1; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (! Character.isDigit(c) && c != '.') return false;
        }

        return true;
    }

    private String expectToken(StreamTokenizer tok, String expected)
            throws IOException {
        String token = getToken(tok);
        assert token.equals(expected) : "expected \"" + expected + "\", got \"" + token + "\" (line " + tok.lineno() + ')';
        return token;
    }

    private String getToken(StreamTokenizer tok)
            throws IOException {
        return getToken(tok, false, "");
    }

    private String getToken(StreamTokenizer tok, boolean includeSpace, String prepend)
            throws IOException {
        tok.nextToken();
        assert tok.ttype != StreamTokenizer.TT_EOF : "unexpected eof";
        String retVal;

        switch (tok.ttype) {
            case StreamTokenizer.TT_WORD:
                retVal = prepend + tok.sval;
                break;

            case StreamTokenizer.TT_NUMBER:
                assert false : tok.nval;   // parseNumbers() was set to false on the tokenizer.
                retVal = new Double(tok.nval).toString();
                break;

            default:
                char c = (char) tok.ttype;
                if (Character.isWhitespace(c) && ! includeSpace) return getToken(tok);
                retVal = prepend + Character.toString(c);
        }

        // If quotes are imbalanced, keep reading tokens until they're balanced.
        return count(retVal, '"') % 2 != 0 ? getToken(tok, true, retVal) : retVal;
    }

    private static int count(String s, char c) {
        int count = 0;
        char lastChar = '\0';

        for (int i = 0; i < s.length(); ++i) {
            if (lastChar != '\\' && s.charAt(i) == c) ++count;
            lastChar = c;
        }

        return count;
    }

    private static StreamTokenizer getJavaTokenizer(Reader reader) {
        StreamTokenizer tok = new StreamTokenizer(reader);
        tok.resetSyntax();
        tok.eolIsSignificant(true);
        tok.lowerCaseMode(false);
        tok.wordChars('A', 'Z');
        tok.wordChars('a', 'z');
        tok.wordChars('_', '_');
        tok.wordChars('@', '@');
        tok.wordChars('[', '[');
        tok.wordChars(']', ']');
        tok.wordChars('.', '.');
        tok.wordChars('"', '"');
        tok.wordChars('\'', '\'');
        tok.wordChars('-', '-');
        tok.wordChars('0', '9');
        tok.wordChars(':', ':');
        tok.wordChars('$', '$');
        tok.wordChars('/', '/');
        tok.wordChars('*', '*');
        tok.wordChars('\\', '\\');
        tok.wordChars('?', '?');
        return tok;
    }
}
