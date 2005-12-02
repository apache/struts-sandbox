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

import org.apache.beehive.samples.netui.jsf.data.AddressData;
import org.apache.beehive.samples.netui.jsf.data.PhysicianData;

import java.io.Serializable;

public class Physician implements Serializable
{
    public static final String FAMILY = "family";
    public static final String SPECIALIST = "specialist";

    // sub objects not visible outside of package
    private PhysicianData physician;
    private AddressData address;

    public Physician()
    {
        initialize();
    }
    
    public Physician(String specialty, String gender, String firstName, String lastName)
    {
        initialize();
        
        physician.setSpecialty(specialty);
        physician.setGender(gender);
        physician.setFirstName(firstName);
        physician.setLastName(lastName);
        physician.setSchool("University of ...");
        String[] hospitals = {"General Hospital", "Some Other Hospital"};
        physician.setHospitalAffiliations(hospitals);
        physician.setBio("Dr. So and so ...");
    }
        
    protected void initialize()
    {
        this.physician = new PhysicianData();
        this.address = new AddressData();
    }

    public String getFirstName()
    {
        return physician.getFirstName();
    }

    public void setFirstName(String firstName)
    {
        physician.setFirstName(firstName);
    }

    public String getLastName()
    {
        return physician.getLastName();
    }

    public void setLastName(String lastName)
    {
        physician.setLastName(lastName);
    }

    public String getSpecialty()
    {
        return physician.getSpecialty();
    }

    public void setSpecialty(String specialty)
    {
        physician.setSpecialty(specialty);
    }

    public String getGender()
    {
        return physician.getGender();
    }
    
    public void setGender(String gender)
    {
        physician.setGender(gender);
    }

    public int getId()
    {
        return physician.getId();
    }

    public String getCity()
    {
        return address.getCity();
    }

    public void setCity(String value)
    {
        address.setCity(value);
    }
    
    public String getSchool()
    {
        return physician.getSchool();
    }

    public void setSchool(String value)
    {
    	physician.setSchool(value);
    }
    
    public String getBio()
    {
        return physician.getBio();
    }

    public void setBio(String value)
    {
    	physician.setBio(value);
    }
    
    public String[] getHospitalAffiliations()
    {
        return physician.getHospitalAffiliations();
    }

    public void setHospitalAffiliations(String[] value)
    {
    	physician.setHospitalAffiliations(value);
    }

}
