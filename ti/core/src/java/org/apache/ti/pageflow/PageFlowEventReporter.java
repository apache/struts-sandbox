/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.ti.pageflow;


/**
 * An event reporter, which will be notified of events like "page flow created", "action raised", etc.
 */
public abstract class PageFlowEventReporter {

    protected PageFlowEventReporter() {
    }

    /**
     * Event fired when an action is raised on a FlowController (a page flow or a shared flow).
     *
     * @param flowController the FlowController on which the action is being raised (a
     *                       {@link PageFlowController} or a {@link SharedFlowController}).
     */
    public abstract void actionRaised(FlowController flowController);

    /**
     * Event fired when an action successfully completes on a FlowController (a page flow or a shared flow).
     *
     * @param flowController  the FlowController on which the action was raised (a
     *                        {@link PageFlowController} or a {@link SharedFlowController}).
     * @param result          the forward result returned from the action.
     * @param timeTakenMillis the length of time in milliseconds for the action to be run.
     */
    public abstract void actionSuccess(FlowController flowController, Forward result, long timeTakenMillis);

    /**
     * Event fired when an exception is raised during processing of an action request.
     *
     * @param ex             the Throwable that was raised.
     * @param flowController the FlowController associated with the action request.  This parameter will be
     *                       <code>null</code> if the request did not get to the point where a FlowController could be created or
     *                       looked up.
     * @see #beginActionRequest
     */
    public abstract void exceptionRaised(Throwable ex, FlowController flowController);

    /**
     * Event fired when an exception is handled successfully during processing of an action request.
     *
     * @param ex              the Throwable that was raised.
     * @param flowController  the FlowController associated with the action request.  This parameter will be
     *                        <code>null</code> if the request did not get to the point where a FlowController could be created or
     *                        looked up.
     * @param result          the forward result returned from the exception handler.
     * @param timeTakenMillis the length of time in milliseconds for the exception to be handled.
     * @see #beginActionRequest
     */
    public abstract void exceptionHandled(Throwable ex, FlowController flowController, Forward result,
                                          long timeTakenMillis);

    /**
     * Event fired when a FlowController (a page flow or a shared flow) is created.
     *
     * @param flowController the FlowController (a {@link PageFlowController} or a {@link SharedFlowController})
     *                       that was created.
     */
    public abstract void flowControllerCreated(FlowController flowController);

    /**
     * Event fired when a FlowController (a page flow or a shared flow) is "destroyed", i.e., removed from wherever it
     * is being stored.
     *
     * @param flowController  the FlowController (a {@link PageFlowController} or a {@link SharedFlowController})
     *                        that is being destroyed.
     * @param storageLocation The storage location.  For session-scoped FlowControllers, this is a
     *                        <code>javax.servlet.http.HttpSession</code>.
     */
    public abstract void flowControllerDestroyed(FlowController flowController, Object storageLocation);

    /**
     * Event fired at the beginning of an action request.  Note that this is called on all action requests, even those
     * that do not successfully run actions.
     */
    public abstract void beginActionRequest();

    /**
     * Event fired at the end of an action request.  Note that this is called on all action requests, even those
     * that do not successfully run actions.
     *
     * @param timeTakenMillis the length of time in milliseconds for the action request to be processed.
     */
    public abstract void endActionRequest(long timeTakenMillis);

    /**
     * Event fired at the end of an action request.  Note that this is called on all action requests, even those
     * that do not successfully run actions.
     */
    public abstract void beginPageRequest();

    /**
     * Event fired at the end of a page request.
     *
     * @param timeTakenMillis the length of time in milliseconds for the page request to be processed.
     */
    public abstract void endPageRequest(long timeTakenMillis);

    /**
     * Event fired when a page flow or shared flow is registered lazily (once per webapp deployment).
     *
     * @param moduleConfig the {@link ModuleConfig} associated with the controller.
     */
    public abstract void flowControllerRegistered(ModuleConfig moduleConfig);
}
