package webserver.prepareing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import property.Properties;
import property.Property;
import property.MappedService;
import property.annotations.GetMapping;
import property.annotations.PostMapping;
import property.annotations.Processor;
import property.annotations.ResponseStatus;
import utils.HTTPMethods;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationScanner {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationScanner.class);
    private static Method addProperty;
    private static Properties properties;

    public static void scan() throws Exception{
        logger.info("Annotation Scan Start");
        //모든 클래스 탐색
        File root = new File("./src/main/java/");
        List<Class> classes = findClasses(root, "");

        //propertis 클래스 가져옴
        properties = Properties.getInstance();
        Class<Properties> propertyClass = Properties.class;
        //프로퍼티를 추가하는 addProperty 메서드 가져옴
        addProperty = propertyClass.getDeclaredMethod("addProperty", Property.class, MappedService.class);
        addProperty.setAccessible(true);

        //프로퍼티 추가 시작
        addProperties(classes);

        logger.info("Annotation Scan Done");
    }


    private static void addProperties(List<Class> classes) throws Exception{
        for (Class clazz :classes){
            //Processor annotation이 붙어있는 클래스 탐색
            if (clazz.isAnnotationPresent(Processor.class)) {
                createProperty(clazz);
            }
        }
    }

    private static void createProperty(Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
                addProperty.invoke(properties, property, new MappedService(clazz.getMethod("getInstance"), method));
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                path.append(postMapping.value());

                Property property = Property.of(HTTPMethods.POST, path.toString());
                addProperty.invoke(properties, property, new MappedService(clazz.getMethod("getInstance"), method));
            }

            if (method.isAnnotationPresent(ResponseStatus.class)) {

            }
        }
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
