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
package org.apache.beehive.samples.netui.jsf.data;

import java.io.Serializable;

public class PhysicianData implements Serializable
{
    // this is our counter to generate id's
    private static int identifier = 0;

    private int id;
    private String specialty;
    private String gender;
    private String firstName;
    private String lastName;
    private	String[] hospitalAffiliations;
    private String school;
    private String bio;

    public PhysicianData()
    {
        initialize();
    }
    
    public PhysicianData(String specialty, String gender, String firstName, String lastName)
    {
        initialize();
        
        this.specialty = specialty;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
    }
        
    protected void initialize()
    {
        this.id = identifier++;
    }

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String[] getHospitalAffiliations() {
		return hospitalAffiliations;
	}

	public void setHospitalAffiliations(String[] hospitalAffiliations) {
		this.hospitalAffiliations = hospitalAffiliations;
	}

	public String getSchool() {
		return school;
	}


	public void setSchool(String school) {
		this.school = school;
	}
	
    public String getFirstName()
    {
        return this.firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getSpecialty()
    {
        return this.specialty;
    }

    public void setSpecialty(String specialty)
    {
        this.specialty = specialty;
    }

    public String getGender()
    {
        return this.gender;
    }
    
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public int getId()
    {
        return id;
    }
}
