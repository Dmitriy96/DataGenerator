package by.bsuir.diplom;

import by.bsuir.diplom.helpers.DynamicCompilation;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class TestHibernate {

    public static void main(String[] args) throws Exception {
        Class<?> entityClass = DynamicCompilation.getClassByJavaEntityFilePath("/home/dmitriy/IdeaProjects/diplom/src/main/java/by/bsuir/diplom/entities/Student.java");

//        Field[] entityFields = entityClass.getDeclaredFields();
//        for (Field field : entityFields) {
//            System.out.println("field: " + field.getName());
//        }

        Object instance = entityClass.newInstance();
        set(instance, "name", "Parampampam");

        Properties properties = new Properties();
        properties.load(new FileInputStream("hibernate.properties"));

        Configuration configuration = new Configuration();
        SessionFactory sessionFactory = configuration.addProperties(properties).addAnnotatedClass(entityClass).
                buildSessionFactory();

        Session session = sessionFactory.openSession();

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(instance);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        sessionFactory.close();

        System.out.println("saved!!!");
    }


    public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
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
