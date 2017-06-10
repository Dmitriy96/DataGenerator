package by.bsuir.diplom.model.parsed;

import by.bsuir.diplom.model.EntityDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ParsedEntities {
    private static List<EntityDescriptor> entityDescriptors = new ArrayList<EntityDescriptor>();

    public static void addEntityDescriptor(EntityDescriptor entityDescriptor) {
        entityDescriptors.add(entityDescriptor);
    }

    public static void addEntityDescriptors(List<EntityDescriptor> newEntityDescriptors) {
        entityDescriptors.addAll(newEntityDescriptors);
    }

    public static List<EntityDescriptor> getEntityDescriptors() {
        return entityDescriptors;
    }
}
