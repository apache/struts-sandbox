package org.apache.struts2.uelplugin;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.tree.TreeBuilder;

import javax.el.ExpressionFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Properties;

import org.apache.struts2.uelplugin.ExpressionFactoryHolder;

/**
 * Responsible for creating the ExpressionFactory that will be used by the
 * UelValueStack
 */
public class UelServletContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent contextEvent) {
        Properties juelProperties = new Properties();
        juelProperties.setProperty("javax.el.methodInvocations", "true");

        //custom parser
        juelProperties.setProperty(TreeBuilder.class.getName(), JUELExtensionBuilder.class.getName());

        ExpressionFactory factory = new ExpressionFactoryImpl(juelProperties);

        ExpressionFactoryHolder.setExpressionFactory(factory);
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {
    }
}
