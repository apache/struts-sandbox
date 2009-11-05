package org.apache.struts2.uelplugin;

import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.misc.NumberOperations;
import de.odysseus.el.tree.impl.Builder;
import de.odysseus.el.tree.impl.Parser;
import de.odysseus.el.tree.impl.Scanner;
import de.odysseus.el.tree.impl.ast.AstBinary;
import de.odysseus.el.tree.impl.ast.AstNode;
import de.odysseus.el.tree.impl.ast.AstUnary;
import de.odysseus.el.tree.impl.ast.AstIdentifier;
import org.apache.commons.lang.xwork.StringUtils;

public class JUELExtensionBuilder extends Builder {

    /**
     * We need a new token for "#".
     */
    static Scanner.ExtensionToken SHARP_TOKEN = new Scanner.ExtensionToken("#");
    static Scanner.ExtensionToken EXTENDED_ADD_TOKEN = new Scanner.ExtensionToken("+");

    /**
     * This is our operator which will be passed to an <code>AstBinary</code>.
     */
    static AstUnary.Operator SHARP_OPERATOR = new AstUnary.SimpleOperator() {
        public Object apply(TypeConverter converter, Object obj) {
            return obj;
        }

        public String toString() {
            return "#";
        }
    };

    static AstBinary.Operator EXTENDED_ADD_OPERATOR = new AstBinary.SimpleOperator() {

        public Object apply(TypeConverter converter, Object o1, Object o2) {
            if (o1 instanceof String || o2 instanceof String)
                return StringUtils.join(new Object[]{o1, o2});
            else
                return NumberOperations.add(converter, o1, o2);
        }


        public String toString() {
            return "+";
        }
    };

    /**
     * This is our handler which will create the abstract syntax node.
     */
    static Parser.ExtensionHandler SHARP_HANDLER = new Parser.ExtensionHandler(Parser.ExtensionPoint.UNARY) {
        public AstNode createAstNode(AstNode... children) {
            AstIdentifier astIdentifier = (AstIdentifier) children[0];
            ValueStackAstIdentifier valueStackAstIdentifier = new ValueStackAstIdentifier(astIdentifier.getName(), astIdentifier.getIndex());
            return new AstUnary(valueStackAstIdentifier, SHARP_OPERATOR);
        }
    };

    static Parser.ExtensionHandler EXTENDED_ADD_HANDLER = new Parser.ExtensionHandler(Parser.ExtensionPoint.ADD) {
        public AstNode createAstNode(AstNode... children) {
            return new AstBinary(children[0], children[1], EXTENDED_ADD_OPERATOR);
        }
    };


    /**
     * Here's our extended parser implementation.
     */
    static class ExtendedParser extends Parser {
        public ExtendedParser(Builder context, String input) {
            super(context, input);
            putExtensionHandler(SHARP_TOKEN, SHARP_HANDLER);
            putExtensionHandler(EXTENDED_ADD_TOKEN, EXTENDED_ADD_HANDLER);
        }

        /**
         * Use a modified scanner which recognizes <code>'~'</code> and keyword <code>'matches'</code>.
         */

        protected Scanner createScanner(String expression) {
            return new Scanner(expression) {

                protected Token nextEval() throws ScanException {
                    String input = getInput();
                    int currentIndex = getPosition();
                    char current = input.charAt(currentIndex);

                    if (current == '#' && (!StringUtils.substring(input, currentIndex + 1, currentIndex + 2).equals('{'))) {
                        return SHARP_TOKEN;
                    } else if (current == '+') {
                        return EXTENDED_ADD_TOKEN;
                    }


                    return super.nextEval();
                }
            };
        }
    }

    public JUELExtensionBuilder() {
        super();
    }

    public JUELExtensionBuilder(Feature... features) {
        super(features);
    }

    /**
     * Make sure to use our modified parser.
     */

    protected Parser createParser(String expression) {
        return new ExtendedParser(this, expression);
    }
}

class ValueStackAstIdentifier extends AstIdentifier {
    public ValueStackAstIdentifier(String name, int index) {
        super("#"+name, index);
    }
}
