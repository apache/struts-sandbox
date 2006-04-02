/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.flow.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;

//import org.apache.avalon.framework.configuration.Configurable;
//import org.apache.avalon.framework.configuration.Configuration;
//import org.apache.avalon.framework.context.Context;
//import org.apache.avalon.framework.context.ContextException;
//import org.apache.avalon.framework.context.Contextualizable;
//import org.apache.avalon.framework.logger.AbstractLogEnabled;
//import org.apache.avalon.framework.service.ServiceException;
//import org.apache.avalon.framework.service.ServiceManager;
//import org.apache.avalon.framework.service.Serviceable;
//import org.apache.avalon.framework.thread.ThreadSafe;
//import org.apache.cocoon.components.ContextHelper;
//import org.apache.cocoon.components.thread.RunnableManager;
//import org.apache.cocoon.environment.ObjectModelHelper;
//import org.apache.cocoon.environment.Request;
//import org.apache.cocoon.environment.Session;


/**
 * The default implementation of {@link ContinuationsManager}. <br/>There are
 * two modes of work: <br/>
 * <ul>
 * <li><b>standard mode </b>- continuations are stored in single holder. No
 * security is applied to continuation lookup. Anyone can invoke a continuation
 * only knowing the ID. Set "session-bound-continuations" configuration option
 * to false to activate this mode.</li>
 * <li><b>secure mode </b>- each session has it's own continuations holder. A
 * continuation is only valid for the same session it was created for. Session
 * invalidation causes all bound continuations to be invalidated as well. Use
 * this setting for web applications. Set "session-bound-continuations"
 * configuration option to true to activate this mode.</li>
 * </ul>
 * 
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu </a>
 * @author <a href="mailto:Michael.Melhem@managesoft.com">Michael Melhem </a>
 * @since March 19, 2002
 * @see ContinuationsManager
 * @version CVS $Id: ContinuationsManagerImpl.java 293111 2005-10-02 13:39:20Z reinhard $
 */
public class ContinuationsManagerImpl implements ContinuationsManager  {

    static final int CONTINUATION_ID_LENGTH = 20;
    static final String EXPIRE_CONTINUATIONS = "expire-continuations";

    /**
     * Random number generator used to create continuation ID
     */
    protected SecureRandom random;
    protected byte[] bytes;

    /**
     * How long does a continuation exist in memory since the last
     * access? The time is in miliseconds, and the default is 1 hour.
     */
    protected int defaultTimeToLive = 3600 * 1000;

    /**
     * Maintains the forest of <code>WebContinuation</code> trees.
     * This set is used only for debugging puroses by
     * {@link #displayAllContinuations()} method.
     */
    protected Set forest = Collections.synchronizedSet(new HashSet());

    /**
     * Main continuations holder. Used unless continuations are stored in user
     * session.
     */
    protected WebContinuationsHolder continuationsHolder;
    
    /**
     * Sorted set of <code>WebContinuation</code> instances, based on
     * their expiration time. This is used by the background thread to
     * invalidate continuations.
     */
    protected SortedSet expirations = Collections.synchronizedSortedSet(new TreeSet());

    protected boolean bindContinuationsToSession;

    private Thread expireThread;
    
    private long expirePeriod = 180000;

    public ContinuationsManagerImpl() throws Exception {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch(java.security.NoSuchAlgorithmException nsae) {
            // Maybe we are on IBM's SDK
            random = SecureRandom.getInstance("IBMSecureRandom");
        }
        random.setSeed(System.currentTimeMillis());
        bytes = new byte[CONTINUATION_ID_LENGTH];
        expireThread = new Thread(
            new Runnable() {
                public void run() {
                    boolean shouldKeepRunning = true;
                    while (shouldKeepRunning) {
                        try {
                            Thread.sleep(expirePeriod);
                        } catch (InterruptedException ex) {
                            getLogger().debug("Continuation expiration thread interrupted");
                            shouldKeepRunning = false;
                        }
                        if (shouldKeepRunning) {
                            expireContinuations();
                        }
                    }
                }
            });

        getLogger().debug("Starting continuation expiration thread");
        expireThread.setName("Flow continuations expiration thread");
        expireThread.setPriority(Thread.MIN_PRIORITY);
        expireThread.start();
        
        this.continuationsHolder = new WebContinuationsHolder();
    }
    
    
    /**
     *  Gets the logger 
     *
     *@return    The logger value
     */
    public Logger getLogger() {
        return Factory.getLogger();
    }
    
    
    /**
     *  Set the default time to live value
     *
     *@param  ttl  The time-to-live in milliseconds
     */
    public void setDefaultTimeToLive(int ttl) {
        this.defaultTimeToLive = ttl;
    }
    
