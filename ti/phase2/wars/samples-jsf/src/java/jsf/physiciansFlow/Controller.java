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

import java.io.Serializable;

import javax.faces.model.DataModel;

import org.apache.ti.pageflow.FormData;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.annotations.ti;
import org.apache.ti.util.type.TypeUtils;

import org.apache.beehive.samples.netui.jsf.physician.Physician;
import org.apache.beehive.samples.netui.jsf.physician.PhysicianSession;

/**
 * This page flow controller for the physicians page flow.
 */
@ti.controller(
    simpleActions={
        @ti.simpleAction(name="physicianSearch", path="begin_success.faces"),
        @ti.simpleAction(name="returnToPreviousPage", navigateTo=ti.NavigateTo.previousPage)
    },
    sharedFlowRefs={
        @ti.sharedFlowRef(name="shared", type=org.apache.ti.samples.jsf.SharedFlow.class)
    },
    defaultResultSuffix="_success.faces"
)
public class Controller
    extends PageFlowController
{
	//
    // Data Retrieval
    //

    // this object retrieves the data
    private transient PhysicianSession physicianSession = null;
    
    // the data model is used across pages so it is kept in the
    // page flow rather than passed via an Action Output on the request
    private transient DataModel results = null;

    public DataModel getResults() {
        return results;
    }

    void setResults(DataModel value) {
        results = value;
    }
    
    /**
     * find physicians based on criteria
     */
    protected void searchPhysicians(Physician criteria)
    {
    	physicianSession.setSearchCriteria(criteria);
        results = physicianSession.getSortedPhysiciansModel();
    }
    
    public void sortByGender()
    {
        // change the sort key
        physicianSession.setSortByGender();
        results = physicianSession.getSortedPhysiciansModel();
	}
	
    public void sortByLastName()
    {
        // change the sort key
        physicianSession.setSortByLastName();
        results = physicianSession.getSortedPhysiciansModel();
	}

    //
    // Page Flow Lifecycle
    //
    
    /**
     * Callback that is invoked when this controller instance is created.
     */
    protected void onCreate()
    {
        this.physicianSession = new PhysicianSession();
    }

    //
    // Page Flow Actions
    //
    protected Forward physicianDetail()
    {
        Forward success = new Forward("success");
        String paramValue = (String) getContext().getWebContext().getParam().get("physicianId");
        int id = TypeUtils.convertToInt(paramValue);
        Physician physician = physicianSession.getPhysician(id);
        success.addActionOutput("physician", physician);
        return success;
    }


    /*
     * This action is equivalent to the "physicianDetail" action above
     * but the row data (physician) is gotten directly from the JSF DataModel.
     */
    @ti.forward(name="success", path="physicianDetail_success.faces")
    protected Forward physicianDetailJSFStyle()
    {
        Forward success = new Forward("success");

		assert(this.results != null);
		Physician physician = (Physician)results.getRowData();
        success.addActionOutput("physician", physician);
        return success;
    }

    @ti.forward(name="success", navigateTo=ti.NavigateTo.currentPage)
    protected Forward displayPhysiciansAbbreviated(PhysicianSearchForm form)
    {
        Forward success = new Forward("success");
        searchPhysicians(form.getSearchCriteria());
        return success;
    }
    
    protected Forward displayPhysiciansWithDetail(PhysicianSearchForm form)
    {
        Forward success = new Forward("success");
        searchPhysicians(form.getSearchCriteria());
        return success;
    }
    
    protected Forward submitMailMessage(MailMessageForm form)
    {
        Forward success = new Forward("success");
        success.addActionOutput("mailMessage", form.getMessage());
        success.addActionOutput("firstName", form.getPhysician().getFirstName());
        success.addActionOutput("lastName", form.getPhysician().getLastName());
        return success;
    }
    
    //
    // Form Beans
    //
    public static class MailMessageForm implements Serializable
    {
        private Physician physician;
        private String message;
 
        public void setMessage(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return this.message;
        }

        public void setPhysician(Physician physician)
        {
            this.physician = physician;
        }

        public Physician getPhysician()
        {
            return this.physician;
        }
    }
    
    public static class PhysicianSearchForm implements Serializable
    {
        private Physician searchCriteria;
 
        public void setSearchCriteria(Physician criteria)
        {
            this.searchCriteria = criteria;
        }

        public Physician getSearchCriteria()
        {
            return this.searchCriteria;
        }
    }
}
