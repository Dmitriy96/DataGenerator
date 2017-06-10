package by.bsuir.diplom.parser;

import by.bsuir.diplom.model.EntityDescriptor;
import by.bsuir.diplom.model.PropertyDescriptor;
import by.bsuir.diplom.model.VariableDescriptor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class CustomSAXParser extends DefaultHandler {

    List<VariableDescriptor> variableDescriptors = new ArrayList<VariableDescriptor>();
    List<EntityDescriptor> entityDescriptors = new ArrayList<EntityDescriptor>();
    EntityDescriptor currentEntityDescriptor;


    public List<VariableDescriptor> getVariableDescriptors() {
        return variableDescriptors;
    }

    public List<EntityDescriptor> getEntityDescriptors() {
        return entityDescriptors;
    }

    @Override
    public void startDocument() throws SAXException {
//        System.out.println("Start parse XML...");
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        System.out.println("startElement: " + namespaceURI + ",  " + localName + ",  " + qName + ",  " + atts);
        if (qName.equals("object")) {
            currentEntityDescriptor = new EntityDescriptor();
            currentEntityDescriptor.setId(Long.parseLong(atts.getValue("id")));
            String action = atts.getValue("action") != null ? atts.getValue("action") : "save";
            currentEntityDescriptor.setEntityAction(action);
            currentEntityDescriptor.setEntityClass(atts.getValue("class"));
        }
        if (qName.equals("property")) {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor();
            propertyDescriptor.setPropertyName(atts.getValue("name"));
            propertyDescriptor.setPropertyValue(atts.getValue("value"));
            currentEntityDescriptor.getPropertyDescriptors().add(propertyDescriptor);
        }
        if (qName.equals("variable")) {
            VariableDescriptor variableDescriptor = new VariableDescriptor();
            variableDescriptor.setName(atts.getValue("name"));
            variableDescriptor.setValue(atts.getValue("value"));
            variableDescriptors.add(variableDescriptor);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        System.out.println("endElement: " + namespaceURI + ",  " + localName + ",  " + qName);
        if (qName.equals("object")) {
            entityDescriptors.add(currentEntityDescriptor);
            currentEntityDescriptor = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        System.out.println("characters: " + ch + ",  " + start + ",  " + length);
    }

    @Override
    public void endDocument() {
        System.out.println("Stop parse XML...");
    }
}
