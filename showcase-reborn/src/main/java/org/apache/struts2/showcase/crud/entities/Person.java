package org.apache.struts2.showcase.crud.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

@Entity
@NamedQueries({
  @NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")		
})
public class Person implements Persistent<Integer> {

	private Integer id;
	
	private String name;
	
	private Date birthDay;

	private Integer coolness;


    @Id
	@GeneratedValue	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Transient
	public Integer getKey() {
		return id;
	}

	@Column(length=32, nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(nullable=false)
	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}
	
	@Column(nullable=false)
	public Integer getCoolness() {
		return coolness;
	}

	public void setCoolness(Integer coolness) {
		this.coolness = coolness;
	}

}
