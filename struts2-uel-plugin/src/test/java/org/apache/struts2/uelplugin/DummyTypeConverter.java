package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.conversion.TypeConverter;

import java.util.Map;
import java.lang.reflect.Member;

import org.apache.struts2.util.StrutsTypeConverter;

public class DummyTypeConverter extends StrutsTypeConverter {
    public Object convertFromString(Map context, String[] values, Class toClass) {
        return "converted";
    }

    public String convertToString(Map context, Object o) {
        return "converted-tostring";
    }
}
