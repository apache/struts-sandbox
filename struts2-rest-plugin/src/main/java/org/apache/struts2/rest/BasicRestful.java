package org.apache.struts2.rest;

import java.util.List;

public interface BasicRestful<E, ID> {

    public ID create(E object);
    
    public void update(ID id, E object);
    
    public void remove(ID id);
    
    public E view(ID id);
    
    public List<E> index();
    
}
