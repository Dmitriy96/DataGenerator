package by.bsuir.diplom;

import by.bsuir.diplom.helpers.PostParsingProcessing;
import by.bsuir.diplom.parser.DatabaseDescriptionFileParser;

public class FillDatabase {
    public static void main(String[] args) throws Exception {
//        try {
//            DatabaseDescriptionFileParser.parse("database_description.xml");
//            PostParsingProcessing.saveEntities("hibernate.properties");
//        } catch (Exception e) {
//            System.out.println("Something goes wrong.");
//        }
        DatabaseDescriptionFileParser.parse("database_description.xml");
        PostParsingProcessing.saveEntities("hibernate.properties");
    }
}
