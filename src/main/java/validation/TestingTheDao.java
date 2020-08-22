package validation;

import dao.JpaDao;
import tables.Books;
import tables.BooksReviews;
import tables.Users;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

// The purpose of this class is just to test the Dao implementation and ensure the EntityManager is retrieving data properly as it is a WIP
// It is also a demonstration of the generic Java-reflection style coding that we want to use to achieve our vision
// Idea -   "Get the name of your table from the xml schema, then automatically handle the rest"
public class TestingTheDao {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

//        JpaDao<Books> dao = new JpaDao<Books>(Books.class);
//        JpaDao dao2 = new JpaDao(Books.class)
//        System.out.println(dao2.getType());


        // Grab this class name from the XML
        String className = "tables.Books";

        Class aClass = Class.forName(className);
        JpaDao test = new JpaDao(aClass);
        System.out.println(test.findOne(1) + "\n");
        System.out.println("Class/Table name: " + aClass.getSimpleName());
        System.out.println("Fields:");

        String[] methodNames = new String[aClass.getDeclaredFields().length];
        Field[] fields = aClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i].getName());
            String fixit = fields[i].getName();
            fixit =  fixit.toLowerCase();
            fixit = fixit.replaceFirst(String.valueOf(fixit.charAt(0)), String.valueOf(Character.toUpperCase(fixit.charAt(0))));
            methodNames[i] = "get" + fixit;
        }

        System.out.println("Generated method names:");
        for (String s : methodNames)
            System.out.println(s);



        System.out.println("\n\n\n");

        //Instantiate and populate this using entitymanager of the declaring type
        JpaDao example = new JpaDao(aClass);
        List list = example.findAll();

        for (Object user : list) {
            System.out.println(aClass.getMethod(methodNames[0]).invoke(user));
            //System.out.println(aClass.getMethod(methodNames[1]).invoke(user));
        }



        JpaDao reviews = new JpaDao(BooksReviews.class);
        System.out.println(reviews.findAll());

    }
}
