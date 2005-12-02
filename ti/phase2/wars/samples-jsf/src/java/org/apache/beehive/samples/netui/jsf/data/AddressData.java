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

public class AddressData
{
    private String street = "";
    private String city = "";
    private String state = "";
    private String zip = "";


    public void setState(String value)
    {
        state = value;
    }

    public void setCity(String value)
    {
        city = value;
    }

    public String getState()
    {
        return state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String value)
    {
        zip = value;
    }

    public String getCity()
    {
        return city;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String value)
    {
        street = value;
    }

    // pass in the actual address and the criteria for the address
    public boolean isMatch(AddressData address, AddressData criteria)
    {
        assert (address != null);
        assert (criteria != null);
        
        boolean match = false;
        
        if ((criteria.getCity() == null) ||
                (criteria.getCity().equalsIgnoreCase(address.getCity())))
        {
            match = true;
        }
        
        return match;
    }
}
