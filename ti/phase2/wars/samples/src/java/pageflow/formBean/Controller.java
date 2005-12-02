package pageflow.formBean;

import org.apache.ti.pageflow.annotations.ti;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.Forward;
import com.opensymphony.xwork.Action;
import java.io.Serializable;

@ti.controller
public class Controller extends PageFlowController {

    public static class MyBean implements Serializable {
        private String _foo;
        public void setFoo(String foo) { _foo = foo; }
        public String getFoo() { return _foo; }
    }

    @ti.action
    public Forward submit(MyBean bean) {
        return new Forward(Action.SUCCESS, "result", bean.getFoo());
    }
}

/*
 (See /WEB-INF/src/_pageflow-config/pageflow/formBean/xwork.xml for a mockup of the generated config.)
 */

