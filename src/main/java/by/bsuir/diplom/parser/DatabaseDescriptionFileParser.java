package by.bsuir.diplom.parser;

import by.bsuir.diplom.model.parsed.ParsedEntities;
import by.bsuir.diplom.model.parsed.ParsedSeries;
import by.bsuir.diplom.model.parsed.ParsedVariables;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class DatabaseDescriptionFileParser {

    public static void parse(String databaseDesriptionFilePath) throws Exception {
        File databaseDescription = new File(databaseDesriptionFilePath);

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
        ParsedSeries.setSeriesDescriptors(customSAXParser.getSeriesDescriptors());
    }
}
