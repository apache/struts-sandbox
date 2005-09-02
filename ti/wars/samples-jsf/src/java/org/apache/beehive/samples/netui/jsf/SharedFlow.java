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
package org.apache.beehive.samples.netui.jsf;

import java.io.IOException;

import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.SharedFlowController;
import org.apache.ti.pageflow.annotations.ti;

@ti.controller(
	simpleActions={
	    @ti.simpleAction(name="home", path="/jsf/Controller.jpf")
	}
)

public class SharedFlow
    extends SharedFlowController
{
	private boolean notesPreference = true;
	
	public void setNotesPreference(boolean value)
	{
		this.notesPreference = value;
	}
	
	public boolean getNotesPreference()
	{
		return this.notesPreference;
	}
}
