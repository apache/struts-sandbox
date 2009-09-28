package org.apache.struts2.showcase.crud.dao;

import org.apache.struts2.showcase.crud.entities.Persistent;


public interface GenericDAO<T extends Persistent<K>, K> {
    
	T create(T newInstance);

	T read(T object);

	void update(T transientObject);

	void delete(T persistentObject);

}