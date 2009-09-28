package org.apache.struts2.showcase.crud.dao;

import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import org.apache.struts2.showcase.crud.entities.Persistent;

@Transactional
public class GenericDAOJPAImpl<T extends Persistent<K>, K> 
		extends JpaDaoSupport 
		implements GenericDAO<T, K> {

    public T create(T newInstance) {
    	return getJpaTemplate().merge(newInstance);
    }

    public void delete(T persistentObject) {
    	getJpaTemplate().remove(getJpaTemplate().merge(persistentObject));
    }

	public T read(T persistentObject) {
    	return getJpaTemplate().find(
    			(Class<T>)persistentObject.getClass(), 
    			persistentObject.getKey() );
    }

    public void update(T transientObject) {
    	getJpaTemplate().merge(transientObject);
    }

}
