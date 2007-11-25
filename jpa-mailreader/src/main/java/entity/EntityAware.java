package entity;

import javax.persistence.EntityManager;

public interface EntityAware {
    void setManager(EntityManager value);
}
