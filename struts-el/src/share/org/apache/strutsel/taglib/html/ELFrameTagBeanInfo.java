/*
 * $Header: /home/cvs/jakarta-struts/contrib/struts-el/src/share/org/apache/strutsel/taglib/html/ELFrameTagBeanInfo.java,v 1.6 2004/02/14 19:43:14 dmkarr Exp $
 * $Revision: 1.6 $
 * $Date: 2004/02/14 19:43:14 $
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

import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.beans.SimpleBeanInfo;

/**
 * This is the <code>BeanInfo</code> descriptor for the
 * <code>org.apache.strutsel.taglib.html.ELFrameTag</code> class.  It is needed
 * to override the default mapping of custom tag attribute names to class
 * attribute names.
 *<p>
 * This is because the value of the unevaluated EL expression has to be kept
 * separately from the evaluated value, which is stored in the base class. This
 * is related to the fact that the JSP compiler can choose to reuse different
 * tag instances if they received the same original attribute values, and the
 * JSP compiler can choose to not re-call the setter methods, because it can
 * assume the same values are already set.
 */
public class ELFrameTagBeanInfo extends SimpleBeanInfo
{
    public  PropertyDescriptor[] getPropertyDescriptors()
    {
        ArrayList proplist = new ArrayList();

        try {
            proplist.add(new PropertyDescriptor("action", ELFrameTag.class,
                                                null, "setActionExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("module", ELFrameTag.class,
                                                null, "setModuleExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("anchor", ELFrameTag.class,
                                                null, "setAnchorExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("forward", ELFrameTag.class,
                                                null, "setForwardExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("frameborder", ELFrameTag.class,
                                                null, "setFrameborderExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("frameName", ELFrameTag.class,
                                                null, "setFrameNameExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("href", ELFrameTag.class,
                                                null, "setHrefExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("longdesc", ELFrameTag.class,
                                                null, "setLongdescExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("marginheight", ELFrameTag.class,
                                                null, "setMarginheightExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("marginwidth", ELFrameTag.class,
                                                null, "setMarginwidthExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("name", ELFrameTag.class,
                                                null, "setNameExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("noresize", ELFrameTag.class,
                                                null, "setNoresizeExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("page", ELFrameTag.class,
                                                null, "setPageExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("paramId", ELFrameTag.class,
                                                null, "setParamIdExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("paramName", ELFrameTag.class,
                                                null, "setParamNameExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("paramProperty", ELFrameTag.class,
                                                null, "setParamPropertyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("paramScope", ELFrameTag.class,
                                                null, "setParamScopeExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("property", ELFrameTag.class,
                                                null, "setPropertyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("scope", ELFrameTag.class,
                                                null, "setScopeExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("scrolling", ELFrameTag.class,
                                                null, "setScrollingExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("style", ELFrameTag.class,
                                                null, "setStyleExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("styleClass", ELFrameTag.class,
                                                null, "setStyleClassExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("styleId", ELFrameTag.class,
                                                null, "setStyleIdExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("title", ELFrameTag.class,
                                                null, "setTitleExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("titleKey", ELFrameTag.class,
                                                null, "setTitleKeyExpr"));
        } catch (IntrospectionException ex) {}
        try {
            proplist.add(new PropertyDescriptor("transaction", ELFrameTag.class,
                                                null, "setTransactionExpr"));
        } catch (IntrospectionException ex) {}
        
        PropertyDescriptor[] result =
            new PropertyDescriptor[proplist.size()];
        return ((PropertyDescriptor[]) proplist.toArray(result));
    }
}