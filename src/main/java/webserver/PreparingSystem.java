package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import property.Properties;
import property.Property;
import property.RunnableMethod;
import property.annotations.GetMapping;
import property.annotations.PostMapping;
import property.annotations.Processor;
import utils.HTTPMethods;

public class PreparingSystem {
    private static final Logger logger = LoggerFactory.getLogger(PreparingSystem.class);

    public static void main(String[] args) throws Exception{
        scan();

        logger.info("WebServer Start!!");
        WebServer webServer = new WebServer();
        webServer.start(args);

    }

    private static void scan() throws Exception{
        logger.info("Annotation Scan Start");
        File root = new File("./src/main/java/");
        List<Class> classes = findClasses(root, "");

        Properties properties = Properties.getInstance();
        Class<Properties> propertyClass = Properties.class;
        Method addProperty = propertyClass.getDeclaredMethod("addProperty", Property.class, RunnableMethod.class);
        addProperty.setAccessible(true);

        for (Class clazz :classes){

            //Processor annotation이 붙어있는 클래스 탐색
            if (clazz.isAnnotationPresent(Processor.class)) {
                Processor processor = (Processor) clazz.getAnnotation(Processor.class);

                //Processor annotation이 붙어있는 클래스의 멤버 메서드 중
                //PostMapping, GetMapping이 붙어있는 메서드 탐색
                for (Method method : clazz.getDeclaredMethods()) {
                    StringBuilder path = new StringBuilder();

                    //class에 붙어있는 경로도 함께 저장
                    path.append(processor.value());

                    //http method와 url path로 Property를 만든 후 properties에 저장
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping getMapping = method.getAnnotation(GetMapping.class);
                        path.append(getMapping.value());

                        Property property = Property.of(HTTPMethods.GET, path.toString());
                        addProperty.invoke(properties, property, new RunnableMethod(clazz.getMethod("getInstance"), method));
                    } else if (method.isAnnotationPresent(PostMapping.class)) {
                        PostMapping postMapping = method.getAnnotation(PostMapping.class);
                        path.append(postMapping.value());

                        Property property = Property.of(HTTPMethods.POST, path.toString());
                        addProperty.invoke(properties, property, new RunnableMethod(clazz.getMethod("getInstance"), method));
                    }
                }
            }
        }


        logger.info("Annotation Scan Done");
    }



    /**
     * 모든 클래스 파일을 읽어서 Class객체로 변환 후 리턴
     * @param directory
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".java")) {
                String path = file.getPath().replaceAll("./src/main/java", "");
                path = path.substring(1, path.length() - 5).replace('/', '.');

                classes.add(Class.forName(path));
            }
        }
        return classes;
    }
}
