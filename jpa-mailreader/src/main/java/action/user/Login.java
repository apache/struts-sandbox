package action.user;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Results( {
        @Result(name = Index.CANCEL, value = Index.INDEX, type = ServletActionRedirectResult.class, params = {
                Index.NAMESPACE, Index.NONE }),
        @Result(name = Index.SUCCESS, value = Index.INDEX, type = ServletActionRedirectResult.class, params = {
                Index.USER, Index.USER_USERNAME }) })
@Validation()
public class Login extends Index {

    @Validations(requiredStrings = {
            @RequiredStringValidator(fieldName = "user.username", key = "error.username.required", message = ""),
            @RequiredStringValidator(fieldName = "user.password1", key = "error.password.required", message = "") })
    public String execute() throws Exception {
        authenticate();
        return (hasErrors()) ? INPUT : SUCCESS;
    }

}
