package by.bsuir.diplom.model.parsed;

import by.bsuir.diplom.model.SeriesDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ParsedSeries {
    private static List<SeriesDescriptor> seriesDescriptors = new ArrayList<SeriesDescriptor>();

    public static List<SeriesDescriptor> getSeriesDescriptors() {
        return seriesDescriptors;
    }

    public static void setSeriesDescriptors(List<SeriesDescriptor> seriesDescriptors) {
        ParsedSeries.seriesDescriptors = seriesDescriptors;
    }
}
