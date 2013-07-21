package com.struts2.tutorial.action;



import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;




public class DefaultAction extends ActionSupport  {
	
	private static final Logger logger = Logger.getLogger( DefaultAction.class.getName() ) ;
	
	//injected by Spring
	private MessageService messageService;
	
	private String message ;
	

	private static final long serialVersionUID = 1L;
	

	public String execute() throws Exception {
	     	
		logger.info("Execute method...");
		
		 message = messageService.getMessage() ;
		 
	     return SUCCESS;
	      
	   }


	

	

	public MessageService getMessageService() {
		return messageService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
	
		messageService.setMessage(message);
		
	}
	
	

}
