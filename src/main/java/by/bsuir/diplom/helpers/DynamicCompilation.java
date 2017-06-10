package by.bsuir.diplom.helpers;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class DynamicCompilation {
    public static void main(String[] args) throws Exception {
        // create the source
        File sourceFile   = new File("/home/dmitriy/IdeaProjects/diplom/src/main/java/by/bsuir/diplom/entities/Student.java");
//        FileWriter writer = new FileWriter(sourceFile);
//
//        writer.write(
//                "public class Hello{ \n" +
//                        " public void doit() { \n" +
//                        "   System.out.println(\"Hello world\") ;\n" +
//                        " }\n" +
//                        "}"
//        );
//        writer.close();

        JavaCompiler compiler    = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(null, null, null);

        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(new File("/home/dmitriy/IdeaProjects/diplom/src/main/java/by/bsuir/diplom/entities")));
        // Compile the file
        compiler.getTask(null,
                fileManager,
                null,
                null,
                null,
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)))
                .call();
        fileManager.close();


        // delete the source file
        // sourceFile.deleteOnExit();

        runIt();
    }

    @SuppressWarnings("unchecked")
    public static void runIt() {
        try {
            Class params[] = {};
            Object paramsObj[] = {};
            Class thisClass = Class.forName("by.bsuir.diplom.entities.Student");    // parse package and class name from file
            Object iClass = thisClass.newInstance();
//            Object obj = thisClass.cast(something);
            System.out.println("iClass: " + iClass);
//            Method thisMethod = thisClass.getDeclaredMethod("doit", params);
//            thisMethod.invoke(iClass, paramsObj);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getClassByJavaEntityFilePath(String path) throws Exception {
//        File entityFile = new File(path);
        String packageName = null;
        String className = null;
        for (String line : Files.readAllLines(Paths.get(path))) {
            if (line.contains("package")) {
                String[] tokens = line.split("\\s");
                String packageToken = tokens[tokens.length - 1];
                packageName = packageToken.substring(0, packageToken.length() - 1);
            }
            if (line.contains("class")) {
                String[] tokens = line.split("\\s");
                className = tokens[tokens.length - 2];
            }
        }

        File sourceFile   = new File(path);
        JavaCompiler compiler    = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(null, null, null);

        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(new File("/home/dmitriy/IdeaProjects/diplom/src/main/java/by/bsuir/diplom/entities")));
        // Compile the file
        compiler.getTask(null,
                fileManager,
                null,
                null,
                null,
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)))
                .call();
        fileManager.close();

        return Class.forName(packageName == null ? className : packageName + "." + className);
    }
}
