package example;

import java.util.Date;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Simple Survey Action.
 */
public class SurveyAction extends ActionSupport {
	private SurveyBean surveyBean = new SurveyBean();

	public String edit() {
		surveyBean.setAge(22);
		surveyBean.setFirstName("Lex");
		surveyBean.setBirthdate(new Date());
		return SUCCESS;
	}

	public String save() {
		return SUCCESS;
	}

	public SurveyBean getSurveyBean() {
		return surveyBean;
	}

	public void setSurveyBean(SurveyBean surveyBean) {
		this.surveyBean = surveyBean;
	}
}
