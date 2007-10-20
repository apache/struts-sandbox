package org.apache.struts2.rest.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.rest.DefaultRestInfo;
import org.apache.struts2.rest.RestInfo;

import com.opensymphony.xwork2.ModelDriven;

public class OrderResource implements ModelDriven<Object>, ParameterAware{
    
    private Order model = new Order();
    private static Map<String,Order> orders = new HashMap<String,Order>() {{
        put("3", new Order("3", "Bob", 33));
        put("4", new Order("4", "Sarah", 44));
        put("5", new Order("5", "Jim", 66));
    }};
    private Collection<Order> list;
    
    public String show() {
        return "show";
    }
    
    public String input() {
        if (model.getId() != null) {
            return "input";
        } else {
            return "new";
        }
        
    }
    
    public String destroy() {
        orders.remove(model.getId());
        return "success";
    }
    
    public RestInfo create() {
        orders.put(model.getId(), model);
        return new DefaultRestInfo()
            .setLocationId(model.getId())
            .renderResult("success");
    }
    
    public String update() {
        orders.put(model.getId(), model);
        return "success";
    }
    
    public RestInfo index() {
        list = orders.values();
        
        return new DefaultRestInfo()
            .renderResult("index")
            .withETag("2323");
    }
    
    public Object getModel() {
        return (list != null ? list : model);
    }

    // Silly workaround since modeldriven doesn't work right in xwork 2.1.0
    public void setParameters(Map<String,String[]> parameters) {
        if (parameters.get("id") != null && orders.get(parameters.get("id")[0]) != null) {
            orders.get(parameters.get("id")[0]).copyTo(model);
        }
    }
    
    
}
