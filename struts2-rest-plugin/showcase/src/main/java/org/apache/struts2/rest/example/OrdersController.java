package org.apache.struts2.rest.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.ValidationAwareSupport;

@Results({
    @Result(name="success", type=ServletActionRedirectResult.class, value="orders") 
})
public class OrdersController extends ValidationAwareSupport implements ModelDriven<Object>, Validateable{
    
    private Order model = new Order();
    private static Map<String,Order> orders = new HashMap<String,Order>();
    private static int nextId = 6;
    private String id;
    
    static {
        orders.put("3", new Order("3", "Bob", 33));
        orders.put("4", new Order("4", "Sarah", 44));
        orders.put("5", new Order("5", "Jim", 66));
    }
    private Collection<Order> list;

    public void setId(String id) {
        if (id != null && orders.containsKey(id)) {
            this.model = orders.get(id);
        }
        this.id = id;
    }
    
    public void validate() {
        if (model.getClientName() == null || model.getClientName().length() ==0) {
            addFieldError("clientName", "The client name is empty");
        }
    }
    
    public HttpHeaders show() {
        return new DefaultHttpHeaders("show");
    }
    
    public String edit() {
        return "edit";
    }
    
    public String editNew() {
        model = new Order();
        return "editNew";
    }
    
    public String destroy() {
        orders.remove(id);
        addActionMessage("Order removed successfully");
        return "success";
    }
    
    public HttpHeaders create() {
        model.setId(String.valueOf(nextId++));
        orders.put(model.getId(), model);
        addActionMessage("New order created successfully");
        return new DefaultHttpHeaders("success")
            .setLocationId(model.getId());
    }
    
    public String update() {
        orders.put(id, model);
        addActionMessage("Order updated successfully");
        return "success";
    }
    
    public HttpHeaders index() {
        list = new ArrayList(orders.values());
        
        return new DefaultHttpHeaders("index")
            .disableCaching();
    }
    
    public Object getModel() {
        return (list != null ? list : model);
    }

}
