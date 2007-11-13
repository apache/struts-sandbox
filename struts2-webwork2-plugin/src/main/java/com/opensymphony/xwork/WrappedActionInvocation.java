package com.opensymphony.xwork;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

public class WrappedActionInvocation implements ActionInvocation {
	private com.opensymphony.xwork2.ActionInvocation strutsActionInvocation;

	public WrappedActionInvocation(
			com.opensymphony.xwork2.ActionInvocation strutsActionInvocation) {
		this.strutsActionInvocation = strutsActionInvocation;
	}

	public void addPreResultListener(PreResultListener listener) {
		strutsActionInvocation.addPreResultListener(listener);
	}

	public Object getAction() {
		return strutsActionInvocation.getAction();
	}

	public ActionContext getInvocationContext() {
		return strutsActionInvocation.getInvocationContext();
	}

	public ActionProxy getProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Result getResult() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResultCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueStack getStack() {
		// TODO Auto-generated method stub
		return null;
	}

	public String invoke() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String invokeActionOnly() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isExecuted() {
		return strutsActionInvocation.isExecuted();
	}

	public void setActionEventListener(ActionEventListener listener) {
		strutsActionInvocation.setActionEventListener(listener);
	}

	public void setResultCode(String code) {
		strutsActionInvocation.setResultCode(code);
	}
}
