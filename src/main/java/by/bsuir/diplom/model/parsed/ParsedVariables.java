package by.bsuir.diplom.model.parsed;

import by.bsuir.diplom.model.VariableDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ParsedVariables {
    private static List<VariableDescriptor> variableDescriptors = new ArrayList<VariableDescriptor>();

    public static void addVariableDescriptors(VariableDescriptor variableDescriptor) {
        variableDescriptors.add(variableDescriptor);
    }

    public static void addVariableDescriptors(List<VariableDescriptor> newVariableDescriptors) {
        variableDescriptors.addAll(newVariableDescriptors);
    }

    public static VariableDescriptor getVariableDescriptor(String variableName) {
        for (VariableDescriptor variableDescriptor : variableDescriptors) {
            if (variableDescriptor.getName().equals(variableName)) {
                return variableDescriptor;
            }
        }
        return null;
    }
}
