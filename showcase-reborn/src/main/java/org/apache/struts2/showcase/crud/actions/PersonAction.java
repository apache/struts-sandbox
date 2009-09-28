package org.apache.struts2.showcase.crud.actions;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.interceptor.validation.SkipValidation;

import org.apache.struts2.showcase.crud.dao.PersonDao;
import org.apache.struts2.showcase.crud.entities.Person;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

@Namespace("/crud")
@Results({
        @Result(name = com.opensymphony.xwork2.Action.INPUT, location = "person-list.jsp"),
        @Result(name = "list", location = "person-list.jsp")
})
public class PersonAction extends ActionSupport implements Preparable {

    private Person person;
    private List<Person> personList;
    private PersonDao personDao;

    public void prepare() {
        person = new Person();
    }

    @SkipValidation
    public String execute() {
        return SUCCESS;
    }

    @Action(value = "person-submit",
            results = @Result(name = com.opensymphony.xwork2.Action.SUCCESS, location = "person-list", type = "redirect")
    )
    public String update() {
        if (person.getKey() != null) {
            personDao.update(person);
        } else {
            personDao.create(person);
        }
        personList = personDao.findAll();
        return SUCCESS;
    }

    @SkipValidation
    @Action(value = "person-delete",
            results = @Result(name = com.opensymphony.xwork2.Action.SUCCESS, location = "person-list", type = "redirect")
    )
    public String delete() {
        person = personDao.read(person);
        personDao.delete(person);
        personList = personDao.findAll();
        return "list";
    }

    @SkipValidation
    @Action("person-list")
    public String list() {
        personList = personDao.findAll();
        return "list";
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    public List<Person> getPersonList() {
        return personList;
    }

}