    public void setExpirationPeriod(long period) {
        this.expirePeriod = period;
    }
    
    public void setBindContinuationsToSession(boolean bind) {
        this.bindContinuationsToSession = bind;
        if (this.bindContinuationsToSession) {
            this.continuationsHolder = null;
        }
    }


    public WebContinuation createWebContinuation(Object kont,
                                                 WebContinuation parent,
                                                 int timeToLive,
                                                 String interpreterId, 
                                                 ContinuationsDisposer disposer,
                                                 WebContext webctx) {
        int ttl = (timeToLive == 0 ? defaultTimeToLive : timeToLive);

        WebContinuation wk = generateContinuation(kont, parent, ttl, interpreterId, disposer, webctx);
        //wk.enableLogging(getLogger());

        if (parent == null) {
            forest.add(wk);
        } else {
            handleParentContinuationExpiration(parent);
        }

        handleLeafContinuationExpiration(wk);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("WK: Created continuation " + wk.getId());
        }

        return wk;
    }
    
    /**
     * When a new continuation is created in @link #createWebContinuation(Object, WebContinuation, int, String, ContinuationsDisposer),
     * it is registered in the expiration set in order to be evaluated by the invalidation mechanism.
     */
    protected void handleLeafContinuationExpiration(WebContinuation wk) {
        expirations.add(wk);
    }

    /**
     * When a new continuation is created in @link #createWebContinuation(Object, WebContinuation, int, String, ContinuationsDisposer),
     * its parent continuation is removed from the expiration set. This way only leaf continuations are part of
     * the expiration set.
     */
    protected void handleParentContinuationExpiration(WebContinuation parent) {
        if (parent.getChildren().size() < 2) {
            expirations.remove(parent);
        }
    }    
    
    /**
     * Get a list of all web continuations (data only)
     */
    public List getWebContinuationsDataBeanList() {
        List beanList = new ArrayList();
        for(Iterator it = this.forest.iterator(); it.hasNext();) {
            beanList.add(new WebContinuationDataBean((WebContinuation) it.next()));
        }
        return beanList;
    }

    public WebContinuation lookupWebContinuation(String id, String interpreterId, WebContext webctx) {
        // REVISIT: Is the following check needed to avoid threading issues:
        // return wk only if !(wk.hasExpired) ?
        WebContinuationsHolder continuationsHolder = lookupWebContinuationsHolder(false, webctx);
        if (continuationsHolder == null)
            return null;
        
        WebContinuation kont = continuationsHolder.get(id);
        if (kont == null)
            return null;
            
        if (!kont.interpreterMatches(interpreterId)) {
            getLogger().error(
                    "WK: Continuation (" + kont.getId()
                            + ") lookup for wrong interpreter. Bound to: "
                            + kont.getInterpreterId() + ", looked up for: "
                            + interpreterId);
            return null;
        }
        return kont;
    }

    /**
     * Create <code>WebContinuation</code> and generate unique identifier for
     * it. The identifier is generated using a cryptographically strong
     * algorithm to prevent people to generate their own identifiers.
     * 
     * <p>
     * It has the side effect of interning the continuation object in the
     * <code>idToWebCont</code> hash table.
     * 
     * @param kont
     *            an <code>Object</code> value representing continuation
     * @param parent
     *            value representing parent <code>WebContinuation</code>
     * @param ttl
     *            <code>WebContinuation</code> time to live
     * @param interpreterId
     *            id of interpreter invoking continuation creation
     * @param disposer
     *            <code>ContinuationsDisposer</code> instance to use for
     *            cleanup of the continuation.
     * @return the generated <code>WebContinuation</code> with unique
     *         identifier
     */
    protected WebContinuation generateContinuation(Object kont,
                                                 WebContinuation parent,
                                                 int ttl,
                                                 String interpreterId,
                                                 ContinuationsDisposer disposer,
                                                 WebContext webctx) {

        char[] result = new char[bytes.length * 2];
        WebContinuation wk = null;
        WebContinuationsHolder continuationsHolder = lookupWebContinuationsHolder(true, webctx);
        while (true) {
            random.nextBytes(bytes);

            for (int i = 0; i < CONTINUATION_ID_LENGTH; i++) {
                byte ch = bytes[i];
                result[2 * i] = Character.forDigit(Math.abs(ch >> 4), 16);
                result[2 * i + 1] = Character.forDigit(Math.abs(ch & 0x0f), 16);
            }

            final String id = new String(result);
            synchronized (continuationsHolder) {
                if (!continuationsHolder.contains(id)) {
                    if (this.bindContinuationsToSession)
                        wk = new HolderAwareWebContinuation(id, kont, parent,
                                ttl, interpreterId, disposer,
                                continuationsHolder);
                    else
                        wk = new WebContinuation(id, kont, parent, ttl,
                                interpreterId, disposer);
                    continuationsHolder.addContinuation(wk);
                    break;
                }
            }
        }

        return wk;
    }

    public void invalidateWebContinuation(WebContinuation wk, WebContext webctx) {
        WebContinuationsHolder continuationsHolder = lookupWebContinuationsHolder(false, webctx);
        if (!continuationsHolder.contains(wk)) {
            //TODO this looks like a security breach - should we throw?
            return;
        }
        _detach(wk);
        _invalidate(continuationsHolder, wk);
    }

    private void _invalidate(WebContinuationsHolder continuationsHolder, WebContinuation wk) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("WK: Manual expire of continuation " + wk.getId());
        }
        disposeContinuation(continuationsHolder, wk);
        expirations.remove(wk);

        // Invalidate all the children continuations as well
        List children = wk.getChildren();
        int size = children.size();
        for (int i = 0; i < size; i++) {
            _invalidate(continuationsHolder, (WebContinuation) children.get(i));
        }
    }

    /**
     * Detach this continuation from parent. This method removes
     * continuation from {@link #forest} set, or, if it has parent,
     * from parent's children collection.
     * @param continuationsHolder
     * @param wk Continuation to detach from parent.
     */
    protected void _detach(WebContinuation wk) {
        WebContinuation parent = wk.getParentContinuation();
        if (parent == null) {
            forest.remove(wk);
        } else 
            wk.detachFromParent();
    }

    /**
     * Makes the continuation inaccessible for lookup, and triggers possible needed
     * cleanup code through the ContinuationsDisposer interface.
     * @param continuationsHolder
     *
     * @param wk the continuation to dispose.
     */
    protected void disposeContinuation(WebContinuationsHolder continuationsHolder, WebContinuation wk) {
        continuationsHolder.removeContinuation(wk);
        wk.dispose();
    }

    /**
     * Removes an expired leaf <code>WebContinuation</code> node
     * from its continuation tree, and recursively removes its
     * parent(s) if it they have expired and have no (other) children.
     * @param continuationsHolder
     *
     * @param wk <code>WebContinuation</code> node
     */
    protected void removeContinuation(WebContinuationsHolder continuationsHolder,
            WebContinuation wk) {
        if (wk.getChildren().size() != 0) {
            return;
        }

        // remove access to this contination
        disposeContinuation(continuationsHolder, wk);
        _detach(wk);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("WK: Deleted continuation: " + wk.getId());
        }

        // now check if parent needs to be removed.
        WebContinuation parent = wk.getParentContinuation();
        if (null != parent && parent.hasExpired()) {
            //parent must have the same continuations holder, lookup not needed
            removeContinuation(continuationsHolder, parent);
        }
    }

    /**
     * Dump to Log file the current contents of
     * the expirations <code>SortedSet</code>
     */
    protected void displayExpireSet() {
        StringBuffer wkSet = new StringBuffer("\nWK; Expire set size: " + expirations.size());
        Iterator i = expirations.iterator();
        while (i.hasNext()) {
            final WebContinuation wk = (WebContinuation) i.next();
            final long lat = wk.getLastAccessTime() + wk.getTimeToLive();
            wkSet.append("\nWK: ")
                    .append(wk.getId())
                    .append(" ExpireTime [");

            if (lat < System.currentTimeMillis()) {
                wkSet.append("Expired");
            } else {
                wkSet.append(lat);
            }
            wkSet.append("]");
        }

        getLogger().debug(wkSet.toString());
    }

    /**
     * Dump to Log file all <code>WebContinuation</code>s
     * in the system
     */
    public void displayAllContinuations() {
        final Iterator i = forest.iterator();
        while (i.hasNext()) {
            ((WebContinuation) i.next()).display();
        }
    }

    /**
     * Remove all continuations which have already expired.
     */
    protected void expireContinuations() {
        long now = 0;
        if (getLogger().isDebugEnabled()) {
            now = System.currentTimeMillis();

            /* Continuations before clean up:
            getLogger().debug("WK: Forest before cleanup: " + forest.size());
            displayAllContinuations();
            displayExpireSet();
            */
        }

        // Clean up expired continuations
        int count = 0;
        WebContinuation wk;
        Iterator i = expirations.iterator();
        while (i.hasNext() && ((wk = (WebContinuation) i.next()).hasExpired())) {
            i.remove();
            WebContinuationsHolder continuationsHolder = null;
            if ( wk instanceof HolderAwareWebContinuation )
                continuationsHolder = ((HolderAwareWebContinuation) wk).getContinuationsHolder();
            else
                continuationsHolder = this.continuationsHolder;
            removeContinuation(continuationsHolder, wk);
            count++;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("WK Cleaned up " + count + " continuations in " +
                              (System.currentTimeMillis() - now));

            /* Continuations after clean up:
            getLogger().debug("WK: Forest after cleanup: " + forest.size());
            displayAllContinuations();
            displayExpireSet();
            */
        }
    }

    /**
     * Method used by WebContinuationsHolder to notify the continuations manager
     * about session invalidation. Invalidates all continuations held by passed
     * continuationsHolder.
     */
    protected void invalidateContinuations(
            WebContinuationsHolder continuationsHolder) {
        // TODO: this avoids ConcurrentModificationException, still this is not
        // the best solution and should be changed
        Object[] continuationIds = continuationsHolder.getContinuationIds()
                .toArray();
        
        for (int i = 0; i < continuationIds.length; i++) {
            WebContinuation wk = continuationsHolder.get(continuationIds[i]);
            if (wk != null) {
                _detach(wk);
                _invalidate(continuationsHolder, wk);
            }
        }
    }

    /**
     * Lookup a proper web continuations holder. 
     * @param createNew
     *            should the manager create a continuations holder in session
     *            when none found?
     */
    public WebContinuationsHolder lookupWebContinuationsHolder(boolean createNew, WebContext webctx) {
        //there is only one holder if continuations are not bound to session
        if (!this.bindContinuationsToSession)
            return this.continuationsHolder;
        
        //if continuations bound to session lookup a proper holder in the session
        if (!createNew && webctx instanceof ServletWebContext) {
            if (((ServletWebContext) webctx).getRequest().getSession(false) == null) {
                return null;
            }
        }

        WebContinuationsHolder holder = 
            (WebContinuationsHolder) webctx.getSessionScope().get(
                    WebContinuationsHolder.CONTINUATIONS_HOLDER);
        if (!createNew)
            return holder;

        if (holder != null)
            return holder;

        holder = new WebContinuationsHolder();
        webctx.getSessionScope().put(WebContinuationsHolder.CONTINUATIONS_HOLDER,
                holder);
        return holder;
    }

    /**
     * A holder for WebContinuations. When bound to session notifies the
     * continuations manager of session invalidation.
     */
    public class WebContinuationsHolder implements HttpSessionBindingListener {
        private final static String CONTINUATIONS_HOLDER = 
                                       "o.a.c.c.f.SCMI.WebContinuationsHolder";

        private Map holder = Collections.synchronizedMap(new HashMap());

        public WebContinuation get(Object id) {
            return (WebContinuation) this.holder.get(id);
        }

        public void addContinuation(WebContinuation wk) {
            this.holder.put(wk.getId(), wk);
        }

        public void removeContinuation(WebContinuation wk) {
            this.holder.remove(wk.getId());
        }

        public Set getContinuationIds() {
            return holder.keySet();
        }
        
        public boolean contains(String continuationId) {
            return this.holder.containsKey(continuationId);
        }
        
        public boolean contains(WebContinuation wk) {
            return contains(wk.getId());
        }

        public void valueBound(HttpSessionBindingEvent event) {
        }

        public void valueUnbound(HttpSessionBindingEvent event) {
            invalidateContinuations(this);
        }
    }

    /**
     * WebContinuation extension that holds also the information about the
     * holder. This information is needed to cleanup a proper holder after
     * continuation's expiration time.
     */
    protected class HolderAwareWebContinuation extends WebContinuation {
        private WebContinuationsHolder continuationsHolder;

        public HolderAwareWebContinuation(String id, Object continuation,
                WebContinuation parentContinuation, int timeToLive,
                String interpreterId, ContinuationsDisposer disposer,
                WebContinuationsHolder continuationsHolder) {
            super(id, continuation, parentContinuation, timeToLive,
                    interpreterId, disposer);
            this.continuationsHolder = continuationsHolder;
        }

        public WebContinuationsHolder getContinuationsHolder() {
            return continuationsHolder;
        }

        //retain comparation logic from parent
        public int compareTo(Object other) {
            return super.compareTo(other);
        }
    }
    
    
    /**  Destroys all continuations and any other resident objects  */
    public void destroy() {
        /*expirations.clear();
        Set clone = new HashSet(forest);
        for (Iterator i = clone.iterator(); i.hasNext(); ) {
            removeContinuation((WebContinuation) i.next());
        }
        */
        if (expireThread != null && expireThread.isAlive()) {
            expireThread.interrupt();
        }
    }

}
