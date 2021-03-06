package by.bsuir.diplom.model;

public class PropertyDescriptor {
    String propertyName;
    String propertyValue;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public String toString() {
        return "{ " + propertyName + ", " + propertyValue + " }";
    }
}
