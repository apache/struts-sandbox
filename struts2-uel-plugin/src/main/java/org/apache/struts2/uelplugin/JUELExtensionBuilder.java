package org.apache.struts2.uelplugin;

import de.odysseus.el.misc.NumberOperations;
import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.tree.Bindings;
import de.odysseus.el.tree.Node;
import de.odysseus.el.tree.impl.Builder;
import de.odysseus.el.tree.impl.Parser;
import de.odysseus.el.tree.impl.Scanner;
import de.odysseus.el.tree.impl.ast.AstBinary;
import de.odysseus.el.tree.impl.ast.AstNode;
import de.odysseus.el.tree.impl.ast.AstUnary;
import org.apache.commons.lang.xwork.StringUtils;

import javax.el.ELContext;
import javax.el.MethodInfo;
import javax.el.ValueReference;

/**
 * Plugs into JUEL parser to supper expressions like "#obj", to provide some level
 * of backward compatibility with OGNL
 */
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
            return new DelegateAstNode(children[0]);
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
                        //fake unary operator so it is accepted by the parser
                        return SHARP_TOKEN;
                    } else if (currentIndex > 0 && input.charAt(currentIndex - 1) == '#' && current != '{') {
                        //direct reference, like #obj, let the parser extract the token
                        Token token = super.nextEval();
                        //add #to the name of the token
                        return token(Symbol.IDENTIFIER, "#" + token.getImage(), token.getSize());
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

class DelegateAstNode extends AstNode {
    private final AstNode child;

    public DelegateAstNode(AstNode child) {
        this.child = child;
    }

    public void appendStructure(StringBuilder builder, Bindings bindings) {
        child.appendStructure(builder, bindings);
    }

    public Object eval(Bindings bindings, ELContext context) {
        return child.eval(bindings, context);
    }

    public int getCardinality() {
        return child.getCardinality();
    }

    public Node getChild(int i) {
        return child.getChild(i);
    }

    public MethodInfo getMethodInfo(Bindings bindings, ELContext context, Class<?> returnType, Class<?>[] paramTypes) {
        return child.getMethodInfo(bindings, context, returnType, paramTypes);
    }

    public Class<?> getType(Bindings bindings, ELContext context) {
        return child.getType(bindings, context);
    }

    public ValueReference getValueReference(Bindings bindings, ELContext context) {
        return child.getValueReference(bindings, context);
    }

    public Object invoke(Bindings bindings, ELContext context, Class<?> returnType, Class<?>[] paramTypes, Object[] paramValues) {
        return child.invoke(bindings, context, returnType, paramTypes, paramValues);
    }

    public boolean isLeftValue() {
        return child.isLeftValue();
    }

    public boolean isLiteralText() {
        return child.isLiteralText();
    }

    public boolean isReadOnly(Bindings bindings, ELContext context) {
        return child.isReadOnly(bindings, context);
    }

    public void setValue(Bindings bindings, ELContext context, Object value) {
        child.setValue(bindings, context, value);
    }
}
