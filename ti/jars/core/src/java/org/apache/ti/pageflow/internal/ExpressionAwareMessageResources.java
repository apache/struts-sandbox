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



/**
 * Message resources extension that knows how to evaluate JSP 2.0-style expressions (set in the message keys) that are
 * prefixed with a special indicator string.
 */
public class ExpressionAwareMessageResources
        //extends MessageResources
{

    /* TODO: re-add expression-based message support
    private static final Logger _log = Logger.getInstance( ExpressionAwareMessageResources.class );
    
    private MessageResources _delegate;
    private Object _formBean;

    public ExpressionAwareMessageResources( Object formBean)
    {
        super( defaultFactory, null, true );
        _formBean = formBean;
    }
    
    public ExpressionAwareMessageResources( MessageResources delegate, Object formBean )
    {
        super( delegate.getFactory(), delegate.getConfig(), delegate.getReturnNull() );
        _delegate = delegate;
        _formBean = formBean;
    }

    public String getMessage( Locale locale, String key )
    {
        if ( key.startsWith( InternalConstants.MESSAGE_IS_EXPRESSION_PREFIX ) )
        {
            String messageExpr = key.substring( InternalConstants.MESSAGE_IS_EXPRESSION_PREFIX_LENGTH );
        
            try
            {
                return InternalExpressionUtils.evaluateMessage( messageExpr, _formBean);
            }
            catch ( Exception e )
            {
                _log.error( "Could not evaluate message expression " + messageExpr, e );
            }
                
            return null;
        }
        
        return _delegate != null ? _delegate.getMessage( locale, key ) : null;
    }
    
    public String getMessage( Locale locale, String key, Object args[] )
    {
        for ( int i = 0; i < args.length; i++ )
        {
            Object arg = args[i];
            
            if ( arg instanceof String )
            {
                String argStr = ( String ) arg;
                
                if ( argStr.startsWith( InternalConstants.MESSAGE_IS_EXPRESSION_PREFIX ) )
                {
                    String argExpr = argStr.substring( InternalConstants.MESSAGE_IS_EXPRESSION_PREFIX_LENGTH );
                
                    try
                    {
                        args[i] =
                            InternalExpressionUtils.evaluateMessage( argExpr, _formBean );
                    }
                    catch ( Exception e )
                    {
                        _log.error( "Could not evaluate message arg expression " + argExpr, e );
                    }
                }
            }
        }
        
        return super.getMessage( locale, key, args );
    }

    protected void setFormBean( Object formBean )
    {
        _formBean = formBean;
    }

    public static void update( MessageResources resources, Object formBean )
    {
        if ( resources instanceof ExpressionAwareMessageResources )
        {
            ( ( ExpressionAwareMessageResources ) resources ).setFormBean( formBean );
        }
    }
    */
}


