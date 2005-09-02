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

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.ti.pageflow.FacesBackingBean;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.annotations.ti;

import org.apache.beehive.samples.netui.jsf.physician.Physician;
import jsf.physiciansFlow.Controller.PhysicianSearchForm;

/**
 * This is the backing bean for JSF page "search.faces" (search.jsp).
 */
@ti.facesBacking
public class search extends FacesBackingBean
{
    @ti.pageFlowField
    Controller pageFlow;
    
    // page flow action form containing search criteria 
    // will be passed to page flow action
    public PhysicianSearchForm searchForm = new PhysicianSearchForm();

    // a bean to hold the search criteria input by the user
    private Physician criteria;
        
    // The type of physician - see "types" below
    private String physicianType = Physician.FAMILY;
      
    // start with the specialist choices disabled
    private boolean specialistsDisabled = true;
    
    // default setting is abbreviated results
    private boolean showDetailedResults = false;
            
    // The type of physician - see "types" below
    private String resultFormatType = "shortFormat";

    // Physician types
    private SelectItem[] physicianTypes = {
        new SelectItem(Physician.FAMILY, "Family Doctor"),
        new SelectItem(Physician.SPECIALIST, "Specialist")
    };
    
    // Specialist types
    private SelectItem[] specialistTypes = {
        new SelectItem("ear", "ear"),
        new SelectItem("nose", "nose"),
        new SelectItem("throat", "throat")
    };
               
    // city options
    private SelectItem[] cities = {
        new SelectItem("Boulder", "Boulder"),
        new SelectItem("Denver", "Denver")
    };
 
    // result format types (minimal info or lots of info)
    private SelectItem[] resultFormatTypes = {
        new SelectItem("shortFormat", "Abbreviated Physician Information"),
        new SelectItem("detailedFormat", "Detailed Physician Information"),
    };
               
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
    
    // get the current notesPreference
	protected void onCreate()
	{
    	criteria = new Physician();
        criteria.setSpecialty(Physician.FAMILY);
	}
	
	protected void onDestroy()
	{
	
	}
	
    /*
     * getter for the "form" bean
     */
    public Physician getCriteria()
    {
        return this.criteria;
    }
    
    //
    // Getters and setters for component values used in the search form
    //
    public String getPhysicianType()
    {
        return this.physicianType;
    }

    public void setPhysicianType(String type)
    {
        this.physicianType = type;
    }

	public String getResultFormatType()
    {
        return this.resultFormatType;
    }
 
 	public void setResultFormatType(String type)
    {
        this.resultFormatType = type;
    }
    
    /**
     * the specialist menu is enabled when the user chooses
     * specialist from the physician type selector
     */
    public boolean getSpecialistsDisabled()
    {
        return this.specialistsDisabled;
    }
    
    private void setSpecialistsDisabled(boolean value)
    {
        this.specialistsDisabled = value;
    }
    
	//
    // getters for all the selector components
    //
    public SelectItem[] getPhysicianTypes()
    {
        return this.physicianTypes;
    }
    
    public SelectItem[] getSpecialistTypes()
    {
        return this.specialistTypes;
    }
    
    public SelectItem[] getCities()
    {
        return this.cities;
    }

	public SelectItem[] getResultFormatTypes()
    {
        return this.resultFormatTypes;
    }
 
    // handler for selector interactions
    public void physicianTypeChange(javax.faces.event.ValueChangeEvent vce)
    {
        // get the new value from the component
        if (vce.getNewValue().equals(Physician.FAMILY))
        {
			setSpecialistsDisabled(true);
        }
        else
        {
			setSpecialistsDisabled(false);
        }
        
        // bypass validation of other components by jumping to render response 
        getFacesContext().renderResponse();
    }

    /**
     * Action handler for the search command
     * Pass the search form to one of two Page Flow actions,
     * based on the users choice of results format
     */
    @ti.commandHandler(
        raiseActions = {
            @ti.raiseAction(action="displayPhysiciansWithDetail", outputFormBean="searchForm"),
            @ti.raiseAction(action="displayPhysiciansAbbreviated", outputFormBean="searchForm")
        }
    )
    public String execute()
    {
        // if the physician type is set to "Family Practicioner" ignore the value of specialty
        if (physicianType.equals(Physician.FAMILY))
            criteria.setSpecialty(Physician.FAMILY);

		// put the criteria into the form that is passed to the page flow actions
        searchForm.setSearchCriteria(criteria);

        // If the user wants the detail format for results
        // we use a different page to present the results
        if (resultFormatType.equals("detailedFormat"))
        {
        	return "displayPhysiciansWithDetail";
        }
        else	// abbreviated results
        {
        	return "displayPhysiciansAbbreviated";
        }
    }
    
    public String sortByLastName()
    {
    	pageFlow.sortByLastName();
    	return null;	// stay on the page
    }
    
    public String sortByGender()
    {
    	pageFlow.sortByGender();
    	return null;	// stay on the page
    }
}
