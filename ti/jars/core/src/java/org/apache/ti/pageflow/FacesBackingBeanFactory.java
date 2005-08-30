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

import org.apache.ti.core.factory.Factory;
import org.apache.ti.core.factory.FactoryUtils;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.internal.AnnotationReader;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.schema.config.PageflowFactories;
import org.apache.ti.schema.config.PageflowFactory;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.internal.FileUtils;
import org.apache.ti.util.logging.Logger;

import java.util.Map;


/**
 * Factory for creating "backing beans" for JavaServer Faces pages.
 */
public class FacesBackingBeanFactory
        extends Factory
        implements InternalConstants {

    private static final Logger _log = Logger.getInstance(FacesBackingBeanFactory.class);

    private static final String CONTEXT_ATTR = InternalConstants.ATTR_PREFIX + "jsfBackingFactory";

    protected void onCreate() {
    }

    protected FacesBackingBeanFactory() {
    }

    public static void init(Map appScope) {
        PageflowFactories factoriesBean = ConfigUtil.getConfig().getPageflowFactories();
        FacesBackingBeanFactory factory = null;

        if (factoriesBean != null) {
            PageflowFactory fcFactoryBean = factoriesBean.getFacesBackingBeanFactory();
            factory = (FacesBackingBeanFactory) FactoryUtils.getFactory(fcFactoryBean, FacesBackingBeanFactory.class);
        }

        if (factory == null) factory = new FacesBackingBeanFactory();
        factory.reinit();

        appScope.put(CONTEXT_ATTR, factory);
    }

    /**
     * Called to reinitialize this instance, most importantly after it has been serialized/deserialized.
     */
    protected void reinit() {
        super.reinit();
    }

    /**
     * Get a FacesBackingBeanFactory.
     *
     * @return a FacesBackingBeanFactory for the given application.  It may or may not be a cached instance.
     */
    public static FacesBackingBeanFactory get() {
        Map appScope = PageFlowActionContext.get().getApplication();
        FacesBackingBeanFactory factory = (FacesBackingBeanFactory) appScope.get(CONTEXT_ATTR);
        assert factory != null
                : FacesBackingBeanFactory.class.getName() + " was not found in application attribute " + CONTEXT_ATTR;
        factory.reinit();
        return factory;
    }

    /**
     * Get the "backing bean" associated with the JavaServer Faces page for a request.
     */
    public FacesBackingBean getFacesBackingBeanForRequest() {
        String uri = PageFlowActionContext.get().getRequestPath();
        assert uri.charAt(0) == '/' : uri;
        String backingClassName = FileUtils.stripFileExtension(uri.substring(1).replace('/', '.'));
        FacesBackingBean currentBean = InternalUtils.getFacesBackingBean();
        
        //
        // If there is no current backing bean, or if the current one doesn't match the desired classname, create one.
        //
        if (currentBean == null || !currentBean.getClass().getName().equals(backingClassName)) {
            FacesBackingBean bean = null;

            if (FileUtils.uriEndsWith(uri, FACES_EXTENSION) || FileUtils.uriEndsWith(uri, JSF_EXTENSION)) {
                bean = loadFacesBackingBean(backingClassName);
                
                //
                // If we didn't create (or failed to create) a backing bean, and if this is a JSF request, then create
                // a default one.  This ensures that there will be a place for things like page inputs, that get stored
                // in the backing bean across postbacks to the same JSF.
                //
                if (bean == null) bean = new DefaultFacesBackingBean();
                
                //
                // If we created a backing bean, invoke its create callback, and tell it to store itself in the session.
                //
                if (bean != null) {
                    try {
                        bean.create();
                    } catch (Exception e) {
                        _log.error("Error while creating backing bean instance of " + backingClassName, e);
                    }

                    bean.persistInSession();
                    return bean;
                }
            }
            
            //
            // We didn't create a backing bean.  If there's one in the session (an inappropriate one), remove it.
            //
            InternalUtils.removeCurrentFacesBackingBean();
        } else if (currentBean != null) {
            if (_log.isDebugEnabled()) {
                _log.debug("Using existing backing bean instance " + currentBean + " for request " +
                        PageFlowActionContext.get().getRequestPath());
            }

            currentBean.reinitialize();
        }

        return currentBean;
    }

    /**
     * Load a "backing bean" associated with the JavaServer Faces page for a request.
     *
     * @param backingClassName the name of the backing bean class.
     * @return an initialized FacesBackingBean, or <code>null</code> if an error occurred.
     */
    protected FacesBackingBean loadFacesBackingBean(String backingClassName) {
        try {
            Class backingClass = null;

            try {
                backingClass = getFacesBackingBeanClass(backingClassName);
            } catch (ClassNotFoundException e) {
                // ignore -- we deal with this and log this immediately below.  getFacesBackingBeanClass() by default
                // does not throw this exception, but a derived version might.
            }

            if (backingClass == null) {
                if (_log.isTraceEnabled()) {
                    _log.trace("No backing bean class " + backingClassName + " found for request "
                            + PageFlowActionContext.get().getRequestPath());
                }
            } else {
                AnnotationReader annReader = Handlers.get().getAnnotationHandler().getAnnotationReader(backingClass);

                if (annReader.getJpfAnnotation(backingClass, "facesBacking") != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("Found backing class " + backingClassName + " for request "
                                + PageFlowActionContext.get().getRequestPath()
                                + "; creating a new instance.");
                    }

                    return getFacesBackingBeanInstance(backingClass);
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug("Found matching backing class " + backingClassName + " for request "
                                + PageFlowActionContext.get().getRequestPath()
                                + ", but it does not have the " + ANNOTATION_QUALIFIER
                                + "facesBacking annotation.");
                    }
                }
            }
        } catch (InstantiationException e) {
            _log.error("Could not create backing bean instance of " + backingClassName, e);
        } catch (IllegalAccessException e) {
            _log.error("Could not create backing bean instance of " + backingClassName, e);
        }

        return null;
    }

    private static class DefaultFacesBackingBean
            extends FacesBackingBean {

    }

    /**
     * Get a FacesBackingBean class.  By default, this loads the class using the thread context class loader.
     *
     * @param className the name of the {@link FacesBackingBean} class to load.
     * @return the loaded {@link FacesBackingBean} class.
     * @throws ClassNotFoundException if the requested class could not be found.
     */
    public Class getFacesBackingBeanClass(String className)
            throws ClassNotFoundException {
        return Handlers.get().getReloadableClassHandler().loadCachedClass(className);
    }

    /**
     * Get a FacesBackingBean instance, given a FacesBackingBean class.
     *
     * @param beanClass the Class, which must be assignable to {@link FacesBackingBean}.
     * @return a new FacesBackingBean instance.
     */
    public FacesBackingBean getFacesBackingBeanInstance(Class beanClass)
            throws InstantiationException, IllegalAccessException {
        assert FacesBackingBean.class.isAssignableFrom(beanClass)
                : "Class " + beanClass.getName() + " does not extend " + FacesBackingBean.class.getName();
        return (FacesBackingBean) beanClass.newInstance();
    }
}
