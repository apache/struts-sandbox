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
package org.apache.ti.pageflow.internal;

import org.apache.ti.pageflow.ControlFieldInitializationException;
import org.apache.ti.pageflow.PageFlowManagedObject;

import java.util.Map;


public class JavaControlUtils {

    /* TODO: re-add Controls support (as a Handler)
    
    private static final Logger _log = Logger.getInstance( JavaControlUtils.class );
    private static final String CONTROL_CONTEXT_CLASSNAME = ServletBeanContext.class.getName();
    private static final String CONTROL_ANNOTATION_CLASSNAME = Control.class.getName();
    
    /** Map of control-container-class (e.g., PageFlowController) to Map of fields/control-properties. *
    private static InternalConcurrentHashMap/*< String, Map< Field, PropertyMap > >* _controlFieldCache =
            new InternalConcurrentHashMap/*< String, Map< Field, PropertyMap > >*();
    
            */
    public static void initializeControlContext() {
        /*
        ControlBeanContext beanContext = getControlBeanContext( request, response, servletContext, true );

        //
        // Start a new execution context
        //
        if ( beanContext instanceof ServletBeanContext )
        {
            ( ( ServletBeanContext ) beanContext ).beginContext( servletContext, request, response ); 
        }
        */
    }

    public static void uninitializeControlContext() {
        /*
        ControlBeanContext beanContext = getControlBeanContext( request, response, servletContext, false );
        
        if ( beanContext instanceof ServletBeanContext )
        {
            ( ( ServletBeanContext ) beanContext ).endContext();
        }
        */
    }

    /*
    public static class ControlInstantiationException
        extends Exception
    {
        private String _controlBeanClassName;
        
        public ControlInstantiationException( String controlBeanClassName, Throwable cause_ )
        {
            super( cause_ );
            _controlBeanClassName = controlBeanClassName;
        }

        public String getControlBeanClassName()
        {
            return _controlBeanClassName;
        }
    }
    
    private static ControlBeanContext getControlBeanContext( HttpServletRequest request, HttpServletResponse response,
                                                             ServletContext servletContext, boolean createIfMissing )
    {
        //
        // Retrieve the control bean context from the request, and if it's not there, from the session.
        // Using the request first ensures that we don't get confused by session invalidation.
        //
        ControlBeanContext beanContext = ( ControlBeanContext ) request.getAttribute( CONTROL_CONTEXT_CLASSNAME );
        if ( beanContext != null ) return beanContext;
        
        HttpSession session = request.getSession( false );
        if ( session != null )
        {
            beanContext = ( ControlBeanContext ) session.getAttribute( CONTROL_CONTEXT_CLASSNAME );
            
            if ( beanContext != null )
            {
                request.setAttribute( CONTROL_CONTEXT_CLASSNAME, beanContext );
                return beanContext;
            }
        }
        
        //
        // If no context exists, then create a new one and store it in the session.
        //
        if ( createIfMissing )
        {
            beanContext = ( ControlBeanContext )
                AdapterManager.getContainerAdapter( servletContext ).createControlBeanContext( request, response );
            request.getSession().setAttribute( CONTROL_CONTEXT_CLASSNAME, beanContext );
            request.setAttribute( CONTROL_CONTEXT_CLASSNAME, beanContext );
        }
       
        return beanContext;
    }
    
    */
    public static void destroyControl(Object controlInstance) {
        /*
        assert controlInstance instanceof ControlBean : controlInstance.getClass().getName();
        BeanContext beanContext = ( ( ControlBean ) controlInstance ).getBeanContext();
        if ( beanContext != null ) beanContext.remove( controlInstance ); 
        */
    }


    /**
     * @return a map of Field (accessible) -> AnnotationMap
     */
    private static Map getAccessibleControlFieldAnnotations(Class controlContainerClass) {
        /*
        String className = controlContainerClass.getName();
        
        //
        // Reading the annotations is expensive.  See if there's a cached copy of the map.
        //
        Map/*< Field, PropertyMap >* cached = ( Map ) _controlFieldCache.get( className );
        
        if ( cached != null )
        {
            return cached;
        }

        
        HashMap/*< Field, PropertyMap >* ret = new HashMap/*< Field, PropertyMap >*();
        boolean accessPrivateFields = true;
        
        // Note that the annnotation reader doesn't change per-class.  Inherited annotated elements are all read.
        AnnotationReader annReader = AnnotationReader.getAnnotationReader( controlContainerClass, servletContext );
        
        //
        // Go through this class and all superclasses, looking for control fields.  Make sure that a superclass control
        // field never replaces a subclass control field (this is what the 'fieldNames' HashSet is for).
        //
        
        HashSet fieldNames = new HashSet();
        
        do
        {
            Field[] fields = controlContainerClass.getDeclaredFields();
            
            for ( int i = 0; i < fields.length; i++ )
            {
                Field field = fields[i];
                String fieldName = field.getName();
                int modifiers = field.getModifiers();
                
                if ( ! fieldNames.contains( fieldName ) && ! Modifier.isStatic( modifiers )
                     && annReader.getAnnotation( field.getName(), CONTROL_ANNOTATION_CLASSNAME ) != null )
                {
                    if ( accessPrivateFields || ! Modifier.isPrivate( modifiers ) )
                    {
                        if ( ! Modifier.isPublic( field.getModifiers() ) ) field.setAccessible( true );
                        ret.put( field, new AnnotatedElementMap( field ) );
                        fieldNames.add( fieldName );
                    }
                }
            }
    
            accessPrivateFields = false;
            controlContainerClass = controlContainerClass.getSuperclass();
        } while ( controlContainerClass != null );
        
        _controlFieldCache.put( className, ret );
        return ret;
        */
        
        return null;
    }

    /**
     * Initialize all null member variables that are Java Controls.
     */
    public static void initJavaControls(PageFlowManagedObject controlClient)
            throws ControlFieldInitializationException {
        /*
        Class controlClientClass = controlClient.getClass();
        
        //
        // First, just return if there are no annotated Control fields.  This saves us from having to catch a
        // (wrapped) ClassNotFoundException for the control client initializer if we were to simply call
        // Controls.initializeClient().
        //
        Map controlFields = getAccessibleControlFieldAnnotations( controlClientClass, servletContext );
        if ( controlFields.isEmpty() ) return;

        request = PageFlowUtils.unwrapMultipart( request );
        ControlBeanContext beanContext = getControlBeanContext( request, response, servletContext, false );
        assert beanContext != null : "ControlBeanContext was not initialized by PageFlowRequestProcessor";
        try
        {
            Controls.initializeClient(null, controlClient, beanContext);
        }
        catch ( Exception e )
        {
            _log.error( "Exception occurred while initializing controls", e);
            throw new ControlFieldInitializationException( controlClientClass.getName(), controlClient, e );
        }
        */
    }

    /**
     * Clean up all member variables that are Java Controls.
     */
    public static void uninitJavaControls(PageFlowManagedObject controlClient) {
        /*
        Map controlFields = getAccessibleControlFieldAnnotations( controlClient.getClass(), servletContext );
        
        for ( Iterator i = controlFields.keySet().iterator(); i.hasNext(); )
        {
            Field controlField = ( Field ) i.next();

            try
            {
                Object fieldValue = controlField.get( controlClient );
                
                if ( fieldValue != null )
                {
                    controlField.set( controlClient, null );
                    destroyControl( fieldValue );
                }
            }
            catch ( IllegalAccessException e )
            {
                _log.error( "Exception while uninitializing Java Control " + controlField.getName(), e );
            }            
        }
        */
    }
}
