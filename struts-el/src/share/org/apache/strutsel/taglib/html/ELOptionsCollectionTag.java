/*
 * $Header: /home/cvs/jakarta-struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/html/ELOptionsCollectionTag.java,v 1.6 2003/02/19 03:53:49 dmkarr Exp $
 * $Revision: 1.6 $
 * $Date: 2003/02/19 03:53:49 $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.strutsel.taglib.html;

import org.apache.struts.taglib.html.OptionsCollectionTag;
import javax.servlet.jsp.JspException;
import org.apache.strutsel.taglib.utils.EvalHelper;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;

/**
 * Tag for creating multiple &lt;select&gt; options from a collection. The
 * collection may be part of the enclosing form, or may be independent of
 * the form. Each element of the collection must expose a 'label' and a
 * 'value', the property names of which are configurable by attributes of
 * this tag.
 * <p>
 * The collection may be an array of objects, a Collection, an Enumeration,
 * an Iterator, or a Map.
 * <p>
 * <b>NOTE</b> - This tag requires a Java2 (JDK 1.2 or later) platform.
 *<p>
 * This class is a subclass of the class
 * <code>org.apache.struts.taglib.html.OptionsCollectionTag</code> which
 * provides most of the described functionality.  This subclass allows all
 * attribute values to be specified as expressions utilizing the JavaServer
 * Pages Standard Library expression language.
 *
 * @author David M. Karr
 * @version $Revision: 1.6 $
 */
public class ELOptionsCollectionTag extends OptionsCollectionTag {

    /**
     * Instance variable mapped to "filter" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String filterExpr;
    /**
     * Instance variable mapped to "label" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String labelExpr;
    /**
     * Instance variable mapped to "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String nameExpr;
    /**
     * Instance variable mapped to "property" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String propertyExpr;
    /**
     * Instance variable mapped to "style" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String styleExpr;
    /**
     * Instance variable mapped to "styleClass" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String styleClassExpr;
    /**
     * Instance variable mapped to "value" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    private String valueExpr;

    /**
     * Getter method for "filter" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getFilterExpr() { return (filterExpr); }
    /**
     * Getter method for "label" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getLabelExpr() { return (labelExpr); }
    /**
     * Getter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getNameExpr() { return (nameExpr); }
    /**
     * Getter method for "property" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getPropertyExpr() { return (propertyExpr); }
    /**
     * Getter method for "style" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getStyleExpr() { return (styleExpr); }
    /**
     * Getter method for "styleClass" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getStyleClassExpr() { return (styleClassExpr); }
    /**
     * Getter method for "value" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public String getValueExpr() { return (valueExpr); }

    /**
     * Setter method for "filter" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setFilterExpr(String filterExpr) { this.filterExpr = filterExpr; }
    /**
     * Setter method for "label" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setLabelExpr(String labelExpr) { this.labelExpr = labelExpr; }
    /**
     * Setter method for "name" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setNameExpr(String nameExpr) { this.nameExpr = nameExpr; }
    /**
     * Setter method for "property" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setPropertyExpr(String propertyExpr) { this.propertyExpr = propertyExpr; }
    /**
     * Setter method for "style" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setStyleExpr(String styleExpr) { this.styleExpr = styleExpr; }
    /**
     * Setter method for "styleClass" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setStyleClassExpr(String styleClassExpr) { this.styleClassExpr = styleClassExpr; }
    /**
     * Setter method for "value" tag attribute.
     * (Mapping set in associated BeanInfo class.)
     */
    public void setValueExpr(String valueExpr) { this.valueExpr = valueExpr; }

    /**
     * Resets attribute values for tag reuse.
     */
    public void release()
    {
        super.release();
        setFilterExpr(null);
        setLabelExpr(null);
        setNameExpr(null);
        setPropertyExpr(null);
        setStyleExpr(null);
        setStyleClassExpr(null);
        setValueExpr(null);
    }

    /**
     * Process the start tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {
        evaluateExpressions();
        return (super.doStartTag());
    }
    
    /**
     * Evaluates and returns a single attribute value, given the attribute
     * name, attribute value, and attribute type.  It uses the
     * <code>EvalHelper</code> class to interface to
     * <code>ExpressionUtil.evalNotNull</code> to do the actual evaluation, and
     * it passes to this the name of the current tag, the <code>this</code>
     * pointer, and the current pageContext.
     *
     * @param attrName attribute name being evaluated
     * @param attrValue String value of attribute to be evaluated using EL
     * @param attrType Required resulting type of attribute value
     * @exception NullAttributeException if either the <code>attrValue</code>
     * was null, or the resulting evaluated value was null.
     * @return Resulting attribute value
     */
    private Object   evalAttr(String   attrName,
                              String   attrValue,
                              Class    attrType)
        throws JspException, NullAttributeException
    {
        return (EvalHelper.eval("optionsCollection", attrName, attrValue,
                                attrType, this, pageContext));
    }
    
    /**
     * Processes all attribute values which use the JSTL expression evaluation
     * engine to determine their values.  If any evaluation fails with a
     * <code>NullAttributeException</code> it will just use <code>null</code>
     * as the value.
     *
     * @exception JspException if a JSP exception has occurred
     */
    private void evaluateExpressions() throws JspException {
        try {
            setFilter(((Boolean) evalAttr("filter", getFilterExpr(), 
                                          Boolean.class)).
                      booleanValue());
        } catch (NullAttributeException ex) {
        }

        try {
            setLabel((String) evalAttr("label", getLabelExpr(), String.class));
        } catch (NullAttributeException ex) {
        }

        try {
            setName((String) evalAttr("name", getNameExpr(), String.class));
        } catch (NullAttributeException ex) {
        }

        try {
            setProperty((String) evalAttr("property", getPropertyExpr(), 
                                          String.class));
        } catch (NullAttributeException ex) {
        }

        try {
            setStyle((String) evalAttr("style", getStyleExpr(), String.class));
        } catch (NullAttributeException ex) {
        }

        try {
            setStyleClass((String) evalAttr("styleClass", getStyleClassExpr(), 
                                            String.class));
        } catch (NullAttributeException ex) {
        }

        // Note that in contrast to other elements which have "style" and
        // "styleClass" attributes, this tag does not have a "styleId"
        // attribute.  This is because this produces the "id" attribute, which
        // has to be unique document-wide, but this tag can generate more than
        // one "option" element.  Thus, the base tag, "OptionsCollectionTag"
        // does not support this attribute.

        try {
            setValue((String) evalAttr("value", getValueExpr(), String.class));
        } catch (NullAttributeException ex) {
        }
    }
}
