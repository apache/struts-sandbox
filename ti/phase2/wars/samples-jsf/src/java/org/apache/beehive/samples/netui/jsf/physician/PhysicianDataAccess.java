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

public class PhysicianDataAccess
{ 
    private boolean initialized = false;
    private ArrayList results;
    private ArrayList data;
    
    public PhysicianDataAccess()
    {
        initialize();    
    }
    
    void initialize()
    {
        if (initialized == false)
        {
            data = new ArrayList();
        
            addPhysician(new Physician("Ear", "female", "Doc", "Watson"));
            addPhysician(new Physician("Nose", "male", "Doctor", "Watson"));
            addPhysician(new Physician("Throat", "female", "Ima", "Cutter"));
            addPhysician(new Physician("Throat", "female", "Lets", "Look"));
            addPhysician(new Physician("Ear", "male", "Bill", "Later"));
            addPhysician(new Physician("Nose", "female", "Pay", "Now"));
            addPhysician(new Physician("Nose", "female", "Sue", "Mee"));
            addPhysician(new Physician("Ear", "female", "Noah", "Charge"));
            addPhysician(new Physician("Ear", "female", "Coe", "Pay"));
            addPhysician(new Physician("Throat", "female", "Fran", "Tic"));
            addPhysician(new Physician("Ear", "male", "Carl", "Later"));
            addPhysician(new Physician("Ear", "female", "Kidney", "Stone"));
            addPhysician(new Physician("Ear", "female", "Ann ", "Esthesia"));
            addPhysician(new Physician("Throat", "male", "Serge", "Ree"));
            addPhysician(new Physician("Nose", "male", "Al", "Imentary"));
            addPhysician(new Physician("Nose", "male", "Hal", "Itosis"));
            addPhysician(new Physician("Nose", "female", "Gerri", "Atric"));
            addPhysician(new Physician("Family", "female", "Jane", "Doe"));
            addPhysician(new Physician("Family", "male", "Bill E", "Rubin"));
            addPhysician(new Physician("Family", "male", "Serge", "Ree"));
	        addPhysician(new Physician("Nose", "male", "Cy", "Cosis"));
            addPhysician(new Physician("Ear", "female", "Dee", "Pression"));
            addPhysician(new Physician("Family", "male", "Lance", "It"));
            addPhysician(new Physician("Nose", "male", "Ed", "Eema"));
            addPhysician(new Physician("Ear", "female", "Julie", "Plumber"));
            addPhysician(new Physician("Throat", "female", "Marie", "Jones"));

            for (int i=0; i<data.size(); i++)
            {
                Physician p = (Physician)data.get(i);
                
                if (i%2 == 0)
                    p.setCity("Boulder");
                else
                    p.setCity("Denver");
            }
    
            initialized = true;
        }
    }
    
    ArrayList getPhysicians(Physician criteria) 
    {
        initialize();
        
        // new search - no results yet
        results = null;
    
        for (int i = 0; i < data.size(); i++)
        {   
            Physician doc = (Physician)data.get(i);
            
            boolean match = false;
            if ((criteria.getSpecialty() == null) ||
                (criteria.getSpecialty().equalsIgnoreCase(doc.getSpecialty())))
            {   
                match = true;
            }
            
            if (match == true)
            {
                match = false;
                    if ((criteria.getCity() == null) ||
                    (criteria.getCity().equalsIgnoreCase(doc.getCity())))
                {
                    match = true;
                }
            }
            
            // add the match to the results
            if (match == true)
            {
                if (results == null)
                    results = new ArrayList();
                results.add(doc);
            }
        }

        return results;
    }

    Physician getPhysician(int id)
    {
        initialize();
        Physician physician = null;

        for (int i = 0; i < data.size(); i++)
        {
            Physician p = (Physician)data.get(i);
            if (p.getId() == id)
            {
                physician = p;
                break;
            }
        }

        return physician;
    }
    
    void addPhysician(Physician physician)
    {
        data.add(physician);

    }
} 
