package org.apache.struts2.uelplugin.reflection;

import java.lang.reflect.Member;
import java.util.Map;

/**
 * Taken from OGNL
 */
public interface MemberAccess {
    /**
     * Sets the member up for accessibility
     */
    public Object setup(Map context, Object target, Member member, String propertyName);

    /**
     * Restores the member from the previous setup call.
     */
    public void restore(Map context, Object target, Member member, String propertyName, Object state);

    /**
     * Returns true if the given member is accessible or can be made accessible
     * by this object.
     */
    public boolean isAccessible(Map context, Object target, Member member, String propertyName);
}
