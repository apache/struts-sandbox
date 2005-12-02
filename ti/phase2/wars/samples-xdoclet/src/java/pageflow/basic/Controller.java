package pageflow.basic;

import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.Forward;
import com.opensymphony.xwork.Action;

/**
 * @ti.controller
 * @ti.handleException type="Controller.CustomException" path="error.jsp"
 * @ti.handleException type="ArithmeticException" method="handleArithmeticException"
 */
public class Controller extends PageFlowController {

    /** @ti.action */
    public String someAction() {
        return Action.SUCCESS;
    }

    static class CustomException extends Exception {}
    static class IntentionalException extends CustomException {}

    /** @ti.action */
    public String throw1() throws IntentionalException {
        throw new IntentionalException();  // caught by the @ti.handleException for CustomException
    }

    /** @ti.action */
    public String throw2() {
        throw new ArithmeticException("intentional");
    }

    /** @ti.exceptionHandler */
    public String handleArithmeticException(ArithmeticException e, String message) {
        return Action.SUCCESS;
    }
}
