package by.bsuir.diplom.helpers;

import by.bsuir.diplom.model.parsed.ParsedEntities;

public class PostParsingProcessing {

    public static Object[] createEntities() throws ClassNotFoundException {
        Class<?> clazz = Class.forName(ParsedEntities.getEntityDescriptors().get(0).getEntityClass(), false, null);
        System.out.println("createEntities: " + clazz.getSimpleName());
        return null;
    }
}
