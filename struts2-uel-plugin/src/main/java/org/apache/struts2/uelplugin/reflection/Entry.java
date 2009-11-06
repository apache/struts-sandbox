package org.apache.struts2.uelplugin.reflection;

/**
 * Taken from OGNL
 */
class Entry {

    Entry next;
    Class key;
    Object value;

    public Entry(Class key, Object value)
    {
        this.key = key;
        this.value = value;
    }

    public String toString()
    {
        return "Entry[" +
               "next=" + next +
               '\n' +
               ", key=" + key +
               '\n' +
               ", value=" + value +
               '\n' +
               ']';
    }
}