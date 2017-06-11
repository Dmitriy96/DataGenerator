package by.bsuir.diplom.model;

import java.util.ArrayList;
import java.util.List;

public class SeriesDescriptor {
    Integer count;
    Integer from;
    String variable;
    List<EntityDescriptor> entityDescriptors = new ArrayList<EntityDescriptor>();

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public List<EntityDescriptor> getEntityDescriptors() {
        return entityDescriptors;
    }

    public void setEntityDescriptors(List<EntityDescriptor> entityDescriptors) {
        this.entityDescriptors = entityDescriptors;
    }
}
