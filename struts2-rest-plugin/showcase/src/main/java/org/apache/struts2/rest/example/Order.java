package org.apache.struts2.rest.example;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Order {
    String id;
    String clientName;
    int amount;
    
    public Order() {}
    
    public Order(String id, String clientName, int amount) {
        super();
        this.id = id;
        this.clientName = clientName;
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public void copyTo(Order order) {
        order.setId(getId());
        order.setAmount(getAmount());
        order.setClientName(getClientName());
    }
    
    
}
