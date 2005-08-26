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
package pageflow.loginexample.start;

import org.apache.ti.pageflow.annotations.ti;
import org.apache.ti.samples.pageflow.loginexample.BaseFlow;

/**
 * Note that this page flow inherits actions and a handleException from BaseFlow.
 */
@ti.controller(
    simpleActions={
        @ti.simpleAction(name="goProtectedFlow", path="/pageflow/loginexample/protectedflow/Controller.jpf", loginRequired=true),
        @ti.simpleAction(name="protectedFlowDone", navigateTo=ti.NavigateTo.currentPage)
    }
)
public class Controller extends BaseFlow
{
}
