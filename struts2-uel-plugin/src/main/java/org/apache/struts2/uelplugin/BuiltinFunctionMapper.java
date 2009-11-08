package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang.xwork.StringUtils;

import javax.el.FunctionMapper;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Builtin function availabe from UEL. Available functions are</p>
 * <ul>
 * <li>getText(String):Looks up a text from resource bundles using the parameter as a key</li>
 * </ul>
 */
public class BuiltinFunctionMapper extends FunctionMapper {
    private static final Map<String, Method> BUILTIN_FUNCTIONS = new HashMap<String, Method>() {
        {
            try {
                Method getText = BuiltinFunctionMapper.class.getMethod("getText", new Class[]{String.class});
                put("getText", getText);
            } catch (NoSuchMethodException e) {
                //this should never happen
                throw new RuntimeException(e);
            }
        }
    };


    public Method resolveFunction(String prefix, String localName) {
        return StringUtils.isBlank(prefix) ? BUILTIN_FUNCTIONS.get(localName) : null;
    }

    public static String getText(String key) {
        ValueStack stack = ActionContext.getContext().getValueStack();
        Object action = stack.findValue("#action");

        if (action != null && action instanceof LocaleProvider) {
            TextProvider textProvider = new TextProviderFactory().createInstance(action.getClass(), (LocaleProvider) action);
            return textProvider.getText(key);
        }

        return null;
    }
}
