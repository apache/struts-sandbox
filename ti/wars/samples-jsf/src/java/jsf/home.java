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
package jsf;

import org.apache.ti.pageflow.FacesBackingBean;
import org.apache.ti.pageflow.annotations.ti;
import org.apache.ti.pageflow.PageFlowController;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.event.ValueChangeEvent;

import org.apache.beehive.samples.netui.jsf.SharedFlow;


/**
 * This is the backing bean for JSF page "home.faces" (home.jsp).
 */
@ti.facesBacking
public class home extends FacesBackingBean
{
    @ti.sharedFlowField(name="shared")
    SharedFlow sharedFlow;

    /**
     * Get the current notesPreference
	 */
	protected void onCreate()
	{
        setNotesPreference("show");

        /* Shared flow isn't hooked up yet -- until then, this won't work
    	boolean pref = sharedFlow.getNotesPreference();

    	if (pref == false)
    		setNotesPreference("hide");
    	else
    		setNotesPreference("show");
        */
	}
	
	protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    private String notesPreference = "show";
    
    public String getNotesPreference()
    {
    	return this.notesPreference;
    }
    
    public void setNotesPreference(String value)
    {
    	this.notesPreference = value;
    }
    
    /**
     * Notes preference options
     */
    private SelectItem[] notesPreferenceOptions = {
        new SelectItem("hide", "Hide"),
        new SelectItem("show", "Show")
    };
    
    public SelectItem[] getNotesPreferenceOptions()
    {
    	return this.notesPreferenceOptions;
    }

	/**
	 * Handler for notes preference
	 */
    public void notesPreferenceChange(ValueChangeEvent vce)
    {
        // get the new value from the component
        if (vce.getNewValue().equals("hide"))
        {
        	setNotesPreference("hide");
			sharedFlow.setNotesPreference(false);
        }
        else
        {
        	setNotesPreference("show");
			sharedFlow.setNotesPreference(true);
        }
    }
}
