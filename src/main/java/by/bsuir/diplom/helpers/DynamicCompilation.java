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

    public static Class<?> getClassByJavaEntityFilePath(String path) throws Exception {
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
        String fullClassName = packageName == null ? className : packageName + "." + className;

        Class<?> requiredClass = getClassFromClasspath(fullClassName);

        if (requiredClass == null) {
            File sourceFile   = new File(path);
            JavaCompiler compiler    = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager =
                    compiler.getStandardFileManager(null, null, null);

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                    Arrays.asList(new File("target")));
            // Compile the file
            compiler.getTask(null,
                    fileManager,
                    null,
                    null,
                    null,
                    fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile)))
                    .call();
            fileManager.close();

            requiredClass = getClassFromClasspath(fullClassName);
        }

        return requiredClass;
    }

    private static Class<?> getClassFromClasspath(String className) {
        Class<?> requiredClass = null;
        try {
            requiredClass = Class.forName(className);
        } catch(ClassNotFoundException e) {}

        return requiredClass;
    }
}
