package by.bsuir.diplom;

import by.bsuir.diplom.helpers.PostParsingProcessing;
import by.bsuir.diplom.parser.DatabaseDescriptionFileParser;

public class FillDatabase {
    public static void main(String[] args) throws Exception {
        DatabaseDescriptionFileParser.parse();
        Object[] entities = PostParsingProcessing.createEntities();
        for (Object entity : entities) {
            System.out.println("entity: " + entity);
        }
    }
}
