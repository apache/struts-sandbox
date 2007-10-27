package org.apache.struts2.rest.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.ValidationAwareSupport;

@Results({
    @Result(name="success", type=ServletActionRedirectResult.class, value="orders") 
})
public class OrdersController extends ValidationAwareSupport implements ModelDriven<Object>, ParameterAware, Validateable{
    
    private Order model = new Order();
    private static Map<String,Order> orders = new HashMap<String,Order>();
    
    static {
        orders.put("3", new Order("3", "Bob", 33));
        orders.put("4", new Order("4", "Sarah", 44));
        orders.put("5", new Order("5", "Jim", 66));
    }
    private Collection<Order> list;
    
    public void validate() {
        if (model.getId() == null || model.getId().length() ==0) {
            addFieldError("id", "ID is wrong");
        }
    }
    
    public String show() {
        return "show";
    }
    
    public String edit() {
        return "edit";
    }
    
    public String editNew() {
        return "editNew";
    }
    
    public String destroy() {
        orders.remove(model.getId());
        addActionMessage("Order removed successfully");
        return "success";
    }
    
    public HttpHeaders create() {
        orders.put(model.getId(), model);
        addActionMessage("New order created successfully");
        return new DefaultHttpHeaders()
            .setLocationId(model.getId())
            .renderResult("success");
    }
    
    public String update() {
        orders.put(model.getId(), model);
        addActionMessage("Order updated successfully");
        return "success";
    }
    
    public HttpHeaders index() {
        list = new ArrayList(orders.values());
        
        return new DefaultHttpHeaders()
            .renderResult("index")
            .disableCaching();
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
