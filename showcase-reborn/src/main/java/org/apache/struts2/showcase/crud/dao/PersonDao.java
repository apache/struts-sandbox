package org.apache.struts2.showcase.crud.dao;

import java.util.List;

import org.apache.struts2.showcase.crud.entities.Person;

public interface PersonDao extends GenericDAO<Person,Integer> {

	/** Retrieve all people */
	public List<Person> findAll() ;
	
}
