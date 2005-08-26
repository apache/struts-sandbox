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

import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

/**
 * Default event reporter.  Logs every event when the log level is set to "debug" or "trace".
 */
public class DefaultPageFlowEventReporter
        extends PageFlowEventReporter {

    private static final Logger _log = Logger.getInstance(DefaultPageFlowEventReporter.class);

    protected DefaultPageFlowEventReporter() {
        super();
    }

    public void actionRaised(FlowController flowController) {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("actionRaised");
            msg.addParam("FlowController", flowController);
            msg.addParam("ActionName", context.getName());
            msg.addParam("formBean", context.getFormBean());
            msg.addParam("Request", context.getRequestPath());
            _log.debug(msg);
        }
    }

    public void actionSuccess(FlowController flowController, Forward result, long timeTakenMillis) {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("actionSuccess");
            msg.addParam("FlowController", flowController);
            msg.addParam("ActionName", context.getName());
            msg.addParam("formBean", context.getFormBean());
            msg.addParam("Request", context.getRequestPath());
            msg.addParam("forward", result);
            msg.addParam("TimeTakenMillis", new Long(timeTakenMillis));
            _log.debug(msg);
        }
    }

    public void exceptionRaised(Throwable ex, FlowController flowController) {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("exceptionRaised");
            msg.addParam("Throwable", ex);
            msg.addParam("ActionName", context.getName());
            msg.addParam("formBean", context.getFormBean());
            msg.addParam("FlowController", flowController);
            msg.addParam("Request", context.getRequestPath());
            _log.debug(msg);
        }
    }

    public void exceptionHandled(Throwable ex, FlowController flowController, Forward result,
                                 long timeTakenMillis) {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("exceptionHandled");
            msg.addParam("Throwable", ex);
            msg.addParam("ActionName", context.getName());
            msg.addParam("formBean", context.getFormBean());
            msg.addParam("FlowController", flowController);
            msg.addParam("Request", context.getRequestPath());
            msg.addParam("forward", result);
            msg.addParam("TimeTakenMillis", new Long(timeTakenMillis));
            _log.debug(msg);
        }
    }

    public void flowControllerCreated(FlowController flowController) {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("flowControllerCreated");
            msg.addParam("FlowController", flowController);
            msg.addParam("Request", context.getRequestPath());
            _log.debug(msg);
        }
    }

    public void flowControllerDestroyed(FlowController flowController, Object storageLocation) {
        if (_log.isDebugEnabled()) {
            LogMsg msg = new LogMsg("flowControllerDestroyed");
            msg.addParam("FlowController", flowController);
            msg.addParam("StorageLocation", storageLocation);
            _log.debug(msg);
        }
    }

    public void beginActionRequest() {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("beginActionRequest");
            msg.addParam("Request", context.getRequestPath());
            _log.debug(msg);
        }
    }

    public void endActionRequest(long timeTakenMillis) {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("endActionRequest");
            msg.addParam("Request", context.getRequestPath());
            msg.addParam("TimeTakenMillis", new Long(timeTakenMillis));
            _log.debug(msg);
        }
    }

    public void beginPageRequest() {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("beginPageRequest");
            msg.addParam("Request", context.getRequestPath());
            _log.debug(msg);
        }
    }

    public void endPageRequest(long timeTakenMillis) {
        if (_log.isDebugEnabled()) {
            PageFlowActionContext context = PageFlowActionContext.get();
            LogMsg msg = new LogMsg("endPageRequest");
            msg.addParam("Request", context.getRequestPath());
            msg.addParam("TimeTakenMillis", new Long(timeTakenMillis));
            _log.debug(msg);
        }
    }

    public void flowControllerRegistered(ModuleConfig moduleConfig) {
        if (_log.isDebugEnabled()) {
            LogMsg msg = new LogMsg("flowControllerRegistered");
            msg.addParam("Namespace", moduleConfig.getNamespace());
            msg.addParam("ControllerClassName", moduleConfig.getControllerClassName());
            msg.addParam("ModuleConfig", moduleConfig);
            _log.debug(msg);
        }
    }

    protected static class LogMsg {

        private String _eventName;
        private InternalStringBuilder _logMessage;

        public LogMsg(String eventName) {
            _eventName = eventName;
        }

        public void addParam(String name, Object value) {
            if (_logMessage == null) {
                _logMessage = new InternalStringBuilder(_eventName).append(": ");
            } else {
                _logMessage.append(", ");
            }

            _logMessage.append(name).append('=').append(value);
        }

        public String toString() {
            return _logMessage == null ? _eventName : _logMessage.toString();
        }
    }
}
