package org.apache.struts2.showcase.crud.dao;

import java.util.List;

import org.apache.struts2.showcase.crud.entities.Person;

public class PersonDaoImpl extends GenericDAOJPAImpl<Person,Integer> 
  implements PersonDao {
	
	public List<Person> findAll() {
		return getJpaTemplate().findByNamedQuery("Person.findAll");
	}
}
