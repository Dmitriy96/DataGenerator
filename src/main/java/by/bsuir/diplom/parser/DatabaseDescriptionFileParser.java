package by.bsuir.diplom.parser;

import by.bsuir.diplom.model.parsed.ParsedEntities;
import by.bsuir.diplom.model.parsed.ParsedVariables;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class DatabaseDescriptionFileParser {

    public static void parse() throws Exception {
        File databaseDescription = new File("database_description.xml");

        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser parser = saxFactory.newSAXParser();
        CustomSAXParser customSAXParser = new CustomSAXParser();

        parser.parse(
                new SequenceInputStream(
                        Collections.enumeration(Arrays.asList(
                                new InputStream[] {
                                        new ByteArrayInputStream("<root>".getBytes()),
                                        new FileInputStream(databaseDescription),
                                        new ByteArrayInputStream("</root>".getBytes()),
                                }))
                ),
                customSAXParser
        );

        ParsedEntities.addEntityDescriptors(customSAXParser.getEntityDescriptors());
        ParsedVariables.addVariableDescriptors(customSAXParser.getVariableDescriptors());
//        System.out.println("getEntityDescriptors: " + customSAXParser.getEntityDescriptors());
//        System.out.println("getVariableDescriptors: " + customSAXParser.getVariableDescriptors());
    }
}
