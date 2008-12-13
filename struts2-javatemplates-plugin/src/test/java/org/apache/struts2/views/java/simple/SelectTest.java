package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.TextField;
import org.apache.struts2.components.Select;
import org.easymock.EasyMock;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SelectTest  extends AbstractTestCase {

     public void testRenderSelectWithHeader() {
        SelectEx tag = new SelectEx(stack, request, response);

        tag.setList("%{{'key0', 'key1'}}");
        tag.setHeaderKey("%{'key0'}");
        tag.setHeaderValue("%{'val'}");

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("select", context);
        String output = writer.getBuffer().toString();
        String expected = s("<select name=''><option value='key0'>val</option></select>");
        assertEquals(expected, output);
    }

    @Override
    protected void setUpStack() {
        super.setUpStack();

        expectFind("'key0'", String.class, "key0");
        expectFind("'val'", String.class, "val");
    }

    class SelectEx extends Select {
        public SelectEx(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
            super(stack, request, response);
        }

        public void processParams() {
            //these methods are protected
            evaluateParams();
        }

        public boolean altSyntax() {
            return true;
        }
    }
}
