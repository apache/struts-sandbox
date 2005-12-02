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
package org.apache.ti.pageflow.validation;



public class ValidatorRules {

}

/* TODO: re-add Validator support
        extends FieldChecks
{
    private static final Logger _log = Logger.getInstance( ValidatorRules.class );
    
    /**
     * Check if a given expression evaluates to <code>true</code>.
     * 
     * @param bean the bean that validation is being performed on.
     * @param va the <code>ValidatorAction</code> that is currently being performed.
     * @param field the <code>Field</code> object associated with the current field being validated.
     * @param errors the <code>ActionMessages</code> object to add errors to if any validation errors occur.
     * @param request the current request object.
     * @return <code>true</code> if the given expression evaluates to <code>true</code>
     * 
    public static boolean validateValidWhen( Object bean, ValidatorAction va, Field field, ActionMessages errors,
                                             HttpServletRequest request, ServletContext servletContext )
    {

        String value;
        
        if ( isString( bean ) )
        {
            value = ( String ) bean;
        }
        else
        {
            value = ValidatorUtil.getValueAsString( bean, field.getProperty() );
        }

        if ( ! GenericValidator.isBlankOrNull( value ) )
        {
            String condition = field.getVarValue( "netui_validwhen" );
            
            try
            {
                if ( ! InternalExpressionUtils.evaluateCondition( condition, bean, request, servletContext ) )
                {
                    errors.add( field.getKey(), Resources.getActionError( request, va, field ) );
                    return false;
                }
            }
            catch ( Exception e )
            {
                _log.error( "Error evaluating expression " + condition + " for ValidWhen rule on field "
                            + field.getProperty() + " on bean of type " +
                            ( bean != null ? bean.getClass().getName() : null ) );
                
                errors.add( field.getKey(), Resources.getActionError( request, va, field ) );
                return false;
            }
        }

        return true;
    } 
    
    /**
     * Check if a field's value is within a range ("min" and "max" Long variables on the passed-in Field).
     *
     * @param bean the bean that validation is being performed on.
     * @param va the <code>ValidatorAction</code> that is currently being performed.
     * @param field the <code>Field</code> object associated with the current field being validated.
     * @param errors the <code>ActionMessages</code> object to add errors to if any validation errors occur.
     * @param request the current request object.
     * @return <code>true</code> if in range, false otherwise.
     *
    public static boolean validateLongRange( Object bean, ValidatorAction va, Field field, ActionMessages errors,
                                             HttpServletRequest request )
    {

        String value;
        
        if ( isString( bean ) )
        {
            value = ( String ) bean;
        }
        else
        {
            value = ValidatorUtil.getValueAsString( bean, field.getProperty() );
        }

        if ( ! GenericValidator.isBlankOrNull( value ) )
        {
            try
            {
                long longValue = Long.parseLong( value );
                long min = Long.parseLong( field.getVarValue( "min" ) );
                long max = Long.parseLong( field.getVarValue( "max" ) );

                if ( longValue < min || longValue > max )
                {
                    errors.add( field.getKey(), Resources.getActionError( request, va, field ) );
                    return false;
                }
            }
            catch ( Exception e )
            {
                errors.add( field.getKey(), Resources.getActionError( request, va, field ) );
                return false;
            }
        }

        return true;
    }
}
*/
