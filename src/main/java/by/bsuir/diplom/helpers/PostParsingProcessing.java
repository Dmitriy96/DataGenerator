package by.bsuir.diplom.helpers;

import by.bsuir.diplom.model.EntityDescriptor;
import by.bsuir.diplom.model.PropertyDescriptor;
import by.bsuir.diplom.model.SeriesDescriptor;
import by.bsuir.diplom.model.parsed.ParsedEntities;
import by.bsuir.diplom.model.parsed.ParsedSeries;
import by.bsuir.diplom.model.parsed.ParsedVariables;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.*;

public class PostParsingProcessing {

    private static Map<String, Class<?>> pathToClassMap = new HashMap<String, Class<?>>();

    public static void saveEntities(String hibernateConfigFilePath) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(hibernateConfigFilePath));

        Configuration configuration = new Configuration().addProperties(properties);

        for (SeriesDescriptor seriesDescriptor : ParsedSeries.getSeriesDescriptors()) {
            for (Class<?> entityClass : getAllEntityClasses(seriesDescriptor.getEntityDescriptors())) {
                configuration.addAnnotatedClass(entityClass);
            }
        }

        for (Class<?> entityClass : getAllEntityClasses(ParsedEntities.getEntityDescriptors())) {
            configuration.addAnnotatedClass(entityClass);
        }
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();

        saveSeries(session);
        saveEntities(session);

        session.close();
        sessionFactory.close();

        System.out.println("saved!!!");
    }

    private static void saveSeries(Session session) throws Exception {
        for (SeriesDescriptor seriesDescriptor : ParsedSeries.getSeriesDescriptors()) {
            for (int i = seriesDescriptor.getFrom(); i < seriesDescriptor.getFrom() + seriesDescriptor.getCount(); i++) {
                for (EntityDescriptor entityDescriptor : seriesDescriptor.getEntityDescriptors()) {
                    Class<?> entityClass = pathToClassMap.get(entityDescriptor.getEntityClass());

                    Object instance = entityClass.newInstance();
                    for (PropertyDescriptor propertyDescriptor : entityDescriptor.getPropertyDescriptors()) {
                        setInSeries(instance, propertyDescriptor.getPropertyName(),
                                propertyDescriptor.getPropertyValue(), seriesDescriptor.getVariable(), i);
                    }

                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        session.save(instance);
                        tx.commit();
                    } catch (HibernateException e) {
                        if (tx != null) tx.rollback();
                        e.printStackTrace();
                    }
                }
            }
        }
    }








    private static void saveEntities(Session session) throws Exception {
        for (EntityDescriptor entityDescriptor : ParsedEntities.getEntityDescriptors()) {
            Class<?> entityClass = pathToClassMap.get(entityDescriptor.getEntityClass());

            Object instance = entityClass.newInstance();
            for (PropertyDescriptor propertyDescriptor : entityDescriptor.getPropertyDescriptors()) {
                set(instance, propertyDescriptor.getPropertyName(), propertyDescriptor.getPropertyValue());
            }

            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(instance);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            }
        }
    }

    private static List<Class<?>> getAllEntityClasses(List<EntityDescriptor> entityDescriptors) throws Exception {
        List<Class<?>> resultClasses = new ArrayList<Class<?>>();
        for (EntityDescriptor entityDescriptor : entityDescriptors) {
            String entityJavaFilePath = entityDescriptor.getEntityClass();
            Class<?> entityClass = DynamicCompilation.getClassByJavaEntityFilePath(entityJavaFilePath);
            pathToClassMap.put(entityJavaFilePath, entityClass);
            resultClasses.add(entityClass);
        }
        return resultClasses;
    }

    private static boolean set(Object object, String fieldName, String fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, ObjectConverter.convert(generatePropertyFromVariables(fieldValue), field.getType()));
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    private static boolean setInSeries(Object object, String fieldName, String fieldValue, String cycleVariableName, Integer cycleVariableValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, ObjectConverter.convert(generatePropertyFromCycleVariable(fieldValue, cycleVariableName, cycleVariableValue), field.getType()));
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    private static String generatePropertyFromVariables(String propertyValue) {
         if (propertyValue.contains("${") && propertyValue.contains("}")) {
             String propertyValueFirstPart = propertyValue.substring(0, propertyValue.indexOf("${"));
             String propertyValueSecondPart = propertyValue.substring(propertyValue.indexOf("}") + 1);
             String propertyVariableName = propertyValue.substring(propertyValue.indexOf("${") + 2, propertyValue.indexOf("}"));
             String variableValue = ParsedVariables.getVariableDescriptor(propertyVariableName) != null ? ParsedVariables.getVariableDescriptor(propertyVariableName).getValue() : "";
             return propertyValueFirstPart + variableValue + propertyValueSecondPart;
         }
         return propertyValue;
    }

    private static String generatePropertyFromCycleVariable(String propertyValue, String cycleVariableName, Integer cycleVariableValue) {
        if (propertyValue.contains("${") && propertyValue.contains("}")) {
            String propertyValueFirstPart = propertyValue.substring(0, propertyValue.indexOf("${"));
            String propertyValueSecondPart = propertyValue.substring(propertyValue.indexOf("}") + 1);
            String propertyVariableName = propertyValue.substring(propertyValue.indexOf("${") + 2, propertyValue.indexOf("}"));
            String variableValue = "";
            if (propertyVariableName.equals(cycleVariableName)) {
                variableValue = cycleVariableValue.toString();
            } else {
                variableValue = ParsedVariables.getVariableDescriptor(propertyVariableName) != null ? ParsedVariables.getVariableDescriptor(propertyVariableName).getValue() : "";
            }
            return propertyValueFirstPart + variableValue + propertyValueSecondPart;
        }
        return propertyValue;
    }

    /**
     * Usage:
     *
     * Class<?> clazz = Class.forName(className);
     * Object instance = clazz.newInstance();
     * int salary = get(instance, "salary");
     * String firstname = get(instance, "firstname");
     */
    public static <V> V get(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (V) field.get(object);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }
}
