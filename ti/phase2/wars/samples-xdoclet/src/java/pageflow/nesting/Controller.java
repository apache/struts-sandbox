/* 
   Copyright 2005 The Apache Software Foundation 

   Licensed under the Apache License , Version 2.0 (the "License" );
   you may not use this file except in compliance with the License. 
   You may obtain a copy of the License at 

      http://www.apache.org/licenses/LICENSE-2.0 

   Unless required by applicable law or agreed to in writing , software 
   distributed under the License is distributed on an "AS IS" BASIS ,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND , either express or implied. 
   See the License for the specific language governing permissions and 
   limitations under the License. 

   $Header:$ 
*/ 
package pageflow.nesting ;

import org.apache.ti.pageflow.PageFlowController ;
import org.apache.ti.pageflow.Forward ;
 
import pageflow.nesting.chooseAirport.ChooseAirport ;
import com.opensymphony.xwork.Action ;

/** 
 * Main page flow , which invokes a nested page flow and gets data back from it. 
 */ 
/**
 * @ti.controller

   // This action runs the Choose Airport wizard.  Since the ChooseAirport page flow is marked
   // with nested=true, it will be able to return control to this page flow when it is done.
 * @ti.simpleAction name="chooseAirport" path="/pageflow/nesting/chooseAirport/ChooseAirport.jpf"

   // This action is raised by the ChooseAirport page flow  The navigateTo attribute here
   // causes flow to return to the current page in this page flow.
 * @ti.simpleAction name="chooseAirportCancelled" navigateTo="currentPage"
 */ 
public class Controller extends PageFlowController 
{
    // This property ("yourName" ) is used in the JSPs to show that this page flow's state is 
    // restored when you return from the nested flow. 
    private String _yourName ;
    public String getYourName () { return _yourName ; }
    public void setYourName (String yourName ) { _yourName = yourName ; }

    /**
     * @ti.action
     */ protected Forward chooseAirportDone ( ChooseAirport.Results results )
    {
        Forward fwd = new Forward (Action.SUCCESS );
        fwd.addActionOutput ("airport" , results.getAirport ());
        return fwd ;
    }
}
