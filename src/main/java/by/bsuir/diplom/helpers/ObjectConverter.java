package by.bsuir.diplom.helpers;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ObjectConverter {
    // Init ---------------------------------------------------------------------------------------

    private static final Map<String, Method> CONVERTERS = new HashMap<String, Method>();

    static {
        // Preload converters.
        Method[] methods = ObjectConverter.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 1) {
                // Converter should accept 1 argument. This skips the convert() method.
                CONVERTERS.put(method.getParameterTypes()[0].getName() + "_"
                        + method.getReturnType().getName(), method);
            }
        }
    }

    private ObjectConverter() {
        // Utility class, hide the constructor.
    }

    // Action -------------------------------------------------------------------------------------

    /**
     * Convert the given object value to the given class.
     * @param from The object value to be converted.
     * @param to The type class which the given object should be converted to.
     * @return The converted object value.
     * @throws NullPointerException If 'to' is null.
     * @throws UnsupportedOperationException If no suitable converter can be found.
     * @throws RuntimeException If conversion failed somehow. This can be caused by at least
     * an ExceptionInInitializerError, IllegalAccessException or InvocationTargetException.
     */
    public static <T> T convert(Object from, Class<T> to) {

        // Null is just null.
        if (from == null) {
            return null;
        }

        // Can we cast? Then just do it.
        if (to.isAssignableFrom(from.getClass())) {
            return to.cast(from);
        }

        // Lookup the suitable converter.
        String converterId = from.getClass().getName() + "_" + to.getName();
        Method converter = CONVERTERS.get(converterId);
        if (converter == null) {
            throw new UnsupportedOperationException("Cannot convert from "
                    + from.getClass().getName() + " to " + to.getName()
                    + ". Requested converter does not exist.");
        }

        // Convert the value.
        try {
            return to.cast(converter.invoke(to, from));
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert from "
                    + from.getClass().getName() + " to " + to.getName()
                    + ". Conversion failed with " + e.getMessage(), e);
        }
    }

    // Converters ---------------------------------------------------------------------------------

    /**
     * Converts String to Boolean. If integer value is 0, then return FALSE, else return TRUE.
     * @param value The String to be converted.
     * @return The converted Boolean value.
     */
    public static Boolean stringToBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    /**
     * Converts String to BigDecimal.
     * @param value The String to be converted.
     * @return The converted BigDecimal value.
     */
    public static BigDecimal stringToBigDecimal(String value) {
        return new BigDecimal(value);
    }

    /**
     * Converts String to Double.
     * @param value The String to be converted.
     * @return The converted Double value.
     */
    public static Double stringToDouble(String value) {
        return Double.parseDouble(value);
    }

    /**
     * Converts String to Integer.
     * @param value The String to be converted.
     * @return The converted Integer value.
     */
    public static Integer stringToInteger(String value) {
        return Integer.parseInt(value);
    }
}
