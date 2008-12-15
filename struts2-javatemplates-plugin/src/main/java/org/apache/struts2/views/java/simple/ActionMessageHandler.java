package org.apache.struts2.views.java.simple;

public class ActionMessageHandler extends AbstractMessageListHandler {
     protected String getListExpression() {
        return "actionMessages";
    }

    @Override
    protected String getItemClass() {
        return "actionMessage";
    }
}
