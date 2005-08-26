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
package org.apache.ti.pageflow.interceptor;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for all chains of {@link Interceptor}s.
 */
public abstract class InterceptorChain {

    private LinkedList/*< Interceptor >*/ _chain = new LinkedList/*< Interceptor >*/();
    private InterceptorContext _context;

    protected InterceptorChain(InterceptorContext context, List/*< Interceptor >*/ interceptors) {
        _context = context;
        _chain.addAll(interceptors);
    }

    public Object continueChain()
            throws InterceptorException {
        if (!_chain.isEmpty()) {
            return invoke((Interceptor) _chain.removeFirst());
        } else {
            return null;
        }
    }

    protected abstract Object invoke(Interceptor interceptor) throws InterceptorException;

    public InterceptorContext getContext() {
        return _context;
    }

    public boolean isEmpty() {
        return _chain.isEmpty();
    }

    protected Interceptor removeFirst() {
        return (Interceptor) _chain.removeFirst();
    }
}
