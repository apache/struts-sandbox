/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package jsf.physiciansFlow;

import org.apache.ti.pageflow.FacesBackingBean;
import org.apache.ti.pageflow.annotations.ti;

import jsf.physiciansFlow.Controller.MailMessageForm;
import org.apache.beehive.samples.netui.jsf.physician.Physician;

/**
 * This is the backing bean for JSF page "physicianDetail.faces" (physicianDetail.jsp).
 */
@ti.facesBacking
public class physicianDetail extends FacesBackingBean
{
    private MailMessageForm mailForm = new MailMessageForm();
    
    protected void onCreate()
    {
        // Initialize the MailMessageForm with the passed-in page input (passed as an action output
        // from the calling action.  We will pass this form bean to action "submitMailMessage" from
        // a commandButton on physicianDetail.jsp.
        mailForm = new MailMessageForm();
        mailForm.setPhysician((Physician) getPageInput("physician"));
    }

    public void setMailForm(MailMessageForm form)
    {
    	mailForm = form;
    }
    
    public MailMessageForm getMailForm()
    {
    	return mailForm;
    }
}
