package by.bsuir.diplom.model;

import java.util.ArrayList;
import java.util.List;

public class EntityDescriptor {
    Long id;
    String entityAction;    // create, load, delete, update
    String entityClass;
    List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(String entityAction) {
        this.entityAction = entityAction;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public List<PropertyDescriptor> getPropertyDescriptors() {
        return propertyDescriptors;
    }

    public void setPropertyDescriptors(List<PropertyDescriptor> propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors;
    }

    @Override
    public String toString() {
        return "EntityDescriptor: " + id + ", " + entityAction + ", " + entityClass + ", " + propertyDescriptors;
    }
}
