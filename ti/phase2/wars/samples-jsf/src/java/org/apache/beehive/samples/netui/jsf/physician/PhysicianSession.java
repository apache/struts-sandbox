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
package org.apache.beehive.samples.netui.jsf.physician; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public class PhysicianSession
{
	private static final int SORT_BY_LASTNAME = 0;
	private static final int SORT_BY_GENDER = 1;
    
	// default is sort by name ascending
    private boolean ascending = true;
    private int sortBy = SORT_BY_LASTNAME;
    
    private PhysicianDataAccess dataAccess = null;
	private Physician searchCriteria = null;
    
    private DataModel physiciansModel = null;
	
    // no arg constructor
    public PhysicianSession()
    {
        this.dataAccess = new PhysicianDataAccess();
    }
	
	public void setSearchCriteria(Physician criteria)
	{
		this.searchCriteria = criteria;
	}
	
	public Physician getSearchCriteria()
	{
		return this.searchCriteria;
	}
    
    public void addPhysician(Physician physician)
    {
        this.dataAccess.addPhysician(physician);
    }
    
    public Physician getPhysician(int id)
    {
        assert(this.dataAccess != null);
        return this.dataAccess.getPhysician(id);
    }
    
    public ArrayList getPhysicians(Physician criteria)
    {
        return this.dataAccess.getPhysicians(criteria);
    }
	
	public DataModel getSortedPhysiciansModel()
	{		
		assert(this.searchCriteria != null);
		ArrayList physicians = dataAccess.getPhysicians(searchCriteria);
		sortPhysicians(physicians);
		
		if (physiciansModel == null)
        {
            physiciansModel = new ListDataModel();
		}
		
		physiciansModel.setWrappedData(physicians);
		
		return this.physiciansModel;
	}
	
    /**
     * sort utilities
     */
    private static final Comparator ASC_LASTNAME_COMPARATOR = new Comparator()
    {
        public int compare (Object o1, Object o2)
        {
            String s1 = ((Physician)o1).getLastName();
            String s2 = ((Physician)o2).getLastName();
            return s1.compareTo(s2);
        }
    };
	
    private static final Comparator DESC_LASTNAME_COMPARATOR = new Comparator()
    {
        public int compare (Object o1, Object o2)
        {
            String s1 = ((Physician)o1).getLastName();
            String s2 = ((Physician)o2).getLastName();
            return s2.compareTo(s1);
        }
    };
   
    private static final Comparator ASC_GENDER_COMPARATOR = new Comparator()
    {
        public int compare (Object o1, Object o2)
        {
            String s1 = ((Physician)o1).getGender();
            String s2 = ((Physician)o2).getGender();
            return s1.compareTo(s2);
        }
    };
	
    private static final Comparator DESC_GENDER_COMPARATOR = new Comparator()
    {
        public int compare (Object o1, Object o2)
        {
            String s1 = ((Physician)o1).getGender();
            String s2 = ((Physician)o2).getGender();
            return s2.compareTo(s1);
        }
    };

	private void sortPhysicians(ArrayList physicians)
    {
        switch (sortBy)
        {
            case SORT_BY_LASTNAME:
                 Collections.sort(physicians,
                    ascending ? ASC_LASTNAME_COMPARATOR : DESC_LASTNAME_COMPARATOR);
                break;
            case SORT_BY_GENDER:
                Collections.sort(physicians,
                   ascending ? ASC_GENDER_COMPARATOR : DESC_GENDER_COMPARATOR);
               break;
        }
    }
    
    public String setSortByLastName()
    {
        if (sortBy == SORT_BY_LASTNAME)
        {
            ascending = !ascending;
        }
        else
        {
            sortBy = SORT_BY_LASTNAME;
            ascending = true;
        }
        return "success";
    }

    public String setSortByGender()
    {
        if (sortBy == SORT_BY_GENDER)
        {
            ascending = !ascending;
        }
        else
        {
            sortBy = SORT_BY_GENDER;
            ascending = true;
        }
        return "success";
    }
} 
