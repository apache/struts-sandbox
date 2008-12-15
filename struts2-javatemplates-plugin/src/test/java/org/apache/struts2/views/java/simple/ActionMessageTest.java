package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.ActionError;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.components.Anchor;

import java.util.List;
import java.util.ArrayList;

public class ActionMessageTest extends AbstractTest {
    private ActionError tag;
    private List<String> errors;

    public void testRenderActionError() {
        tag.setCssClass("class");
        tag.setCssStyle("style");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul><li><span style='style' class='class'>this clas is bad</span></li><li><span style='style' class='class'>baaaaad</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderActionErrorWithoutCssClass() {
        tag.setCssStyle("style");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<ul><li><span style='style' class='actionMessage'>this clas is bad</span></li><li><span style='style' class='actionMessage'>baaaaad</span></li></ul>");
        assertEquals(expected, output);
    }

    public void testRenderActionErrorNoErrors() {
        this.errors.clear();
        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        assertEquals("", output);
    }

    @Override
    protected void setUp() throws Exception {
        this.errors = new ArrayList<String>();
        this.errors.add("this clas is bad");
        this.errors.add("baaaaad");

        //errors are needed to setup stack
        super.setUp();
        this.tag = new ActionError(stack, request, response);
    }

    @Override
    protected void setUpStack() {
        super.setUpStack();
        expectFind("actionMessages", this.errors);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "actionmessage";
    }
}

